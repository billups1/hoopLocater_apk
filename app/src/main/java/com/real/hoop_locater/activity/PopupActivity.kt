package com.real.hoop_locater.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.real.hoop_locater.R
import com.real.hoop_locater.databinding.ActivityPopupBinding
import com.real.hoop_locater.dto.hoop.Hoop

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

        val hoop = intent.getSerializableExtra("hoop") as Hoop

        binding.name.text = hoop.name
        binding.hoopCount.text = hoop.hoopCount.toString()
        binding.floorType.text = hoop.floorType.krName
        binding.light.text = hoop.light.krName
        binding.freeState.text = hoop.freeState.krName
        binding.standardState.text = hoop.standardState.krName

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