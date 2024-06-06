package com.example.hoop_locater

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.hoop_locater.databinding.ActivityMainBinding
import com.example.hoop_locater.dto.hoop.Hoop
import com.example.hoop_locater.dto.hoop.HoopList
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
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

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val PERM_PLAG = 99

    lateinit var binding: ActivityMainBinding

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (isPermitted()) {
//            // 현재 위치로 카메라 이동
//
//        } else {
//            ActivityCompat.requestPermissions(this, permissions, PERM_PLAG)
//            Toast.makeText(this,"권한 승인이 필요합니다.",Toast.LENGTH_LONG).show()
//        }

        this.mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.settingFragment -> {
                    Log.d("BottomNavigationView", "zxczxc")
                    return@setOnItemSelectedListener true
                }
                R.id.helpFragment -> {
                    startActivity(Intent(this, HelpPopupActivity::class.java))
                    return@setOnItemSelectedListener true
                }
                R.id.appInfoFragment -> {
                    Log.d("BottomNavigationView", "zxczxc")
                    return@setOnItemSelectedListener true
                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        googleMap.setOnInfoWindowClickListener(object : OnInfoWindowClickListener {
            override fun onInfoWindowClick(marker: Marker) {
                Log.d("markerClick", "markerClick" + marker.title)

                val intent = Intent(this@MainActivity, PopupActivity::class.java)
                intent.putExtra("id", marker.title)
                startActivity(intent)
            }
        })

        googleMap.setOnMapLongClickListener(object : OnMapLongClickListener {
            override fun onMapLongClick(latLng: LatLng) {
                val intent = Intent(this@MainActivity, HoopCreateActivity::class.java)
                intent.putExtra("latitude", latLng.latitude)
                intent.putExtra("longitude", latLng.longitude)
                Log.d("longClick", latLng.toString())
                startActivity(intent)
            }
        })

//         확대 축소 버튼
        googleMap.uiSettings.isZoomControlsEnabled = true

        // 내 위치 가져오기
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                PERM_PLAG
            )
            return
        }
        googleMap.isMyLocationEnabled = true

        val retrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(RetrofitService::class.java)

        service.getHoopList()?.enqueue(object : Callback<HoopList> {
            override fun onResponse(call: Call<HoopList>, response: Response<HoopList>) {
                response.body()!!.forEach {
                    setupMarker(it)
                }
            }

            override fun onFailure(call: Call<HoopList>, t: Throwable) {
                Log.d("YMC", "onResponse 실패" + t.toString() + "/" + t.message)
            }
        })

        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(37.60794, 127.0090),
                15f
            )
        )  // 카메라 이동

        this.googleMap = googleMap
    }

    private fun setupMarker(hoop: Hoop) {

        val markerOption = MarkerOptions().apply {
            position(LatLng(hoop.latitude, hoop.longitude))
            title(hoop.name)
            snippet("이 지역의 농구장")
        }

        googleMap.addMarker(markerOption)?.showInfoWindow()

    }

}