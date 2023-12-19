package com.example.chitchat

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("name")
     fun postName(@Body postRequest: NameData): Call<ResponseBody>

    @GET("name")
    fun getName(): Call<NameData>
}
