package com.oftatech.superschoolboyremastered.util

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class Application : Application() {
    init {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}