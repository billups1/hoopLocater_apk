package com.real.hoop_locater.app

import android.app.AlertDialog
import android.content.Context
import com.real.hoop_locater.app.App.Companion.prefs
import com.real.hoop_locater.app.App.Companion.retrofitService
import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.auth.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthApp(context: Context) {

    private var alertDialog = AlertDialog.Builder(context)

    fun getMyId(): String {
        var myId: String = prefs.getAnonymousId()

        val accessToken = prefs.getAccessToken()

        if (accessToken != null || accessToken != "") {
            retrofitService.myInfo().enqueue(object : Callback<ResponseDto<User>> {
                override fun onResponse(
                    call: Call<ResponseDto<User>>,
                    response: Response<ResponseDto<User>>
                ) {
                    if (response.body()?.data?.loginId.toString() != null) {
                        myId = response.body()?.data?.loginId.toString()
                    } else {
                        prefs.deleteAccessToken()
                        alertDialog.setMessage("로그아웃 되었습니다. 다시 로그인해 주세요.")
                            .setPositiveButton("확인") { dialog, which ->
                            }
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponseDto<User>>, t: Throwable) {
                    prefs.deleteAccessToken()
                    alertDialog.setMessage("로그아웃 되었습니다. 다시 로그인해 주세요.")
                        .setPositiveButton("확인") { dialog, which ->
                        }
                        .show()
                }
            })
        }

        return myId;
    }

    fun anonymousState(): Boolean { // 익명 상태이면 true, 계정 로그인 상태이면 false
        if (prefs.getAccessToken() == null) {
            return true
        } else {
            var result: Boolean = true
            retrofitService.myInfo().enqueue(object : Callback<ResponseDto<User>> {
                override fun onResponse(
                    call: Call<ResponseDto<User>>,
                    response: Response<ResponseDto<User>>
                ) {
                    if (response.body()?.data?.loginId.toString() != null) {
                        result = false
                    } else {
                        prefs.deleteAccessToken()
                        alertDialog.setMessage("로그아웃 되었습니다. 다시 로그인해 주세요.")
                            .setPositiveButton("확인") { dialog, which ->
                            }
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponseDto<User>>, t: Throwable) {
                    prefs.deleteAccessToken()
                    alertDialog.setMessage("로그아웃 되었습니다. 다시 로그인해 주세요.")
                        .setPositiveButton("확인") { dialog, which ->
                        }
                        .show()
                }
            })
            return result
        }
    }

}