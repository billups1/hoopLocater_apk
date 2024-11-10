package com.real.hoop_locater.app

import android.app.Application
import com.real.hoop_locater.BuildConfig.API_URL
import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.auth.User
import com.real.hoop_locater.web.RetrofitService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    companion object {
        lateinit var prefs: Prefs
        lateinit var retrofitService: RetrofitService
        var ANONYMOUS_ID_PREFIX = "anonymous_"
    }
    override fun onCreate() {
        // SharedPreferences
        prefs = Prefs(applicationContext)

        // Retrofit2 서비스
        var builder = OkHttpClient().newBuilder()
        var okHttpClient = builder
            .addInterceptor(AuthInterceptor())
            .build()
        val retrofit = Retrofit.Builder().baseUrl(API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
        retrofitService = retrofit.create(RetrofitService::class.java)

        super.onCreate()
    }
}