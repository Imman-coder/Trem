package com.example.myapplication.network.model

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.util.regex.Pattern


fun main(args: Array<String>) {
    try {
        val reader = PdfReader("C:\\Users\\Ava\\AndroidStudioProjects\\MyApplication\\app\\src\\main\\res\\raw\\res.pdf")

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
                    credit = (matcher.group(4)?.let { Integer.parseInt(it) } ?: 1),
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

        val ops = ResultDto(
            cgpa,
            sems = li2
        )

        println(ops)

    } catch (e: Exception) {
        println("Error found is : \n$e")
    }

}

