package com.centura.findhim

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.centura.findhim.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yeslab.fastprefs.FastPrefs
import kotlin.math.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    private val PERMISSIONS_REQUEST_LOCATION = 100
    private var resultPlace: String? = null

    private val EARTH_RADIUS = 6371.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val prefs = FastPrefs(this)
        val coin = prefs.getInt("coin",0)
        val result = coin - 1
        prefs.setInt("coin",result)

        resultPlace = intent.getStringExtra("resultPlace")

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        showCurrentLocation()
        showRandomLocation()
    }

    private fun showCurrentLocation() {
        if (googleMap != null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            googleMap?.isMyLocationEnabled = true

            // Mevcut konumu al
            val prefs = FastPrefs(this)
            val lat = prefs.get("latitude", 0.0)
            val lng = prefs.get("longitude", 0.0)
            val currentLocation = LatLng(lat!!, lng!!)

            // Marker ekle
            googleMap?.addMarker(
                MarkerOptions()
                    .position(currentLocation)
                    .title(getString(R.string.yourlocation))

            )

            // Kamerayı mevcut konuma hareket ettir
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 11f))
        }
    }

    private fun showRandomLocation() {
        if (googleMap != null) {
            val currentLocation = getLatLngFromCurrentLocation()
            val doubleList = mutableListOf<Double>()
            for (i in 0 until 15) {
                val randomValue = (1..8).random().toDouble()
                doubleList.add(randomValue)
            }

            val randomElement = doubleList.random()
            val randomLocation = getRandomLocation(currentLocation, randomElement)

            // Marker ekle
            googleMap?.addMarker(
                MarkerOptions()
                    .position(randomLocation)
                    .title("Aranan kişi buraya $randomElement km uzaklıkta.")
            )
        }
    }

    private fun getLatLngFromCurrentLocation(): LatLng {
        val prefs = FastPrefs(this)
        val lat = prefs.get("latitude", 0.0)
        val lng = prefs.get("longitude", 0.0)
        return LatLng(lat!!, lng!!)
    }

    private fun getRandomLocation(currentLocation: LatLng, distance: Double): LatLng {
        // Rastgele bir yöne ve mesafeye göre yeni bir konum oluştur
        val radius = distance / EARTH_RADIUS
        val randomAngle = 2 * PI * Math.random()
        val randomDistance = sqrt(Math.random()) * radius

        val latitude1 = Math.toRadians(currentLocation.latitude)
        val longitude1 = Math.toRadians(currentLocation.longitude)

        val latitude2 = asin(sin(latitude1) * cos(randomDistance) + cos(latitude1) * sin(randomDistance) * cos(randomAngle))
        val longitude2 = longitude1 + atan2(
            sin(randomAngle) * sin(randomDistance) * cos(latitude1),
            cos(randomDistance) - sin(latitude1) * sin(latitude2)
        )

        return LatLng(Math.toDegrees(latitude2), Math.toDegrees(longitude2))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation()
                showRandomLocation()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }



}
