package com.example.hoop_locater

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isEmpty
import com.example.hoop_locater.databinding.ActivityPopupBinding
import com.example.hoop_locater.databinding.ActivityReportPopupBinding

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
        window.attributes.width = (displayMetrics.widthPixels * 0.85).toInt()

        val id = intent.getStringExtra("id")

        binding.idTextView.text = id

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
            Toast.makeText(this@ReportPopupActivity, id + reason, Toast.LENGTH_LONG).show()
        }

        binding.backBtn.setOnClickListener {
            this.finish()
        }

    }
}