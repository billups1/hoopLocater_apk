package com.real.hoop_locater.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.real.hoop_locater.R
import com.real.hoop_locater.app.App.Companion.retrofitService
import com.real.hoop_locater.databinding.ActivityMainBinding
import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.hoop.Hoop
import com.real.hoop_locater.dto.hoop.HoopList
import com.real.hoop_locater.util.RequestPermissionsUtil
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.CookieManager

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityMainBinding

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var sharedPreferences: SharedPreferences

    val DEFAULT_ZOOM = 12f;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)

        this.mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // 메뉴바 아이콘 기능 추가
        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.settingFragment -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.authFragment -> {
                    startActivity(Intent(this, AuthActivity::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.helpFragment -> {
                    startActivity(Intent(this, HelpPopupActivity::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.homeFragment -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    ActivityCompat.finishAffinity(this)
                    return@setOnItemSelectedListener true
                }

                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }

        MobileAds.initialize(this, object : OnInitializationCompleteListener {
            override fun onInitializationComplete(p0: InitializationStatus) {
            }
        })
        binding.adView.loadAd(AdRequest.Builder().build())

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

        // 확대 축소 버튼
        googleMap.uiSettings.isZoomControlsEnabled = true


        // 마커 불러오기
        var builder = OkHttpClient().newBuilder()
        var okHttpClient = builder
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()

        // 맵 이동 끝났을때 경계선 좌표 알려주는 메서드 -> TODO 농구장 데이터가 더 많아지면 경계선 조금 밖에 까지만 마커 뿌리기
//        googleMap.setOnCameraIdleListener(object : OnCameraIdleListener {
//            override fun onCameraIdle() {
//                var northeastLatLng = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast; // 화면 좌측 상단 부분의 LatLng
//                var southwestLatLng = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest; // 화면 좌측 상단 부분의 LatLng
//
//                Log.d("northeastLatLng", northeastLatLng.toString())
//                Log.d("southwestLatLng", southwestLatLng.toString())
//            }
//        })
        retrofitService.getHoopList()?.enqueue(object : Callback<ResponseDto<HoopList>> {
            override fun onResponse(call: Call<ResponseDto<HoopList>>, response: Response<ResponseDto<HoopList>>
            ) {
                response.body()?.data?.forEach {
                    setupMarker(it)
                }
            }

            override fun onFailure(call: Call<ResponseDto<HoopList>>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "농구장 불러오기에 실패했습니다. 잠시 후 다시 시도해 주세요.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        if (intent.getSerializableExtra("hoop") != null) {
            val extraHoop = intent.getSerializableExtra("hoop") as Hoop
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(extraHoop.latitude, extraHoop.longitude),
                    DEFAULT_ZOOM
                )
            )
        } else if (sharedPreferences.getLong("myLatitude", 0L) != 0L) {
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(
                    sharedPreferences.getDouble("myLatitude", 0.0),
                    sharedPreferences.getDouble("myLongitude", 0.0)
                ), DEFAULT_ZOOM)
            )
        } else {
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(37.55100808889846, 126.99088096618652), // 남산
                    DEFAULT_ZOOM
                )
            )
        }

        // 내 위치 가져오기
        binding.myLocationBtn.setOnClickListener {
            if (!isEnableLocationSystem(this@MainActivity)) {
                Toast.makeText(this@MainActivity, "위치정보가 꺼져 있습니다.", Toast.LENGTH_LONG).show()
            } else {
                moveToMyLocation(googleMap)
            }
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

    @SuppressLint("MissingPermission")
    private fun moveToMyLocation(googleMap: GoogleMap) {
        RequestPermissionsUtil(this).requestLocation()

//        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) // https://velog.io/@simsubeen/Android-Kotlin-%EC%82%AC%EC%9A%A9%EC%9E%90-%EC%9C%84%EC%B9%98-%EC%96%BB%EA%B8%B0

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    Toast.makeText(this, "내 위치로 이동", Toast.LENGTH_LONG).show()
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), googleMap.cameraPosition.zoom))

                    val markerOption = MarkerOptions().apply {
                        position(LatLng(location.latitude, location.longitude))
                        icon(getBitmapDescriptorFromVector(R.drawable.baseline_adjust_24, this@MainActivity))
                        title("내 현재 위치")
                    }

                    val myLocationMarker = googleMap.addMarker(markerOption)
                    Handler(Looper.getMainLooper()).postDelayed({
                        myLocationMarker!!.remove()
                    }, 5000)
                }
            }
            .addOnFailureListener { fail ->
                Toast.makeText(this, "위치 찾기 실패", Toast.LENGTH_LONG).show()
            }
    }

    fun isEnableLocationSystem(context: Context): Boolean { // 위치상태 켜져있는지 확인
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            locationManager?.isLocationEnabled!!
        }else{
            val mode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF)
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }

    private fun SharedPreferences.getDouble(key: String, default: Double) =
        java.lang.Double.longBitsToDouble(
            getLong(
                key,
                java.lang.Double.doubleToRawLongBits(default)
            )
        )

    private fun getBitmapDescriptorFromVector(id: Int, context: Context): BitmapDescriptor { // 벡터 이미지를 비트맵으로 바꾸기
        var vectorDrawable: Drawable = context.getDrawable(id)!!
        var h = (30 * getResources().getDisplayMetrics().density).toInt();
        var w = (30 * getResources().getDisplayMetrics().density).toInt();
        vectorDrawable.setBounds(0, 0, w, h)
        var bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bm)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bm)
    }

}