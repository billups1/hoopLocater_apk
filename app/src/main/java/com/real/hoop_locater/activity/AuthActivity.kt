package com.real.hoop_locater.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.real.hoop_locater.BuildConfig.API_URL
import com.real.hoop_locater.R
import com.real.hoop_locater.databinding.ActivityAuthBinding
import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.auth.User
import com.real.hoop_locater.web.RetrofitService
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager

class AuthActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 메뉴바 아이콘 기능 추가
        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.settingFragment -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.authFragment -> {
                    startActivity(Intent(this, AuthActivity::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.helpFragment -> {
                    startActivity(Intent(this, HelpPopupActivity::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.homeFragment -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    ActivityCompat.finishAffinity(this)
                    return@setOnItemSelectedListener true
                }

                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }

        var builder = OkHttpClient().newBuilder()
        var okHttpClient = builder
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()

        val retrofit = Retrofit.Builder().baseUrl(API_URL)
            .client(okHttpClient) // 쿠키매니저
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(RetrofitService::class.java)

        // fragment 설정
        binding.run {
            service.myInfo().enqueue(object : Callback<ResponseDto<User>> {
                override fun onResponse(call: Call<ResponseDto<User>>, response: Response<ResponseDto<User>>
                ) {
                    var user = response.body()?.data
                    if (user?.loginId != null) { // 유저가 있으면 내 정보로 이동
                        setFragment(AuthMyInfoFragment())
                    }
                }

                override fun onFailure(call: Call<ResponseDto<User>>, t: Throwable) {

                }
            })
        }

    }

    private fun setFragment(flag: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.authFrameLayout, flag)
            .setReorderingAllowed(true)
            .addToBackStack("")
            .commit()
    }

}