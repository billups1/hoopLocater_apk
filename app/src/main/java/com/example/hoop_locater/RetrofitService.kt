package com.example.hoop_locater

import com.example.hoop_locater.dto.hoop.HoopList
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("api/v1/hoop/list/all")
    fun getHoopList(): Call<HoopList>

}