package com.real.hoop_locater.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.real.hoop_locater.R
import com.real.hoop_locater.app.App
import com.real.hoop_locater.app.App.Companion.prefs
import com.real.hoop_locater.app.App.Companion.retrofitService
import com.real.hoop_locater.databinding.ActivityUpdatePopupBinding
import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.auth.User
import com.real.hoop_locater.dto.hoop.Hoop
import com.real.hoop_locater.web.hoop.HoopUpdateRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

        // 내 id 입력하기
        val accessToken = prefs.getAccessToken()
        var alertDialog = AlertDialog.Builder(this)
        if (accessToken != null && accessToken != "") {
            retrofitService.myInfo().enqueue(object : Callback<ResponseDto<User>> {
                override fun onResponse(
                    call: Call<ResponseDto<User>>,
                    response: Response<ResponseDto<User>>
                ) {
                    if (response.body()?.data?.loginId != null) {
                        binding.writerView.text = response.body()?.data?.nickName.toString()
                    } else {
                        binding.writerView.text = App.prefs.getViewAnonymousId()
                        prefs.deleteAccessToken()
                        alertDialog.setMessage("로그아웃 되었습니다. 다시 로그인해 주세요.")
                            .setPositiveButton("확인") { dialog, which ->
                            }
                            .show()
                    }
                }
                override fun onFailure(call: Call<ResponseDto<User>>, t: Throwable) {
                    binding.writerView.text = App.prefs.getViewAnonymousId()
                    prefs.deleteAccessToken()
                    alertDialog.setMessage("로그아웃 되었습니다. 다시 로그인해 주세요.")
                        .setPositiveButton("확인") { dialog, which ->
                        }
                        .show()
                }
            })
        } else {
            binding.writerView.text = App.prefs.getViewAnonymousId()
        }

        val hoop = intent.getSerializableExtra("hoop") as Hoop

        binding.id.setText(hoop.id.toString())

        binding.nameInput.setText(hoop.name)
        binding.hoopCountInput.setText(hoop.hoopCount.toString())


        val floorTypeList = listOf("URETHANE","PARQUET","ASPHALT","DIRT","ETC")
        binding.floorTypeSpinner.adapter = ArrayAdapter.createFromResource(this,
            R.array.floorItemList, android.R.layout.simple_spinner_item)
        var floorType = hoop.floorType.key
        binding.floorTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    floorType = floorTypeList[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        val lightList = listOf("NO_INFO", "PM9", "PM10", "PM11", "PM12", "NO_LIGHT")
        binding.lightSpinner.adapter = ArrayAdapter.createFromResource(this,
            R.array.lightItemList, android.R.layout.simple_spinner_item)
        var light = hoop.light.key
        binding.lightSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                light = lightList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val freeStateList = listOf("NO_INFO", "FREE", "PAID")
        binding.freeStateSpinner.adapter = ArrayAdapter.createFromResource(this,
            R.array.freeStateItemList, android.R.layout.simple_spinner_item)
        var freeState = hoop.freeState.key
        binding.freeStateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                freeState = freeStateList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val standardStateList = listOf("NO_INFO", "STANDARD", "UN_STANDARD")
        binding.StandardStateSpinner.adapter = ArrayAdapter.createFromResource(this,
            R.array.standardStateItemList, android.R.layout.simple_spinner_item)
        var standardState = hoop.standardState.key
        binding.StandardStateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                standardState = standardStateList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.floorTypeSpinner.setSelection(hoop.floorType.order)
        binding.lightSpinner.setSelection(hoop.light.order)
        binding.freeStateSpinner.setSelection(hoop.freeState.order)
        binding.StandardStateSpinner.setSelection(hoop.standardState.order)

        binding.updateBtn.setOnClickListener {

            if (binding.nameInput.text.toString().trim().length == 0) {
                Toast.makeText(this, "농구장 이름을 입력해 주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (binding.hoopCountInput.text.toString().trim().length == 0) {
                Toast.makeText(this, "골대 개수를 입력해 주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setMessage("농구장 정보를 수정하시겠습니까?")
                .setPositiveButton("확인") { dialog, which ->

                    retrofitService.updateHoop(
                        HoopUpdateRequest(
                            hoop.id,
                            binding.nameInput.text.toString(),
                            binding.hoopCountInput.text.toString().toInt(),
                            floorType,
                            light,
                            freeState,
                            standardState
                        )
                    ).enqueue(object : Callback<ResponseDto<Hoop>> {
                        override fun onResponse(call: Call<ResponseDto<Hoop>>, response: Response<ResponseDto<Hoop>>) {
                            Toast.makeText(this@UpdatePopupActivity, "농구장 정보가 수정되었습니다.", Toast.LENGTH_LONG).show()
                            val intent = Intent(baseContext, MainActivity::class.java)
                            intent.putExtra("hoop", response.body()?.data as Hoop)
                            startActivity(intent)
                            ActivityCompat.finishAffinity(this@UpdatePopupActivity)
                        }

                        override fun onFailure(call: Call<ResponseDto<Hoop>>, t: Throwable) {
                            Toast.makeText(
                                this@UpdatePopupActivity,
                                "농구장 수정에 실패했습니다. 다시 시도해 주세요.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
                }
                .setNegativeButton("취소") { dialog, which ->

                }.show()
        }

        binding.backBtn.setOnClickListener {
            this.finish()
        }

    }
}