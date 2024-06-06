package com.example.hoop_locater

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hoop_locater.databinding.ActivityHoopCreateBinding
import com.example.hoop_locater.dto.hoop.Hoop
import com.example.hoop_locater.dto.hoop.HoopCreateRequest
import com.example.hoop_locater.dto.hoop.HoopList
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
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


        binding.createBtn.setOnClickListener {

            val retrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build()
            val service = retrofit.create(RetrofitService::class.java)

            service.createHoop(HoopCreateRequest(binding.nameInput.text.toString(),
                intent.getDoubleExtra("latitude", 0.0),
                intent.getDoubleExtra("longitude", 0.0),
                binding.hoopCountInput.text.toString().toInt(),
                floorType,
                light)).enqueue(object : Callback<Hoop> {
                override fun onResponse(call: Call<Hoop>, response: Response<Hoop>) {
                    Toast.makeText(this@HoopCreateActivity, "농구장이 생성되었습니다.", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@HoopCreateActivity, MainActivity::class.java)
                    intent.putExtra("hoop", response.body()!!)
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