package com.example.hoop_locater

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

    lateinit var binding: ActivityMainBinding

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var currentMarker: Marker? = null
    private var currentMarker2: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val retrofit = Retrofit.Builder().baseUrl("http://localhost:5000/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(RetrofitService::class.java)

        service.getHoopList()?.enqueue(object : Callback<HoopList>{
            override fun onResponse(call: Call<HoopList>, response: Response<HoopList>) {
                var result: HoopList? = response.body()
                Log.d("YMC", "onResponse 성공: " + result?.toString());
            }

            override fun onFailure(call: Call<HoopList>, t: Throwable) {
                Log.d("YMC", "onResponse 실패")
            }
        })

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        binding.navigationView.setOnItemSelectedListener{ item ->
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
                }                }
            }

    }

    override fun onMapReady(googleMap: GoogleMap) {

        googleMap.setOnInfoWindowClickListener(object : OnInfoWindowClickListener {
            override fun onInfoWindowClick(marker: Marker) {
                Toast.makeText(baseContext, "윈도우 클릭", Toast.LENGTH_SHORT).show()
                Log.d("markerClick", "markerClick"+marker.title)

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
                startActivity(intent)
            }
        })

        this.googleMap = googleMap

        val SEOUL = LatLng(37.556, 126.97)

        currentMarker = setupMarker(LatLngEntity(37.5562,126.9724), "1111")  // default 서울역
        currentMarker?.showInfoWindow()

        currentMarker2 = setupMarker(LatLngEntity(37.5565,126.9727), "22222")
        currentMarker2?.showInfoWindow()



    }

    private fun setupMarker(locationLatLngEntity: LatLngEntity, sss : String): Marker? {

        val positionLatLng = LatLng(locationLatLngEntity.latitude!!,locationLatLngEntity.longitude!!)
        val markerOption = MarkerOptions().apply {
            position(positionLatLng)
            title(sss)
            snippet("서울역 위치")
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL  // 지도 유형 설정
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, 15f))  // 카메라 이동
        return googleMap.addMarker(markerOption)

    }

    data class LatLngEntity(
        var latitude: Double?,
        var longitude: Double?
    )


}