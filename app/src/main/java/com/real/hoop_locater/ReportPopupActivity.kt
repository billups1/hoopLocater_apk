package com.real.hoop_locater

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.real.hoop_locater.BuildConfig.API_URL
import com.real.hoop_locater.databinding.ActivityReportPopupBinding
import com.real.hoop_locater.dto.hoop.Hoop
import com.real.hoop_locater.dto.report.request.ReportCreateRequest
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

            val retrofit = Retrofit.Builder().baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
            val service = retrofit.create(RetrofitService::class.java)

            service.reportHoop(
                ReportCreateRequest(
                    hoop.id,
                    reason,
                    getSharedPreferences("sp1", Context.MODE_PRIVATE).getString("anonymousLogin", null)
                )
            ).enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    Toast.makeText(this@ReportPopupActivity, "신고가 접수되었습니다.", Toast.LENGTH_LONG).show()
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    this@ReportPopupActivity.finish()
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Toast.makeText(this@ReportPopupActivity, "신고에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
                }
            })

        }

        binding.backBtn.setOnClickListener {
            this.finish()
        }

    }
}