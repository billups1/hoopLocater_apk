package com.example.hoop_locater

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hoop_locater.databinding.ActivityUpdatePopupBinding

class UpdatePopupActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdatePopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_popup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityUpdatePopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val displayMetrics = applicationContext.resources.displayMetrics
        window.attributes.width = (displayMetrics.widthPixels * 0.85).toInt()

        val id = intent.getStringExtra("id")

        binding.textView.setText(id)

        binding.nameInput.setText(id)

        binding.floorTypeSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.floorItemList, android.R.layout.simple_spinner_item)
        var floorType = ""
        binding.floorTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        floorType = "URETHANE"
                    }
                    1 -> {
                        floorType = "PARQUET"
                    }
                    2 -> {
                        floorType = "ASPHALT"
                    }
                    3 -> {
                        floorType = "DIRT"
                    }
                    4 -> {
                        floorType = "ETC"
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        binding.lightSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.lightItemList, android.R.layout.simple_spinner_item)
        var light = ""
        binding.lightSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        light = "NO_INFO"
                    }
                    1 -> {
                        light = "PM9"
                    }
                    2 -> {
                        light = "PM10"
                    }
                    3 -> {
                        light = "PM11"
                    }
                    4 -> {
                        light = "PM12"
                    }
                    5 -> {
                        light = "NO_LIGHT"
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }



        binding.updateBtn.setOnClickListener {
            Toast.makeText(this, binding.textView.text.toString(), Toast.LENGTH_SHORT).show()
        }

        binding.backBtn.setOnClickListener {
            this.finish()
            val intent = Intent(this, PopupActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

    }
}