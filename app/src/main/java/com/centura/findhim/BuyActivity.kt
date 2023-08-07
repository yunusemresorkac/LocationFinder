package com.centura.findhim

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import com.android.billingclient.api.*
import com.centura.findhim.databinding.ActivityBuyBinding
import com.yeslab.fastprefs.FastPrefs

class BuyActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBuyBinding


    private var billingClient: BillingClient? = null
    private var productDetailsIdTwo: SkuDetails? = null
    private var productDetailsIdOne: SkuDetails? = null

    private var purchase: Purchase? = null
    private val iapIdTwo = Constants.IAP_ID_TWO
    private val iapIdOne = Constants.IAP_ID_ONE
    private val TAG_IAP = "InAppPurchaseTag"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        billingSetup()


        binding.continueBtn.setOnClickListener {


//            saveBuy()
//            startActivity(Intent(this,MapsActivity::class.java))

            when (binding.subsType.position) {
                0 -> {
                    makePurchase(iapIdOne)
                }
                1 -> {
                    makePurchase(iapIdTwo)
                }

            }
        }



    }

    private fun billingSetup() {
        billingClient = BillingClient.newBuilder(this@BuyActivity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(
                @NonNull billingResult: BillingResult
            ) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG_IAP, "OnBillingSetupFinish connected")
                    queryProductDetails()
                } else {
                    Log.i(TAG_IAP, "OnBillingSetupFinish failed")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.i(TAG_IAP, "OnBillingSetupFinish connection lost")
            }
        })
    }

    private fun queryProductDetails() {
        val skuList: MutableList<String> = ArrayList()
        skuList.add(iapIdTwo)
        skuList.add(iapIdOne)
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient!!.querySkuDetailsAsync(params) { billingResult: BillingResult, skuDetailsList: List<SkuDetails>? ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    when (skuDetails.sku) {
                        iapIdOne -> {
                            productDetailsIdOne = skuDetails
                            val price = productDetailsIdOne!!.price
                            runOnUiThread {
                                binding.typeOne.text = getString(R.string.onevalue) + " " + price
                            }
                            println("fiyat $price")
                        }
                        iapIdTwo -> {
                            productDetailsIdTwo = skuDetails
                            val price = productDetailsIdTwo!!.price
                            runOnUiThread {
                                binding.typeTwo.text = getString(R.string.twovalue) + " " + price
                            }
                            println("fiyat $price")
                        }
                    }
                }
            }
        }
    }

    private fun makePurchase(productId: String) {
        val productDetails = if (productId == iapIdTwo) productDetailsIdTwo else productDetailsIdOne
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(productDetails!!)
            .build()
        billingClient!!.launchBillingFlow(this, billingFlowParams)
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult: BillingResult, purchases: List<Purchase>? ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    completePurchase(purchase)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                Log.i(TAG_IAP, "onPurchasesUpdated: Purchase Canceled")
            } else {
                Log.i(TAG_IAP, "onPurchasesUpdated: Error")
            }
        }

    private fun completePurchase(item: Purchase) {
        purchase = item
        if (purchase!!.purchaseState == Purchase.PurchaseState.PURCHASED) {
            runOnUiThread {
                consumePurchase()
            }
        }
    }

    private fun consumePurchase() {


        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase!!.purchaseToken)
            .build()
        val listener =
            ConsumeResponseListener { billingResult, purchaseToken ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    runOnUiThread {
                       saveBuy()
                    }
                }
            }
        billingClient!!.consumeAsync(consumeParams, listener)
    }

    private fun saveBuy(){
        val prefs = FastPrefs(this)
        val myCoin = prefs.getInt("coin",0)

        when (binding.subsType.position) {
            0 -> {
                val result = myCoin + Constants.COIN_VALUE_ONE
                prefs.setInt("coin" , result)

            }
            1 -> {
                val result = myCoin + Constants.COIN_VALUE_TWO

                prefs.setInt("coin" , myCoin + result)
            }

        }
        val intent = Intent(this,MapsActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        billingClient?.endConnection()
    }


}