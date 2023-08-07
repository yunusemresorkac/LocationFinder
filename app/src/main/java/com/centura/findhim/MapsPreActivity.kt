package com.centura.findhim

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.centura.findhim.databinding.ActivityMapsPreBinding


class MapsPreActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMapsPreBinding


    private val stringList = mutableListOf<String>()

    private var currentIndex = 0
    private var isRunning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsPreBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initList()

        showRandomString()

        val animation = AlphaAnimation(1f, 0f)
        animation.duration = 1000
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.REVERSE
        binding.statusImage.startAnimation(animation)

    }

    private fun initList(){
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


    private fun showRandomString() {
        if (!isRunning) return // Uygulama durmuşsa fonksiyondan çık

        if (currentIndex < stringList.size) {
            val randomString = stringList[currentIndex]
            binding.statusText.text = randomString

            if (currentIndex == 25) {
                isRunning = false
                val result = stringList[currentIndex].toString()
                startActivity(Intent(this, MapsActivity::class.java)
                    .putExtra("resultPlace",result))
                return
            }

            currentIndex++
            binding.statusText.postDelayed({ showRandomString() }, 500)
        }
    }



}