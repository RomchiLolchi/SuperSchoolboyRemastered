package com.oftatech.superschoolboyremastered.dao

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.oftatech.superschoolboyremastered.activity.UIState
import com.oftatech.superschoolboyremastered.ui.theme.Madang

class MainSPDao(private val context: Context) {

    companion object {
        private const val DEFAULT_SP_NAME = "MainSP"

        private const val UI_THEME_SP_KEY = "ui_theme"
        private const val ACCENT_COLOR_SP_KEY = "accent_color"
        private const val FIRST_OPEN_SP_KEY = "is_first_open"
    }

    fun readAccentColor(): Color {
        return Color(context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getString(ACCENT_COLOR_SP_KEY, Madang.value.toString())!!.toULong())
    }

    fun writeAccentColor(newAccentColor: Color) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putString(ACCENT_COLOR_SP_KEY, newAccentColor.value.toString()).apply()
    }

    fun readUITheme(): UIState {
        return UIState.valueOf(context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getString(UI_THEME_SP_KEY, UIState.SystemSettings.toString())!!)
    }

    fun writeUITheme(newUIState: UIState) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putString(UI_THEME_SP_KEY, newUIState.toString()).apply()
    }

    fun readFirstOpen(): Boolean {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getBoolean(FIRST_OPEN_SP_KEY, true)
    }

    fun writeFirstOpen(firstOpen: Boolean) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putBoolean(FIRST_OPEN_SP_KEY, firstOpen).apply()
    }
}