package com.real.hoop_locater.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.real.hoop_locater.BuildConfig.API_URL
import com.real.hoop_locater.R
import com.real.hoop_locater.app.App
import com.real.hoop_locater.app.App.Companion.prefs
import com.real.hoop_locater.app.App.Companion.retrofitService
import com.real.hoop_locater.web.RetrofitService
import com.real.hoop_locater.databinding.ActivityReportPopupBinding
import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.auth.User
import com.real.hoop_locater.dto.hoop.Hoop
import com.real.hoop_locater.web.report.ReportCreateRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReportPopupActivity : AppCompatActivity() {

    lateinit var binding: ActivityReportPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_popup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityReportPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val displayMetrics = applicationContext.resources.displayMetrics
        window.attributes.width = (displayMetrics.widthPixels * 0.91).toInt()

        // 내 id 입력하기
        val accessToken = prefs.getAccessToken()
        var alertDialog = AlertDialog.Builder(this)
        if (accessToken != null && accessToken != "") {
            retrofitService.myInfo().enqueue(object : Callback<ResponseDto<User>> {
                override fun onResponse(
                    call: Call<ResponseDto<User>>,
                    response: Response<ResponseDto<User>>
                ) {
                    if (response.body()?.data?.loginId != null) {
                        binding.writerView.text = response.body()?.data?.nickName.toString()
                    } else {
                        binding.writerView.text = App.prefs.getViewAnonymousId()
                        prefs.deleteAccessToken()
                        alertDialog.setMessage("로그아웃 되었습니다. 다시 로그인해 주세요.")
                            .setPositiveButton("확인") { dialog, which ->
                            }
                            .show()
                    }
                }
                override fun onFailure(call: Call<ResponseDto<User>>, t: Throwable) {
                    binding.writerView.text = App.prefs.getViewAnonymousId()
                    prefs.deleteAccessToken()
                    alertDialog.setMessage("로그아웃 되었습니다. 다시 로그인해 주세요.")
                        .setPositiveButton("확인") { dialog, which ->
                        }
                        .show()
                }
            })
        } else {
            binding.writerView.text = App.prefs.getViewAnonymousId()
        }

        val hoop = intent.getSerializableExtra("hoop") as Hoop

        var reason = ""
        binding.reasonRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.rg_noHoop -> reason = "NO_HOOP"
                R.id.rg_cantEnter -> reason = "CANT_ENTER"
                R.id.rg_straggling -> reason = "STRAGGLING"
                R.id.rg_etc -> reason = "ETC"
            }
        }

        binding.reportBtn.setOnClickListener {
            if (reason == "") {
                Toast.makeText(this@ReportPopupActivity, "사유를 선택한 후 신고해 주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setMessage("신고하시겠습니까?")
                .setPositiveButton("확인") { dialog, which ->

                    retrofitService.reportHoop(
                        ReportCreateRequest(
                            hoop.id,
                            reason
                        )
                    ).enqueue(object : Callback<ResponseDto<Int>> {
                        override fun onResponse(call: Call<ResponseDto<Int>>, response: Response<ResponseDto<Int>>) {
                            Toast.makeText(this@ReportPopupActivity, "신고가 접수되었습니다.", Toast.LENGTH_LONG).show()
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            this@ReportPopupActivity.finish()
                        }

                        override fun onFailure(call: Call<ResponseDto<Int>>, t: Throwable) {
                            Toast.makeText(this@ReportPopupActivity, "신고에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
                        }
                    })
                }.setNegativeButton("취소") { dialog, which ->

                }.show()

        }

        binding.backBtn.setOnClickListener {
            this.finish()
        }

    }
}