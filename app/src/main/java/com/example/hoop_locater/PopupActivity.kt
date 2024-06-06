package com.example.hoop_locater

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hoop_locater.databinding.ActivityPopupBinding
import com.example.hoop_locater.databinding.ActivityUpdatePopupBinding
import com.example.hoop_locater.dto.hoop.Hoop

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
        window.attributes.width = (displayMetrics.widthPixels * 0.85).toInt()

        val hoop = intent.getSerializableExtra("hoop") as Hoop

        binding.name.text = hoop.name
        binding.hoopCount.text = hoop.hoopCount.toString()
        binding.floorType.text = hoop.floorType.krName
        binding.light.text = hoop.light.krName

        // 업데이트
        binding.updateBtn.setOnClickListener {
            this.finish()
            val intent = Intent(this@PopupActivity, UpdatePopupActivity::class.java)
            intent.putExtra("hoop", hoop)
            startActivity(intent)
        }

        // 뒤로가기
        binding.backBtn.setOnClickListener {
            this.finish()
        }

        // 신고
        binding.reportBtn.setOnClickListener {
            this.finish()
            val intent = Intent(this@PopupActivity, ReportPopupActivity::class.java)
            intent.putExtra("hoop", hoop)
            startActivity(intent)
        }

    }
}