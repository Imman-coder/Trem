package com.example.myapplication.network.model

import com.example.myapplication.network.util.TimetableDtoMapper
import com.google.gson.Gson


fun main(args: Array<String>) {
    val ts = "{\"EventTable\":[[1,2,3,4,5,6],[5,4,7,8,3,1,9,10],[4,8,7,1,3,11],[5,9,4,12,3,1,6,10],[1,13,3,8,15,5],[15,5,6,8,3]],\"TimeList\":[555,610,665,720,775,825,875,925,975],\"EventList\":{\"1\":{\"time_span\":1,\"subjects\":[{\"subject\":\"Design Analysis Algorithm\",\"subject_code\":\"\",\"teacher\":\"Sikheresh Barik\"}],\"class_type\":0},\"2\":{\"time_span\":\"3\",\"subjects\":[{\"subject\":\"Design Analysis Algorithm\",\"subject_code\":\"\",\"teacher\":\"Sikheresh Barik / S G\"}],\"class_type\":\"1\"},\"3\":{\"time_span\":1,\"subjects\":[{\"subject\":\"Lunch Break\",\"subject_code\":\"\",\"teacher\":\"\"}],\"class_type\":\"2\"},\"4\":{\"time_span\":1,\"subjects\":[{\"subject\":\"Java Programming\",\"subject_code\":\"\",\"teacher\":\"Mousami Acharya\"}],\"class_type\":0},\"5\":{\"time_span\":1,\"subjects\":[{\"subject\":\"Theory Of Computation\",\"subject_code\":\"\",\"teacher\":\"Sudeep Gochayat\"}],\"class_type\":0},\"6\":{\"time_span\":1,\"subjects\":[{\"subject\":\"Organizational Behavior\",\"subject_code\":\"\",\"teacher\":\"Sushant Kuma N\"}],\"class_type\":0},\"7\":{\"time_span\":1,\"subjects\":[{\"subject\":\"Discrete Structure\",\"subject_code\":\"\",\"teacher\":\"Ranjak Kumar Jati\"}],\"class_type\":0},\"8\":{\"time_span\":1,\"subjects\":[{\"subject\":\"Computer Architecture Organization\",\"subject_code\":\"\",\"teacher\":\"S T\"}],\"class_type\":0},\"9\":{\"time_span\":1,\"subjects\":[{\"subject\":\"Soft Skill\",\"subject_code\":\"\",\"teacher\":\"Mamta Banarjee\"}],\"class_type\":0},\"10\":{\"time_span\":1,\"subjects\":[{\"subject\":\"Library\",\"subject_code\":\"\",\"teacher\":\"\"}],\"class_type\":\"2\"},\"11\":{\"time_span\":\"3\",\"subjects\":[{\"subject\":\"Java Programming\",\"subject_code\":\"\",\"teacher\":\"Mousami Acharya / R D\"}],\"class_type\":\"1\"},\"12\":{\"time_span\":1,\"subjects\":[{\"subject\":\"Constution Of India\",\"subject_code\":\"\",\"teacher\":\"Leena Patnaik\"}],\"class_type\":0},\"13\":{\"time_span\":\"3\",\"subjects\":[{\"subject\":\"M A T\",\"subject_code\":\"\",\"teacher\":\"S T / N T\"}],\"class_type\":\"1\"},\"14\":{\"time_span\":1,\"subjects\":[],\"class_type\":0},\"15\":{\"time_span\":1,\"subjects\":[{\"subject\":\"Discrete Structure\",\"subject_code\":\"\",\"teacher\":\"S K\"}],\"class_type\":0}}}";

    val gson = Gson()
    val k = TimetableDtoMapper().mapToDomainModel(gson.fromJson(ts, TimetableDto::class.java))

    println(k)


}

