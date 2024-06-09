package com.real.hoop_locater

import com.real.hoop_locater.dto.hoop.Hoop
import com.real.hoop_locater.dto.hoop.request.HoopCreateRequest
import com.real.hoop_locater.dto.hoop.HoopList
import com.real.hoop_locater.dto.report.request.ReportCreateRequest
import com.real.hoop_locater.dto.hoop.request.HoopUpdateRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface RetrofitService {

    @GET("api/v1/hoop/list/all")
    fun getHoopList(): Call<HoopList>

    @POST("api/v1/hoop/create")
    fun createHoop(@Body hoopCreateRequest: HoopCreateRequest): Call<Hoop>

    @PUT("api/v1/hoop/update")
    fun updateHoop(@Body hoopUpdateRequest: HoopUpdateRequest): Call<Hoop>

    @POST("api/v1/report/create")
    fun reportHoop(@Body reportCreateRequest: ReportCreateRequest): Call<Int>

}