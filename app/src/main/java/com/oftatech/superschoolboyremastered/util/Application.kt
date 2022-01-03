package com.oftatech.superschoolboyremastered.util

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.oftatech.superschoolboyremastered.R
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        configureAndUpdateFirebaseRemoteConfig()
        configureAds()
    }

    private fun configureAds() {
        MobileAds.initialize(this)
        MobileAds.setRequestConfiguration(
            RequestConfiguration
                .Builder()
                .setTestDeviceIds(listOf("9BD6F6AA3C1820EF4220355836618F22")).build())
    }
    private fun configureAndUpdateFirebaseRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.firebase_remote_config_defaults)
        remoteConfig.fetchAndActivate()
    }
}