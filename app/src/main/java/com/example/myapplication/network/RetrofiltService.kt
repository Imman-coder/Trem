package com.example.myapplication.network

import com.example.myapplication.network.model.AttendanceDto
import com.example.myapplication.network.model.ResultDto
import com.example.myapplication.network.model.TimetableDto
import com.example.myapplication.network.response.HashCarrier
import com.example.myapplication.network.response.ProfileCarrier
import com.example.myapplication.network.response.UserSta
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileService {


    @GET("index.php")
    suspend fun getHash(
    ): HashCarrier


    @GET("academics/student_registered_subjects.php?role_code=M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09")
    suspend fun getUserSta(
    ): UserSta

    @GET("academics/db_student_academic.php")

    suspend fun getAttendence(
        @Query("semester") sem :String,
        @Query("s_c") studentCode :String,
        @Query("oper") oper :String="SELECT_REGD_SUBJECT",
        @Query("role_code") rolecode :String="M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09",
        @Header("Referer") referer :String = "https://driems.online/academics/student_registered_subjects.php?role_code=M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09",

    ): AttendanceDto


    @FormUrlEncoded
    @POST("index.php")
    suspend fun login(
        @Field("cmbInstitute") cmbInstitute: String,
        @Field("key") key: String,
        @Field("password") password: String,
        @Field("shapassword") shapassword: String,
        @Field("username") username: String
    ): ProfileCarrier


    @GET("autonomous_exam/exam_result_db.php")
    suspend fun getResults(
        @Query("role_code") rolecode :String="M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09",
        @Query("type") type :String="PRINT",
        @Query("oper") oper :String="SHOW_SEMESTER_RESULT",
        @Header("Referer") referer :String = "https://driems.online/autonomous_exam/exam_result.php?role_code=M1Z5SEVJM2dub0NWWE5GZy82dHh2QT09",

    ):ResultDto

}

interface DataService{
    @GET("{fn}")
    suspend fun getTimetable(
        @Path("fn") filename:String
    ):TimetableDto
}