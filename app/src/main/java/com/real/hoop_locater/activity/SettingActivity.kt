package com.real.hoop_locater.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.real.hoop_locater.R
import com.real.hoop_locater.adapter.RVAdapter
import com.real.hoop_locater.menu.SettingMenu

class SettingActivity : AppCompatActivity() {

    private val menuList = mutableListOf<SettingMenu>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        menuList.add(SettingMenu.SET_DEFAULT_LOCATION)

        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        val adapter = RVAdapter(baseContext, menuList)

        adapter.itemClick = object : RVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                var intent: Intent? = null;
                when (menuList[position]) {
                    SettingMenu.SET_DEFAULT_LOCATION -> {
                        intent = Intent(baseContext, SetDefaultLocationActivity::class.java)
                    }
                }
                startActivity(intent)
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


    }

}