package com.oftatech.superschoolboyremastered.dao

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap

class SessionsSettingsSPDao(private val context: Context) {

    companion object {
        private const val DEFAULT_SP_NAME = "SessionSettingsSP"

        private const val RANKED_SP_KEY = "ranked"
        private const val TIMER_SP_KEY = "timer"
        private const val NUMBERS_SP_KEY = "numbers"
        private const val DIFFICULTY_SP_KEY = "difficulty"
    }

    fun readRanked(): Boolean {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getBoolean(RANKED_SP_KEY, false)
    }

    fun writeRanked(newRanked: Boolean) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putBoolean(RANKED_SP_KEY, newRanked).apply()
    }

    fun readTimer(): Float {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getFloat(TIMER_SP_KEY, 0.1f)
    }

    fun writeTimer(newTimer: Float) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putFloat(TIMER_SP_KEY, newTimer).apply()
    }

    fun readNumbers(): SnapshotStateMap<Int, Boolean> {
        val basicStringMap = mutableStateMapOf(1 to true, 2 to true, 3 to true, 4 to true, 5 to true, 6 to true, 7 to true, 8 to true, 9 to true, 10 to true).map { "${it.key}=${it.value}" }.joinToString(separator = ",")
        val stringMap = context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getString(NUMBERS_SP_KEY, basicStringMap)
        return stringMap!!.split(",").associateTo(SnapshotStateMap()) {
            val (key, value) = it.split("=")
            key.toInt() to value.toBoolean()
        }
    }

    fun writeNumbers(newNumbers: SnapshotStateMap<Int, Boolean>) {
        val newNumbersString = newNumbers.map { "${it.key}=${it.value}" }.joinToString(separator = ",")
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putString(NUMBERS_SP_KEY, newNumbersString).apply()
    }

    fun readDifficulty(): Float {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getFloat(DIFFICULTY_SP_KEY, 0.01f)
    }

    fun writeDifficulty(newDifficulty: Float) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putFloat(DIFFICULTY_SP_KEY, newDifficulty).apply()
    }
}