package com.immanlv.trem.network.util

import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.immanlv.trem.data.util.ExtrasString
import com.immanlv.trem.domain.model.CourseCoverageDetails
import com.immanlv.trem.network.mapped.AttendanceResult
import com.immanlv.trem.network.mapped.CourseCoverageDetailResult
import com.immanlv.trem.network.mapped.HashResult
import com.immanlv.trem.network.mapped.ImageDataResult
import com.immanlv.trem.network.mapped.LoginResult
import com.immanlv.trem.network.mapped.ProfileResult
import com.immanlv.trem.network.mapped.ScorecardResult
import com.immanlv.trem.network.mapped.SicResult
import com.immanlv.trem.network.mapped.TimetableResult
import com.immanlv.trem.network.mapped.UserStaResult
import com.immanlv.trem.network.model.AttendanceDto
import com.immanlv.trem.network.model.CourseCoverageDetailsDto
import com.immanlv.trem.network.model.CoverageDetailDto
import com.immanlv.trem.network.model.ProfileDto
import com.immanlv.trem.network.model.ScorecardDto
import com.immanlv.trem.network.model.ScorecardSubjectDto
import com.immanlv.trem.network.model.SemDto
import com.immanlv.trem.network.model.TimetableDto
import com.immanlv.trem.network.util.RegexStrings.AttendanceIdExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.BatchExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.BranchExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.DashboardSicNoExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.HashExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.LoggedInCheckRegex
import com.immanlv.trem.network.util.RegexStrings.NameExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.PhoneNumberExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.ProfileLoggedInCheckRegex
import com.immanlv.trem.network.util.RegexStrings.ProgramNameExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.RegistrationNumberExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.SGPAExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.ScorecardLineExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.SemesterExtractionRegex
import com.immanlv.trem.network.util.RegexStrings.SicNoExtractionRegex
import com.immanlv.trem.network.util.exception.LoginException
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.Objects
import java.util.regex.Pattern
import kotlin.io.path.deleteIfExists

class ConverterFactory : Converter.Factory() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if (String::class.java == type) {
            return Converter<ResponseBody, String?> { r -> r.string() }
        } else if (HashResult::class.java == type) {
            return parseHash()
        } else if (LoginResult::class.java == type) {
            return getLoginStatus()
        } else if (SicResult::class.java == type) {
            return getSic()
        } else if (ProfileResult::class.java == type) {
            return parseProfileJsoup()
        } else if (UserStaResult::class.java == type) {
            return parseLoginStatus()
        } else if (AttendanceResult::class.java == type) {
            return parseAttendance()
        } else if (ScorecardResult::class.java == type) {
            return parseResult()
        } else if (TimetableResult::class.java == type) {
            return parseTimetable()
        } else if (ImageDataResult::class.java == type) {
            return getImageData()
        } else if (CourseCoverageDetailResult::class.java == type) {
            return parseCourseCoverage()
        }
        return null
    }

    private fun parseTimetable(): Converter<ResponseBody, TimetableResult> {
        return Converter { value: ResponseBody ->
            val s = value.string()
            Log.d("TAG", "parseTimetable: UnParsed : $s")
            val gson = Gson()
            val v = gson.fromJson(s, TimetableDto::class.java)
            Log.d("TAG", "parseTimetable: Parsed : $v")
            TimetableResult.Success(v)
        }
    }

    private fun parseCourseCoverage(): Converter<ResponseBody, CourseCoverageDetailResult> {
        return Converter { value: ResponseBody ->
            val s = value.string()

            val jsoup = Jsoup.parse(s)
            val cols = jsoup.select("table>tbody>tr")

            val li = mutableListOf<CoverageDetailDto>()
            cols.forEach {
                val ss = it.select("td")
                li += CoverageDetailDto(
                    sl = ss[0].text().toInt(),
                    date = ss[1].text(),
                    time = ss[2].text(),
                    topic = ss[3].text(),
                    roomNo = ss[4].text(),
                    status = ss[5].text(),
                )
            }

            CourseCoverageDetailResult.Success(CourseCoverageDetailsDto(li))
        }
    }

    private fun getImageData(): Converter<ResponseBody, ImageDataResult> {
        Log.d("TAG", "getImageData: Fetched data, decoding")
        return Converter { value: ResponseBody ->
            val bytestream = value.byteStream()
            val stream = ImageUtils.bitmapToBase64(BitmapFactory.decodeStream(bytestream))
            ImageDataResult.Success(stream)
        }
    }

    private fun getLoginStatus(): Converter<ResponseBody, LoginResult> {
        return Converter { value: ResponseBody ->
            val m = value.string()
            val pattern = Pattern.compile(LoggedInCheckRegex, Pattern.MULTILINE)
            val matcher = pattern.matcher(m)
            if (matcher.find())
                return@Converter LoginResult.Success(true)
            LoginResult.Failed(LoginException("Invalid Credentials",LoginException.Type.InvalidCredentials))
        }
    }

    private fun getSic(): Converter<ResponseBody, SicResult> {
        return Converter { value: ResponseBody ->
            val str = value.string()
            val pattern = Pattern.compile(DashboardSicNoExtractionRegex, Pattern.MULTILINE)
            val matcher = pattern.matcher(str)
            var sic: String? = ""
            if (matcher.find()) sic = matcher.group(1)?.trim()
            try {
                SicResult.Success(sic!!)
            } catch (e: Exception) {
                SicResult.Failed(
                    LoginException(
                        "Failed to get Sic no.",
                        LoginException.Type.FailedToGetHash
                    )
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseResult(): Converter<ResponseBody, ScorecardResult> {
        return Converter { value: ResponseBody ->

            val r: ScorecardDto
            try {
                val tempFile = Files.createTempFile(null, null)
                Files.write(tempFile, value.bytes())
                val content = listOf("Line 1", "Line 2", "Line 3")
                Files.write(tempFile, content, StandardOpenOption.APPEND)

                try {
                    val reader = PdfReader(tempFile.toString())

                    val n = reader.numberOfPages
                    var cgpa = 0.0f

                    val li2 = mutableListOf<SemDto>()

                    for (i in 1..n) {
                        var extractedText = ""
                        val pattern = Pattern.compile(ScorecardLineExtractionRegex)
                        val pattern2 = Pattern.compile(SGPAExtractionRegex)
                        extractedText = """$extractedText${
                            PdfTextExtractor.getTextFromPage(reader, i).trim { it <= ' ' }
                        }""".trimIndent()

                        val matcher = pattern.matcher(extractedText)
                        val li = mutableListOf<ScorecardSubjectDto>()
                        while (matcher.find()) {
                            li += ScorecardSubjectDto(
                                name = matcher.group(6) ?: "",
                                code = matcher.group(3) ?: "",
                                credit = matcher.group(4)?.let { Integer.parseInt(it) } ?: 0,
                                grade = matcher.group(5)?.get(0) ?: ' '
                            )
                        }
                        val matcher2 = pattern2.matcher(extractedText)
                        matcher2.find()
                        li2 +=
                            SemDto(
                                sem = i,
                                sgpa = matcher2.group(2)?.toFloat() ?: .0f,
                                subjects = li
                            )

                        cgpa = matcher2.group(2)?.toFloat() ?: cgpa

                    }
                    reader.close()

                    r = ScorecardDto(
                        cgpa,
                        sems = li2
                    )
                    println(r)
                    tempFile.deleteIfExists()

                } catch (e: Exception) {
                    tempFile.deleteIfExists()
                    return@Converter ScorecardResult.Failed(e)
                }
            } catch (e: IOException) {
                return@Converter ScorecardResult.Failed(e)
            }
            return@Converter ScorecardResult.Success(r)
        }
    }

    private fun parseHash(): Converter<ResponseBody, HashResult> {
        return Converter { value: ResponseBody ->
            val t = value.string()

            val patterne = Pattern.compile(LoggedInCheckRegex, Pattern.MULTILINE)
            val matchere = patterne.matcher(t)
            if (matchere.find())
                return@Converter HashResult.Failed(
                    LoginException(
                        "Already logged in",
                        LoginException.Type.AlreadyLoggedIn
                    )
                )

            val pattern = Pattern.compile(HashExtractionRegex, Pattern.MULTILINE)
            val matcher = pattern.matcher(t)
            if (matcher.find()) {
                val hash = matcher.group(0)
                Log.d("TAG", "parseHash: $hash")
                return@Converter HashResult.Success(Objects.requireNonNull<String>(hash))
            }

            TODO("Unknown Exception")
        }
    }

    private fun parseAttendance(): Converter<ResponseBody, AttendanceResult> {
        return Converter { value1: ResponseBody ->
            val s = value1.string()
            val gson = Gson()
            try {
                AttendanceResult.Success(gson.fromJson(s, AttendanceDto::class.java))
            } catch (e: Exception) {
                AttendanceResult.Failed(e)
            }

        }
    }

    private fun parseLoginStatus(): Converter<ResponseBody, UserStaResult> {
        return Converter { value: ResponseBody ->
            val m = value.string()
            val pattern = Pattern.compile(LoggedInCheckRegex, Pattern.MULTILINE)
            val matcher = pattern.matcher(m)
            if (matcher.find()) {
                Log.d("TAG", "parseLoginStatus: Login Status Succeed")
                val pattern1 = Pattern.compile(AttendanceIdExtractionRegex, Pattern.MULTILINE)
                val matcher1 = pattern1.matcher(m)
                if(matcher1.find())
                    return@Converter matcher1.group(0)?.let { UserStaResult.Success(it) }
            }
            Log.d("TAG", "parseLoginStatus: Login Status Failed")
            return@Converter UserStaResult.Failed
        }
    }

    private fun parseProfile(): Converter<ResponseBody, ProfileResult> {
        return Converter { value: ResponseBody ->
            val p = value
            val res = p.string()

//            V1 -> regexx for later use.
//            val regex = "~EST Campus~\\|DGI"
//            val nameregex = "(?<=Name</b> - ).+(?=  </li>)"
//            val phoneregex = "(?<=Mobile No.</b> - ).*(?=</li>)"
//            val programcatregex = "(?<=Program Category</b> - ).*(?= , )"
//            val redgregex = "(?<=student_code=)\\d+(?=\" *>ADHOC)"
//            val semregex = "(?<=Semester</b> - )\\d(?=</li>)"

//            V2
//            val nameregex = "<li><b>Name<\\/b>[\\-|\\s+]+((([a-zA-Z]+)|\\s)+((([a-zA-Z]+))+))[\\-|\\s+]+<\\/li>"
//            val phoneregex = "<li><b>Mobile No.<\\/b>.*([0-9]{10,12})<\\/li>"
//            val programcatregex = "<li><b>Program Category<\\/b>[\\-|\\s+]+([a-zA-Z]+)\\s,"
//            val semregex = ", <b>Semester<\\/b>[\\-|\\s+]*([0-8])[\\-|\\s+]*<\\/li>"

            var pattern = Pattern.compile(ProfileLoggedInCheckRegex, Pattern.MULTILINE)
            var matcher = pattern.matcher(res)
            if (matcher.find()) {
                var name = ""
                pattern = Pattern.compile(NameExtractionRegex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) name = matcher.group(1)?.trim().toString()

                var redgno: Long = -1
                pattern = Pattern.compile(RegistrationNumberExtractionRegex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) redgno = Objects.requireNonNull(matcher.group(1)).toLong()

                var phone: Long = -1
                pattern = Pattern.compile(PhoneNumberExtractionRegex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) phone =
                    Objects.requireNonNull(matcher.group(1)?.trim() ?: "-1").toLong()

                var sem = -1
                pattern = Pattern.compile(SemesterExtractionRegex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) sem = Objects.requireNonNull(matcher.group(1)).toInt()

                var program = ""
                pattern = Pattern.compile(ProgramNameExtractionRegex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) program = matcher.group(1)?.toString() ?: ""

                var branch = ""
                pattern = Pattern.compile(BranchExtractionRegex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) branch = matcher.group(1)?.toString() ?: ""

                var batch = ""
                pattern = Pattern.compile(BatchExtractionRegex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) batch = matcher.group(1)?.toString() ?: ""


                var sicno: Long = 0
                pattern = Pattern.compile(SicNoExtractionRegex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) {
                    sicno = matcher.group(1)?.toLong() ?: 0

                }


                return@Converter ProfileResult.Success(
                    ProfileDto(
                        name = name,
                        redgno = redgno,
                        phoneno = phone,
                        sem = sem,
                        program = program,
                        branch = branch,
                        batch = batch,
                        sicno = sicno,
                    )
                )
            }
            TODO("Unknown Exception")
            ProfileResult.Failed(Exception())
        }
    }

    private fun parseProfileJsoup(): Converter<ResponseBody, ProfileResult> {
        return Converter { value: ResponseBody ->
            val p = value
            val res = p.string()

            println(res)

            val jsoup = Jsoup.parse(res)
            var name = jsoup.selectXpath(ScrappingStrings.NameXPath).text()
            var regdno = jsoup.selectXpath(ScrappingStrings.RegistrationNumberXPath).text()
            var sicno = jsoup.selectXpath(ScrappingStrings.SicNumberXPath).text()
            var mobno = jsoup.selectXpath(ScrappingStrings.MobileNumberXPath).text()
            var program = jsoup.selectXpath(ScrappingStrings.ProgramXPath).text()
            var semester = jsoup.selectXpath(ScrappingStrings.SemesterXPath).text()
            var branch = jsoup.selectXpath(ScrappingStrings.BranchXPath).text()
            var batch = jsoup.selectXpath(ScrappingStrings.BatchXPath).text()
            var profilePicture = jsoup.selectXpath(ScrappingStrings.ProfilePictureXPath).attr("src") //15

            if(name.isEmpty()) name = jsoup.selectXpath(ScrappingStrings.NameXPath2).text()
            if(regdno.isEmpty()) regdno = jsoup.selectXpath(ScrappingStrings.RegistrationNumberXPath2).text()
            if(sicno.isEmpty()) sicno = jsoup.selectXpath(ScrappingStrings.SicNumberXPath2).text()
            if(mobno.isEmpty()) mobno = jsoup.selectXpath(ScrappingStrings.MobileNumberXPath2).text()
            if(program.isEmpty()) program = jsoup.selectXpath(ScrappingStrings.ProgramXPath2).text()
            if(semester.isEmpty()) semester = jsoup.selectXpath(ScrappingStrings.SemesterXPath2).text()
            if(branch.isEmpty()) branch = jsoup.selectXpath(ScrappingStrings.BranchXPath2).text()
            if(batch.isEmpty()) batch = jsoup.selectXpath(ScrappingStrings.BatchXPath2).text()
            if(profilePicture.isEmpty()) profilePicture = jsoup.selectXpath(ScrappingStrings.ProfilePictureXPath2).attr("src")

            if(profilePicture[0] == '/') profilePicture = profilePicture.substring(1)

            return@Converter ProfileResult.Success(
                ProfileDto(
                    name = name,
                    redgno = regdno.toLong(),
                    phoneno = mobno.toLong(),
                    sem = semester.toInt(),
                    program = program,
                    branch = branch,
                    batch = batch,
                    sicno = sicno.toLong(),
                    extras = mapOf(ExtrasString.PROFILEPICTURE_URL to profilePicture)
                )
            )
        }
    }

}

