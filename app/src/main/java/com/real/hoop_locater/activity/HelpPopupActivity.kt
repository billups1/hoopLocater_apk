package com.real.hoop_locater.activity

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.real.hoop_locater.R
import com.real.hoop_locater.databinding.ActivityHelpPopupBinding

class HelpPopupActivity : AppCompatActivity() {

    lateinit var binding: ActivityHelpPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_help_popup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityHelpPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.helpContentTextView.movementMethod = ScrollingMovementMethod.getInstance()

        val displayMetrics = applicationContext.resources.displayMetrics
        window.attributes.width = (displayMetrics.widthPixels * 0.91).toInt()

        binding.backBtn.setOnClickListener {
            this.finish()
        }

    }
}