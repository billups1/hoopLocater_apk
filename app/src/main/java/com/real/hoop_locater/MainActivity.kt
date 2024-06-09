package com.real.hoop_locater

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.real.hoop_locater.BuildConfig.API_URL
import com.real.hoop_locater.databinding.ActivityMainBinding
import com.real.hoop_locater.dto.hoop.Hoop
import com.real.hoop_locater.dto.hoop.HoopList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

//    val permissions = arrayOf(
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION
//    )
//    val PERM_PLAG = 99

    lateinit var binding: ActivityMainBinding

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
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
    }

    override fun onMapReady(googleMap: GoogleMap) {

        googleMap.setOnInfoWindowClickListener(object : OnInfoWindowClickListener {
            override fun onInfoWindowClick(marker: Marker) {
                val intent = Intent(this@MainActivity, PopupActivity::class.java)
                intent.putExtra("hoop", marker.tag as Hoop)
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

//         확대 축소 버튼
        googleMap.uiSettings.isZoomControlsEnabled = true

//        // 내 위치 가져오기
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                permissions,
//                PERM_PLAG
//            )
//        }
//        googleMap.isMyLocationEnabled = true

        val retrofit = Retrofit.Builder().baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(RetrofitService::class.java)

        // 맵 이동 끝났을때 경계선 좌표 알려주는 메서드
//        googleMap.setOnCameraIdleListener(object : OnCameraIdleListener {
//            override fun onCameraIdle() {
//                var northeastLatLng = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast; // 화면 좌측 상단 부분의 LatLng
//                var southwestLatLng = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest; // 화면 좌측 상단 부분의 LatLng
//
//                Log.d("northeastLatLng", northeastLatLng.toString())
//                Log.d("southwestLatLng", southwestLatLng.toString())
//            }
//        })
        service.getHoopList()?.enqueue(object : Callback<HoopList> {
            override fun onResponse(call: Call<HoopList>, response: Response<HoopList>) {
                response.body()?.forEach {
                    setupMarker(it)
                }
            }
            override fun onFailure(call: Call<HoopList>, t: Throwable) {
                Toast.makeText(this@MainActivity, "농구장 불러오기에 실패했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
            }
        })

        if (intent.getSerializableExtra("hoop") != null) {
            val extraHoop = intent.getSerializableExtra("hoop") as Hoop
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(extraHoop.latitude, extraHoop.longitude),
                    13f
                )
            )
        } else {
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(37.5664056, 126.9778222), // 서울시청
                    13f
                )
            )
        }

        this.googleMap = googleMap
    }

    private fun setupMarker(hoop: Hoop) {

        val markerOption = MarkerOptions().apply {
            position(LatLng(hoop.latitude, hoop.longitude))
            title(hoop.name)
            snippet("클릭해서 자세히 보기")
        }

        val showInfoWindow = googleMap.addMarker(markerOption)
        showInfoWindow!!.tag = hoop
    }

}