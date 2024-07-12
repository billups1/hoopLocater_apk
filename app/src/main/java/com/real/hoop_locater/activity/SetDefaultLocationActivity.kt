package com.real.hoop_locater.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.real.hoop_locater.R
import com.real.hoop_locater.databinding.ActivityMainBinding
import com.real.hoop_locater.databinding.ActivitySetDefaultLocationBinding
import kotlin.math.log

class SetDefaultLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivitySetDefaultLocationBinding

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var sharedPreferences: SharedPreferences

    var latlng = LatLng(37.55100808889846, 126.99088096618652)  // 남산

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySetDefaultLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)

        this.mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.settingFragment -> {
                    startActivity(Intent(this, SettingActivity::class.java))
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

        binding.setBtn.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putDouble("myLatitude", latlng.latitude)
            editor.putDouble("myLongitude", latlng.longitude)
            editor.commit()
            Toast.makeText(this, "기본 위치가 변경되었습니다.", Toast.LENGTH_LONG).show()

            Log.d("qwe", sharedPreferences.getLong("myLongitude", 0).toString())
            this.finish()
        }

        binding.resetBtn.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.remove("myLatitude")
            editor.remove("myLongitude")
            editor.commit()
            Toast.makeText(this, "기본 위치가 초기화되었습니다.", Toast.LENGTH_LONG).show()
            this.finish()
        }

        binding.backBtn.setOnClickListener {
            this.finish()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setOnCameraIdleListener(object : OnCameraIdleListener {
            override fun onCameraIdle() {
                var cameraPosition = googleMap.cameraPosition
                latlng = LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude)
            }
        })

        googleMap.uiSettings.isZoomControlsEnabled = true

        if (sharedPreferences.getLong("myLatitude", 0L) != 0L) {
            latlng = LatLng(
                sharedPreferences.getDouble("myLatitude", 0.0),
                sharedPreferences.getDouble("myLongitude", 0.0)
            )

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(latlng, 13f)
            )
        } else {
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latlng, // 서울시청
                    13f
                )
            )
        }

        this.googleMap = googleMap
    }

    // https://stackoverflow.com/questions/16319237/cant-put-double-sharedpreferences
    private fun SharedPreferences.Editor.putDouble(key: String, double: Double) =
        putLong(key, java.lang.Double.doubleToRawLongBits(double))

    private fun SharedPreferences.getDouble(key: String, default: Double) =
        java.lang.Double.longBitsToDouble(
            getLong(
                key,
                java.lang.Double.doubleToRawLongBits(default)
            )
        )

}