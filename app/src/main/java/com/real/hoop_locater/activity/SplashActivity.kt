package com.real.hoop_locater.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.real.hoop_locater.R
import com.real.hoop_locater.app.App
import com.real.hoop_locater.app.App.Companion.ANONYMOUS_ID_PREFIX
import java.util.UUID

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)

        if (App.prefs.getAnonymousId() == null || App.prefs.getAnonymousId().length < 11) {
            App.prefs.setAnonymousId(ANONYMOUS_ID_PREFIX + UUID.randomUUID().toString().substring(0, 10))
        }

    }
}