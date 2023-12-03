package com.immanlv.trem.network.util

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
import com.immanlv.trem.network.model.CourseCoverageDetailsDto
import com.immanlv.trem.network.model.CoverageDetailDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkService {

    @GET("index.php")
    suspend fun getHash(
    ): HashResult

    @GET("academics/student_registered_subjects.php?role_code=M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09")
    suspend fun getUserSta(
    ): UserStaResult

    @GET("academics/db_student_academic.php")
    suspend fun getAttendence(
        @Query("semester") sem :String,
        @Query("s_c") studentCode :String,
        @Query("oper") oper :String="SELECT_REGD_SUBJECT",
        @Query("role_code") rolecode :String="M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09",
        @Header("Referer") referer :String = "https://driems.online/academics/student_registered_subjects.php?role_code=M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09",

        ): AttendanceResult


    @FormUrlEncoded
    @POST("index.php")
    suspend fun login(
        @Field("cmbInstitute") cmbInstitute: String,
        @Field("key") key: String,
        @Field("password") password: String,
        @Field("shapassword") shapassword: String,
        @Field("username") username: String
    ): LoginResult



    @GET("dashboard/student_dashboard.php?role_code=M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09")
    suspend fun getSic(
    ): SicResult

    @GET("student_admin/student_info.php?role_code=M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09")
    suspend fun getDetails(
        @Query("sic_number") sic:String
    ): ProfileResult

    @GET("autonomous_exam/exam_result_db.php")
    suspend fun getResults(
        @Query("role_code") rolecode :String="M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09",
        @Query("type") type :String="PRINT",
        @Query("oper") oper :String="SHOW_SEMESTER_RESULT",
        @Header("Referer") referer :String = "https://driems.online/autonomous_exam/exam_result.php?role_code=M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09",
        ): ScorecardResult

    @GET("uploads/student_photo/DGI_{program}_{batch}/{branch}/{plink}/{plink}_128X128.jpg")
    suspend fun getProfilePicture(
        @Path("program") program:String,
        @Path("batch") batch:String,
        @Path("branch") branch:String,
        @Path("plink") slno:Long,
    ): ImageDataResult

    @GET("signout.php?role_code=M1Z5SEVJM2dub0NWWE5GZy82dHh2QT0")
    suspend fun logout()

    @GET("{url}")
    suspend fun getProfilePicture(
        @Path("url") url:String
    ): ImageDataResult

    @GET("academics/course_coverage_details.php?role_code=M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09")
    suspend fun getCourseCoverage(
        @Query("paper_code") paperCode:String,
        @Query("semester_code") semesterCode:String,
        @Query("branch_code") branchCode:String,
        @Query("section_code") sectionCode:String,
        @Query("student_code") studentCode: String,
        @Query("batch_code") batchCode:String,

    ): CourseCoverageDetailResult
}

interface DataService{
    @GET("DGI_{program}_{batch}/{branch}/{section}.json")
    suspend fun getTimetable(
        @Path("program") program: String,
        @Path("batch") batch: String,
        @Path("branch") branch: String,
        @Path("section") section: Char
    ):TimetableResult

    @GET("testTable.json")
    suspend fun getTestTimetable():TimetableResult
}