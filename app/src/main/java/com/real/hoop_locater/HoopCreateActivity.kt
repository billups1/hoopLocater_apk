package com.real.hoop_locater

import android.content.Context
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.real.hoop_locater.BuildConfig.API_URL
import com.real.hoop_locater.databinding.ActivityHoopCreateBinding
import com.real.hoop_locater.dto.hoop.Hoop
import com.real.hoop_locater.dto.hoop.request.HoopCreateRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HoopCreateActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityHoopCreateBinding

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hoop_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityHoopCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.settingFragment -> {
                    Toast.makeText(baseContext, "구현중", Toast.LENGTH_LONG).show()
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

        val floorTypeList = listOf("URETHANE","PARQUET","ASPHALT","DIRT","ETC")
        binding.floorTypeSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.floorItemList, android.R.layout.simple_spinner_item)
        var floorType = "URETHANE"
        binding.floorTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        binding.lightSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.lightItemList, android.R.layout.simple_spinner_item)
        var light = "NO_INFO"
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
        binding.freeStateSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.freeStateItemList, android.R.layout.simple_spinner_item)
        var freeState = "FREE"
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
        binding.StandardStateSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.standardStateItemList, android.R.layout.simple_spinner_item)
        var standardState = "NO_INFO"
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


        binding.createBtn.setOnClickListener {

            if (binding.nameInput.text.toString().length == 0) {
                Toast.makeText(this, "농구장 이름을 입력해 주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (binding.hoopCountInput.text.toString().length == 0) {
                Toast.makeText(this, "골대 개수를 입력해 주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val retrofit = Retrofit.Builder().baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
            val service = retrofit.create(RetrofitService::class.java)

            service.createHoop(
                HoopCreateRequest(binding.nameInput.text.toString(),
                intent.getDoubleExtra("latitude", 0.0),
                intent.getDoubleExtra("longitude", 0.0),
                binding.hoopCountInput.text.toString().toInt(),
                floorType,
                light,
                freeState,
                standardState,
                getSharedPreferences("sp1", Context.MODE_PRIVATE).getString("anonymousLogin", null))
            ).enqueue(object : Callback<Hoop> {
                override fun onResponse(call: Call<Hoop>, response: Response<Hoop>) {
                    Toast.makeText(this@HoopCreateActivity, "농구장이 생성되었습니다.", Toast.LENGTH_LONG).show()
                    finish()
                    val intent = Intent(this@HoopCreateActivity, MainActivity::class.java)
                    intent.putExtra("hoop", response.body())
                    startActivity(intent)
                }

                override fun onFailure(call: Call<Hoop>, t: Throwable) {
                    Toast.makeText(this@HoopCreateActivity, "농구장 생성에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
                }
            })
        }


        binding.backBtn.setOnClickListener {
            this.finish()
        }
    }

    override fun onMapReady(googleMap : GoogleMap) {
        this.googleMap = googleMap

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        currentMarker = setupMarker(LatLngEntity(latitude, longitude))
        currentMarker?.showInfoWindow()
    }

    private fun setupMarker(locationLatLngEntity: LatLngEntity): Marker? {

        val positionLatLng = LatLng(locationLatLngEntity.latitude!!,locationLatLngEntity.longitude!!)
        val markerOption = MarkerOptions().apply {
            position(positionLatLng)
            title("새 농구장 위치")
            snippet("다음 위치에 새 농구장 정보를 등록합니다.")
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL  // 지도 유형 설정
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, 16f))  // 카메라 이동
        return googleMap.addMarker(markerOption)

    }

    data class LatLngEntity(
        var latitude: Double?,
        var longitude: Double?
    )
}