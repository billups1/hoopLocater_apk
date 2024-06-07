package com.real.hoop_locater

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.real.hoop_locater.BuildConfig.API_URL
import com.real.hoop_locater.databinding.ActivityUpdatePopupBinding
import com.real.hoop_locater.dto.hoop.Hoop
import com.real.hoop_locater.dto.hoop.HoopUpdateRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
        window.attributes.width = (displayMetrics.widthPixels * 0.91).toInt()

        val hoop = intent.getSerializableExtra("hoop") as Hoop

        binding.id.setText(hoop.id.toString())

        binding.nameInput.setText(hoop.name)
        binding.hoopCountInput.setText(hoop.hoopCount.toString())

        binding.floorTypeSpinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.floorItemList,
            android.R.layout.simple_spinner_item
        )
        var floorType = ""
        binding.floorTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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


        binding.lightSpinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.lightItemList,
            android.R.layout.simple_spinner_item
        )
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

        binding.floorTypeSpinner.setSelection(hoop.floorType.order)
        binding.lightSpinner.setSelection(hoop.light.order)

        binding.updateBtn.setOnClickListener {
            val retrofit = Retrofit.Builder().baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
            val service = retrofit.create(RetrofitService::class.java)

            service.updateHoop(
                HoopUpdateRequest(
                    hoop.id.toString().toInt(),
                    binding.nameInput.text.toString(),
                    binding.hoopCountInput.text.toString().toInt(),
                    floorType,
                    light
                )
            ).enqueue(object : Callback<Hoop> {
                override fun onResponse(call: Call<Hoop>, response: Response<Hoop>) {
                    Toast.makeText(this@UpdatePopupActivity, "농구장 정보가 수정되었습니다.", Toast.LENGTH_LONG).show()
                    finish()
                    val intent = Intent(baseContext, MainActivity::class.java)
                    intent.putExtra("hoop", response.body() as Hoop)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<Hoop>, t: Throwable) {
                    Toast.makeText(
                        this@UpdatePopupActivity,
                        "농구장 수정에 실패했습니다. 다시 시도해 주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        }

        binding.backBtn.setOnClickListener {
            this.finish()
            val intent = Intent(this, PopupActivity::class.java)
            intent.putExtra("hoop", hoop)
            startActivity(intent)
        }

    }
}