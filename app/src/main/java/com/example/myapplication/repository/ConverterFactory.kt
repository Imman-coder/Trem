package com.example.myapplication.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.network.model.AttendanceDto
import com.example.myapplication.network.model.ProfileDto
import com.example.myapplication.network.model.ResultDto
import com.example.myapplication.network.model.ResultSubjectDto
import com.example.myapplication.network.model.SemDto
import com.example.myapplication.network.model.TimetableDto
import com.example.myapplication.network.response.FetchType
import com.example.myapplication.network.response.HashCarrier
import com.example.myapplication.network.response.ProfileCarrier
import com.example.myapplication.network.response.UserSta
import com.example.myapplication.presentation.navigation.login.LoginViewModel
import com.google.gson.Gson
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import kotlinx.serialization.Serializable
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.Arrays
import java.util.Objects
import java.util.regex.Pattern
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists

class ConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if (String::class.java == type) {
            return Converter<ResponseBody, String?> { r -> r.string() }
        } else if (HashCarrier::class.java == type) {
            return parseHash()
        } else if (ProfileCarrier::class.java == type) {
            return parseProfile()
        } else if (UserSta::class.java == type) {
            return parseLoginStatus()
        } else if (AttendanceDto::class.java == type) {
            return parseAttendence()
        } else if (ResultDto::class.java == type) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return parseResult()
            }
        }else if(TimetableDto::class.java==type){
            return parseTimetable()
        }
        return null
    }

    private fun parseTimetable(): Converter<ResponseBody,TimetableDto>{
        return Converter {
            value:ResponseBody ->
            val s = value.string()
            val gson = Gson()
            gson.fromJson(s, TimetableDto::class.java)

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun parseResult(): Converter<ResponseBody, ResultDto> {
        return Converter { value: ResponseBody ->

            var r = ResultDto()
            try {
                val tempFile = Files.createTempFile(null, null)
                Files.write(tempFile, value.bytes())
                val content = Arrays.asList("Line 1", "Line 2", "Line 3")
                Files.write(tempFile, content, StandardOpenOption.APPEND)

                try {
                    val reader = PdfReader(tempFile.toString())

                    val n = reader.numberOfPages
                    val sregex = "SGPA *: *(\\d{1,2}\\.\\d{1,2}) *CGPA *: *(\\d{1,2}\\.\\d{1,2})"
                    val regex = "((\\d{1,2}) (\\w*) (\\d) ([ABCDEFO])\\n((\\w+ ){1,4}(\\S+)))"
                    var cgpa = 0.0f

                    val li2 = mutableListOf<SemDto>()

                    for (i in 1..n) {
                        var extractedText = ""
                        val pattern = Pattern.compile(regex)
                        val pattern2 = Pattern.compile(sregex)
                        extractedText = """$extractedText${PdfTextExtractor.getTextFromPage(reader, i).trim { it <= ' ' }}""".trimIndent()

                        val matcher = pattern.matcher(extractedText)
                        val li = mutableListOf<ResultSubjectDto>()
                        while (matcher.find()) {
                            li += ResultSubjectDto(
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

                    r = ResultDto(
                        cgpa,
                        sems = li2
                    )
                    println(r)
                    tempFile.deleteIfExists()

                } catch (e: Exception) {
                    tempFile.deleteIfExists()
                    println("Error found is : \n$e")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return@Converter r
        }
    }

    private fun parseHash(): Converter<ResponseBody, HashCarrier> {
        return Converter { value: ResponseBody ->
            val t = value.string()

            val regexe = "~EST Campus~\\|DGI"
            val patterne = Pattern.compile(regexe, Pattern.MULTILINE)
            val matchere = patterne.matcher(t)
            if(matchere.find()) throw LoginException("Already Logged In","",LoginException.Error.AlreadyLoggedIn)

            val regex = "(?<=do_submit\\(')(.)+(?='\\);)"
            val pattern = Pattern.compile(regex, Pattern.MULTILINE)
            val matcher = pattern.matcher(t)
            if (matcher.find()) {
                return@Converter HashCarrier(
                    FetchType.Successful,
                    Objects.requireNonNull<String>(matcher.group(0))
                )
            }
            HashCarrier()
        }
    }

    private fun parseAttendence(): Converter<ResponseBody, AttendanceDto> {
        return Converter { value1: ResponseBody ->
            val s = value1.string()
            println(s)
            val gson = Gson()
            gson.fromJson(s, AttendanceDto::class.java)
        }
    }

    private fun parseLoginStatus(): Converter<ResponseBody, UserSta> {
        return Converter { value: ResponseBody ->
            val regex = "(?<=name=\"hidUserName\" value=\").+(?=\">)"
            val pattern = Pattern.compile(regex, Pattern.MULTILINE)
            val matcher = pattern.matcher(value.string())
            if (matcher.find()) {
                return@Converter UserSta(FetchType.Successful, matcher.group(0))
            }
            UserSta()
        }
    }

    private fun parseProfile(): Converter<ResponseBody, ProfileCarrier?> {
        return Converter { value: ResponseBody ->
            val res = value.string()
            val regex = "~EST Campus~\\|DGI"
            val nameregex = "(?<=Name</b> - ).+(?=  </li>)"
            val redgregex = "(?<=student_code=)\\d+(?=\" *>ADHOC)"
            val phoneregex = "(?<=Mobile No.</b> - ).*(?=</li>)"
            val emailregex = "(?<=Email Id</b> - ).*(?=</li>)"
            val rollregex = "(?<=Roll No.</b> - ).*(?=</li>)"
            val programcatregex = "(?<=Program Category</b> - ).*(?= , )"
            val semregex = "(?<=Semester</b> - )\\d(?=</li>)"
            val branchregex = "(?<=Program </b> - ).*(?= ,)"
            val sectionregex = "(?<=Section</b> - )[A-Z](?=</li)"
            var pattern = Pattern.compile(regex, Pattern.MULTILINE)
            var matcher = pattern.matcher(res)
            if (matcher.find()) {
                var name: String? = ""
                pattern = Pattern.compile(nameregex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) name = matcher.group(0)
                
                var reedgno: Long = -1
                pattern = Pattern.compile(redgregex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) reedgno = Objects.requireNonNull(matcher.group(0)).toLong()
                
                var phone: Long = -1
                pattern = Pattern.compile(phoneregex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) phone = Objects.requireNonNull(matcher.group(0)).toLong()
                
                var sem = -1
                pattern = Pattern.compile(semregex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) sem = Objects.requireNonNull(matcher.group(0)).toInt()
                
                var program: String? = ""
                pattern = Pattern.compile(programcatregex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) program = matcher.group(0)
                
                var branch:String? =""
                pattern = Pattern.compile(branchregex, Pattern.MULTILINE)
                matcher = pattern.matcher(res)
                if (matcher.find()) branch = matcher.group(0)

                
                assert(name != null)
                assert(program != null)

                return@Converter ProfileCarrier(
                    FetchType.Successful,
                    ProfileDto(
                        name!!,
                        reedgno,
                        phone,
                        sem,
                        program!!,
                        branch!!,
                        null,
                        null
                    )
                )
            }
            ProfileCarrier(FetchType.Unsuccessful, null)
        }
    }
}

@Serializable
data class EventTable(
    val EventTable: List<List<Int>>,
    val TimeList: List<Int>,
    val EventList: Map<String, Event>
)

@Serializable
data class Event(
    val time_span: Int,
    val subjects: List<Subject>,
    val class_type: Int
)

@Serializable
data class Subject(
    val subject: String,
    val subject_code: String,
    val teacher: String
)