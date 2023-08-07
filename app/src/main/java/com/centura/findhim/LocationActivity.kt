package com.centura.findhim

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.centura.findhim.databinding.ActivityLocationBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.yeslab.fastprefs.FastPrefs
import kotlinx.coroutines.*


class LocationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLocationBinding
    private val cardViews = mutableListOf<CardView>()
    private val locationTexts = mutableListOf<TextView>()
    private val locationImages = mutableListOf<ImageView>()
    private val locationFoundText: String by lazy { getString(R.string.completed) }
    private val locationSearchingTexts: List<String> by lazy {
        listOf(
            getString(R.string.searchingcountry),
            getString(R.string.citysearch),
            getString(R.string.locadeter)
        )
    }
    private val locationFoundIcon = R.drawable.check_svgrepo_com__1_

    private var currentIndex = 0

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val delayDuration = 3000L

    private lateinit var locationRequest: LocationRequest

    private val stringList = mutableListOf<String>()
    private val titleList = mutableListOf<String>()

    private var currentIndexPlace = 0
    private var currentIndexTitle = 0

    private var isRunning = true
    private var isRunningTitle = true

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)




        cardViews.addAll(listOf(binding.locationCard, binding.locationCard2, binding.locationCard3))
        locationTexts.addAll(listOf(binding.locationText, binding.locationText2, binding.locationText3))
        locationImages.addAll(listOf(binding.locationImage, binding.locationImage2, binding.locationImage3))





        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000




        getCurrentLocation()

        initList()



    }

    private fun anim(){
        val animation = AlphaAnimation(1f, 0f)
        animation.duration = 1000
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.REVERSE
        binding.statusImage.startAnimation(animation)


    }

    private fun initList(){

        titleList.addAll(listOf(
            "İstasyon Sunucuları Tespit Ediliyor",
            "5G Sinyal Gönderiliyor",
            "Yaklaşık Konum Tespit Ediliyor"
        ))


        stringList.addAll(listOf(
            "Migros",
            "A101",
            "BIM",
            "Şok",
            "Carrefour",
            "Starbucks",
            "Kahve Dünyası",
            "Gratis",
            "Caribou Coffee",
            "Gloria Jean's",
            "Burger King",
            "Mc Donald's",
            "KFC",
            "Domino's Pizza",
            "Popeyes",
            "Pizza Hut",
            "Ekrem Coşkun Döner",
            "Köfteci Yusuf",
            "Media Markt",
            "Teknosa",
            "Vatan Bilgisayar",
            "Greyder",
            "H&M",
            "mavi jeans",
            "FLO",
            "Baydöner",
            "Otogar",
            "Atatürk Parkı",
            "Ziraat Bankası",
            "Yapı Kredi Bankası",
            "Halk Bankası",
            "QNB Finansbank",
            "Decathlon",
            "Denizbank",
            "Ing Bank",
            "Koton",
            "LCW",
            "Atasay",
            "Yataş",
            "Fenerium",
            "3 Otel",
            "Bir Park",
            "Devlet Hastanesi",
            "Eczane",
            "Pastane",
            "Kuruyemişci",
            "Çay Bahçesi",
            "Kafe",
            "Restoran",
            "Kültür Merkezi",
            "Müze",
            "Spor Salonu",
            "Galeri",
            "Otel",
            "Havalimanı",
            "Sosyal Tesisler",
            "Semt Pazarı",
            "Okul",
            "Lise",
            "İlkokul",
            "Üniversite",
            "Fitness Salonu",
            "Güzellik Merkezi",
            "Pideci",
            "Kebapçı",
            "Baklavacı",
            "Alışveriş Merkezi",
            "Sanayi Sitesi",
            "Öğrenci Yurdu",
            "Mezarlık",
            "Petrol İstasyonu",
            "Sağlık Ocağı"
        ))


        stringList.shuffle()

    }

    private fun showRandomStringDemo() {
        binding.locationLay.visibility = View.VISIBLE
        if (!isRunning) return

        if (currentIndexPlace < stringList.size) {
            val randomString = stringList[currentIndexPlace]
            binding.statusText.text = randomString

            if (currentIndexPlace == 25) {
                isRunning = false
                val result = stringList[currentIndexPlace].toString()
                startActivity(Intent(this, BuyActivity::class.java)
                    .putExtra("resultPlace",result))
                return
            }

            currentIndexPlace++
            binding.statusText.postDelayed({ showRandomStringDemo() }, 500)
        }
        anim()
    }

    private fun showRandomTitle(){
        if (!isRunningTitle) return
        if (currentIndexTitle < titleList.size) {
            val randomString = titleList[currentIndexTitle]
            binding.statusTitle.text = randomString

            if (currentIndexTitle == 2) {
                isRunningTitle = false
                return
            }

            currentIndexTitle++
            binding.statusTitle.postDelayed({ showRandomTitle() }, 2000)
        }
    }

    private fun showRandomString() {
        binding.locationLay.visibility = View.VISIBLE
        if (!isRunning) return

        if (currentIndexPlace < stringList.size) {
            val randomString = stringList[currentIndexPlace]
            binding.statusText.text = randomString

            if (currentIndexPlace == 25) {
                isRunning = false
                val result = stringList[currentIndexPlace].toString()
                startActivity(Intent(this, MapsActivity::class.java)
                    .putExtra("resultPlace",result))
                return
            }

            currentIndexPlace++
            binding.statusText.postDelayed({ showRandomString() }, 500)
        }
        anim()
    }





    private fun showNextCardView() {
        coroutineScope.launch {
            if (currentIndex < cardViews.size) {
                val currentCardView = cardViews[currentIndex]
                val currentLocationText = locationTexts[currentIndex]
                val currentLocationImage = locationImages[currentIndex]

                currentCardView.visibility = View.VISIBLE
                currentLocationText.text = locationSearchingTexts[currentIndex]
                currentLocationImage.setImageResource(R.drawable.find_glass_interface_svgrepo_com)

                delay(delayDuration)

                currentLocationText.text = locationFoundText
                currentLocationImage.setImageResource(locationFoundIcon)

                if (currentIndex == cardViews.size - 1) {
                    val prefs = FastPrefs(this@LocationActivity)
                    val coin = prefs.getInt("coin",0)


                    if (coin > 0){
                        showRandomString()
                        showRandomTitle()

                    }else{
                        showRandomStringDemo()
                        showRandomTitle()
                    }


                } else {
                    currentIndex++
                    showNextCardView()
                }


            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    getCurrentLocation()
                } else {
                    turnOnGPS()
                }
            }else{
                finish()
                Toast.makeText(this,"Konum iznini vermeniz gerekmektedir.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                getCurrentLocation()
            }else{
                finish()
                Toast.makeText(this,"Konumunuzu açmanız gerekmektedir.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this@LocationActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(this@LocationActivity)
                        .requestLocationUpdates(locationRequest, object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                LocationServices.getFusedLocationProviderClient(this@LocationActivity)
                                    .removeLocationUpdates(this)
                                if (locationResult != null && locationResult.locations.size > 0) {
                                    val index = locationResult.locations.size - 1
                                    val latitude = locationResult.locations[index].latitude
                                    val longitude = locationResult.locations[index].longitude

                                    val prefs = FastPrefs(this@LocationActivity)
                                    prefs.setString("latitude", latitude.toString())
                                    prefs.setString("longitude", longitude.toString())

                                    println("bilgiler $latitude : $longitude")

                                    showNextCardView()

                                }
                            }
                        }, Looper.getMainLooper())
                } else {
                    turnOnGPS()
                }
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }

    private fun turnOnGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(
            applicationContext
        )
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)

            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(this@LocationActivity, 2)
                    } catch (ex: SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }
    }

    private fun isGPSEnabled(): Boolean {
        var locationManager: LocationManager? = null
        var isEnabled = false
        if (locationManager == null) {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return isEnabled
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()

    }


}