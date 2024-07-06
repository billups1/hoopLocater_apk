package com.real.hoop_locater.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.real.hoop_locater.BuildConfig
import com.real.hoop_locater.R
import com.real.hoop_locater.databinding.ActivityPopupBinding
import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.hoop.Hoop
import com.real.hoop_locater.web.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PopupActivity : AppCompatActivity() {

    lateinit var binding: ActivityPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_popup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val displayMetrics = applicationContext.resources.displayMetrics
        window.attributes.width = (displayMetrics.widthPixels * 0.91).toInt()

        var hoop = intent.getSerializableExtra("hoop") as Hoop

        val retrofit = Retrofit.Builder().baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(RetrofitService::class.java)

        Thread {
            hoop = service.getHoop(hoop.id).execute().body()!!.data
            binding.name.text = hoop.name
            binding.hoopCount.text = if (hoop.hoopCount == 0) "(정보없음)" else hoop.hoopCount.toString()
            binding.floorType.text = hoop.floorType.krName
            binding.light.text = hoop.light.krName
            binding.freeState.text = hoop.freeState.krName
            binding.standardState.text = hoop.standardState.krName
            binding.commentBtn.text = "추가정보 : 이 농구장의 댓글 " + hoop.commentCount + "개"

            // 댓글
            binding.commentBtn.setOnClickListener {
                this.finish()
                val intent = Intent(this@PopupActivity, CommentPopupActivity::class.java)
                intent.putExtra("hoop", hoop)
                startActivity(intent)
            }

            // 업데이트
            binding.updateBtn.setOnClickListener {
                this.finish()
                val intent = Intent(this@PopupActivity, UpdatePopupActivity::class.java)
                intent.putExtra("hoop", hoop)
                startActivity(intent)
            }

            // 신고
            binding.reportBtn.setOnClickListener {
                this.finish()
                val intent = Intent(this@PopupActivity, ReportPopupActivity::class.java)
                intent.putExtra("hoop", hoop)
                startActivity(intent)
            }

        }.start()

        // 뒤로가기
        binding.backBtn.setOnClickListener {
            this.finish()
        }

    }
}