package com.oftatech.superschoolboyremastered.util

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.oftatech.superschoolboyremastered.R
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class Application : Application(), ViewModelStoreOwner {

    private val modelStore by lazy { ViewModelStore() }
    override fun getViewModelStore(): ViewModelStore {
        return modelStore
    }

    override fun onCreate() {
        super.onCreate()

        configureAppMetrica()
        if (packageName.equals(processName())) {
            configureAndUpdateFirebaseRemoteConfig()
            configureAds()
        }
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

    private fun configureAppMetrica() {
        val config = YandexMetricaConfig.newConfigBuilder("a3ce8095-109b-4d74-b92e-fb8cd419fc98").build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }

    private fun processName(): String? {
        val mypid = Process.myPid()
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = manager.runningAppProcesses
        for (info in infos) {
            if (info.pid == mypid) {
                return info.processName
            }
        }
        return null
    }
}