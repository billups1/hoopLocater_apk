package com.real.hoop_locater.app

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var req = chain.request().newBuilder()
            .addHeader("accessToken", App.prefs.getAccessToken())
            .addHeader("anonymousId", App.prefs.getAnonymousId())
            .build()
        return chain.proceed(req)

    }
}