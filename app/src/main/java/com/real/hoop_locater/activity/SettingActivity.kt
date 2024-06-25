package com.real.hoop_locater.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.real.hoop_locater.R
import com.real.hoop_locater.adapter.RVAdapter
import com.real.hoop_locater.databinding.ActivitySettingBinding
import com.real.hoop_locater.menu.SettingMenu

class SettingActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingBinding
    private val menuList = mutableListOf<SettingMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menuList.add(SettingMenu.SET_DEFAULT_LOCATION)
        menuList.add(SettingMenu.PRIVACY_POLICY)

        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        val adapter = RVAdapter(baseContext, menuList)

        adapter.itemClick = object : RVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                var intent: Intent? = null;
                when (menuList[position]) {
                    SettingMenu.SET_DEFAULT_LOCATION -> {
                        intent = Intent(baseContext, SetDefaultLocationActivity::class.java)
                    }
                    SettingMenu.PRIVACY_POLICY -> {
                        intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://billups1.github.io/"))
                    }
                }
                startActivity(intent)
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.settingFragment -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.helpFragment -> {
                    startActivity(Intent(this, HelpPopupActivity::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.homeFragment -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }

                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }


    }

}