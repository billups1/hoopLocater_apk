package com.example.hoop_locater

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("api/v1/hoop/list")
    fun getHoopList(): Call<Hoop>

}