package com.real.hoop_locater.app

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences.Editor
import android.util.Log

// https://velog.io/@jinstonlee/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C%EC%97%90%EC%84%9C-JWT-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0
class Prefs(context: Context) {
    private var prefNm = "sp1"
    private var prefs = context.getSharedPreferences(prefNm, MODE_PRIVATE)

    fun getAccessToken(): String {
        return prefs.getString("accessToken", "").toString()
    }

    fun setAccessToken(accessToken: String) {
        val editor : Editor = prefs.edit()
        editor.putString("accessToken", accessToken)
        editor.commit()
    }

    fun deleteAccessToken() {
        val editor : Editor = prefs.edit()
        editor.remove("accessToken")
        editor.commit()
    }

    fun getViewAnonymousId(): String { // 회원한테 보여지기 위한 비회원 id  ex) (비회원) abcdefghi
        return "(비회원) " + prefs.getString("anonymousId", "").toString().substring(10) // "anonymous_" 제외한 순수 uuid
    }

    fun getAnonymousId(): String { // 오리지날 비회원 id
        return prefs.getString("anonymousId", "").toString()
    }

    fun setAnonymousId(anonymousId: String) {
        val editor : Editor = prefs.edit()
        editor.putString("anonymousId", anonymousId)
        editor.commit()
    }

    fun deleteAnonymousId() {
        val editor : Editor = prefs.edit()
        editor.remove("anonymousId")
        editor.commit()
    }

}