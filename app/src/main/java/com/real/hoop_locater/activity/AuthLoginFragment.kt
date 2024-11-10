package com.real.hoop_locater.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.real.hoop_locater.BuildConfig.API_URL
import com.real.hoop_locater.R
import com.real.hoop_locater.databinding.FragmentAuthLoginBinding
import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.auth.User
import com.real.hoop_locater.app.App
import com.real.hoop_locater.app.App.Companion.prefs
import com.real.hoop_locater.app.App.Companion.retrofitService
import com.real.hoop_locater.app.AuthInterceptor
import com.real.hoop_locater.web.RetrofitService
import com.real.hoop_locater.web.auth.AuthLoginRequest
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthLoginFragment : Fragment() {

    lateinit var binding: FragmentAuthLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAuthLoginBinding.inflate(inflater, container, false)

        // 내 id 불러오기
        val accessToken = prefs.getAccessToken()
        if (accessToken != null && accessToken != "") {
            retrofitService.myInfo().enqueue(object : Callback<ResponseDto<User>> {
                override fun onResponse(
                    call: Call<ResponseDto<User>>,
                    response: Response<ResponseDto<User>>
                ) {
                    if (response.body()?.data?.loginId != null) {
                        findNavController().navigate(R.id.action_authLoginFragment_to_authMyInfoFragment)
                    } else {
                        App.prefs.deleteAccessToken()
                    }
                }
                override fun onFailure(call: Call<ResponseDto<User>>, t: Throwable) {
                    App.prefs.deleteAccessToken()
                }
            })
        }

        binding.joinBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_authLoginFragment_to_authJoinFragment)
        }

        binding.loginBtn.setOnClickListener {

            var loginId = binding.idInput.text.toString().trim()
            var password = binding.passwordInput.text.toString().trim()

            if (loginId.length == 0) {
                Toast.makeText(activity, "아이디를 입력해 주시기 바랍니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password.length == 0) {
                Toast.makeText(activity, "비밀번호를 입력해 주시기 바랍니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            retrofitService.login(
                AuthLoginRequest(loginId, password)
            ).enqueue(object : Callback<ResponseDto<User>> {
                override fun onResponse(
                    call: Call<ResponseDto<User>>,
                    response: Response<ResponseDto<User>>
                ) {
                    if (response.body()?.code.equals("success")) {
                        App.prefs.setAccessToken(response.headers().get("accessToken").toString())
                        it.findNavController().navigate(R.id.action_authLoginFragment_to_authMyInfoFragment)
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show() // 로그인 실패 시 Toast로 메시지 띄우기
                    }
                }

                override fun onFailure(call: Call<ResponseDto<User>>, t: Throwable) {
                    Toast.makeText(context, "요청에 실패했습니다.", Toast.LENGTH_LONG).show()
                    return
                }
            })
        }

        // 메인화면으로 이동
        binding.moveToMainBtn.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish() // 현재 액티비티 종료하기
        }

        return binding.root
    }

}