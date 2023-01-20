package com.example.testumt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails

class MainActivity : AppCompatActivity() {

    private lateinit var referrerClient: InstallReferrerClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUtmTracking()
    }
    private fun initUtmTracking() {
        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                        obtainReferrerDetails()
                        referrerClient.endConnection()
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        // API not available on the current Play Store app.
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        // Connection couldn't be established.
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun obtainReferrerDetails(){
        val utmTags = arrayListOf<String>("utm_source","utm_medium","utm_campaign","utm_content","utm_term")
        val hashMap:HashMap<String,String> = HashMap()
        val response: ReferrerDetails = referrerClient.installReferrer
        Log.d("response - ", "response - ${response.toString()}")
        val referrerUrl: String = response.installReferrer
        Log.d("referrerUrl - ", "referrerUrl - $referrerUrl")
        val utmList = referrerUrl.split('&')
            utmList.forEachIndexed() {index,substring->
                hashMap[utmTags[index]] = substring.split("=")[1]
            }
        for((key,value) in hashMap){
            Log.d("UTM_TAG","UTM_TAG:$key = $value")
        }

    }
}