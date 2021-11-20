package com.oftatech.superschoolboyremastered.util

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

object Utils {

    @SuppressLint("ComposableNaming")
    @Composable
    fun appSetup() {
        //TODO Здесь обновлять secondary color
        SetStatusBarColorFromComposeColor(color = MaterialTheme.colors.background)
        SetAndroidNavigationBarBarColorFromComposeColor(color = MaterialTheme.colors.background)
    }

    @Composable
    fun SetStatusBarColorFromComposeColor(color: Color) {
        val activity = LocalContext.current as ComponentActivity
        activity.window.statusBarColor =
            android.graphics.Color.argb(color.alpha, color.red, color.green, color.blue)
    }

    @Composable
    fun SetAndroidNavigationBarBarColorFromComposeColor(color: Color) {
        val activity = LocalContext.current as ComponentActivity
        val androidColor = android.graphics.Color.argb(color.alpha, color.red, color.green, color.blue)
        activity.window.navigationBarColor = androidColor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.window.navigationBarDividerColor = androidColor
        }
    }
}