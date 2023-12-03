package com.immanlv.trem.network.util

object RegexStrings {

    const val LoggedInCheckRegex = "~EST Campus~\\|DGI"
    const val DashboardSicNoExtractionRegex = "sic_number=(.{16})\""
    const val SGPAExtractionRegex = "SGPA *: *(\\d{1,2}\\.\\d{1,2}) *CGPA *: *(\\d{1,2}\\.\\d{1,2})"
    const val ScorecardLineExtractionRegex = "((\\d{1,2}) (\\w*) (\\d) ([ABCDEFO])\\n((\\w+ ){1,4}(\\S+)))"
    const val HashExtractionRegex = "(?<=do_submit\\(')(.)+(?='\\);)"
    const val AttendanceIdExtractionRegex = "(?<=name=\"hidUserName\" value=\").+(?=\">)"

    const val ProfileLoggedInCheckRegex = "~EST Campus~"
    const val NameExtractionRegex = "<td style=\"text-align: center;font-size:20px;font-weight: bold\" colspan=\"4\">(([a-zA-Z]+ )+([a-zA-Z]+)).+</td>"
    const val RegistrationNumberExtractionRegex = "<input type=\"hidden\" name=\"hidRegnNo\" id=\"hidRegnNo\" value=\"([0-9]+).*\">"
    const val PhoneNumberExtractionRegex = "<td>Regd Mobile no.</td>\\n.*<td>:</td>\\n.*<td style=\"font-weight: bold;\">([0-9]{1,12}).*</td>"
    const val EmailExtractionRegex = "(?<=Email Id</b> - ).*(?=</li>)"
    const val RollNumberExtractionRegex = "(?<=Roll No.</b> - ).*(?=</li>)"
    const val ProgramNameExtractionRegex = "<td>Course</td>\\n.*<td>:</td>\\n.*<td style=\"font-weight: bold;\">([a-zA-Z]+).*</td>"
    const val SemesterExtractionRegex = "id=\"cmbSemester\" value=\"([0-8])\""
    const val BranchExtractionRegex = "<td style=\"width: 27%;font-weight: bold;font-size: 16px;\">Branch</td>\\n.*<td>:</td>\\n.*<td><span style=\"font-size: 18px;\">([a-zA-Z]+).*</span></td>"
    const val SectionExtractionRegex = "(?<=Section</b> - )[A-Z](?=</li)"
    const val BatchExtractionRegex = "<td style=\"width: 27%;font-weight: bold;font-size: 16px;\">Batch</td>\\n.*<td>:</td>\\n.*<td><span style=\"font-size: 18px;\">([0-9]+-[0-9]+)</span></td>"
    const val SicNoExtractionRegex = "href=\"\\.\\.\\/student_admin\\/student_info\\.php\\?role_code=M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09&sic_number=(.+)\" >More Info <i class"
//    const val SicNoExtractionRegex = "<td style=\"width: 21%;font-weight: bold;font-size: 16px;\">Sic. No</td>\\n.*<td>:</td>\\n.*<td style=\"font-size: 18px;\">([0-9]+)</td>"
}
