package com.oftatech.superschoolboyremastered.dao

import android.content.Context

class StatisticsSPDao(val context: Context) {
    companion object {
        private const val DEFAULT_SP_NAME = "StatsSP"

        private const val ABSOLUTE_RIGHT_ANSWERS_IN_ROW_KEY = "absolute_right_answers_in_row"
        private const val ABSOLUTE_AVERAGE_RESPONSE_TIME_KEY = "absolute_average_response_time"
        private const val LS_TIMER_KEY = "ls_timer"
        private const val LS_RIGHT_ANSWERS_KEY = "ls_right_answers"
        private const val LS_WRONG_ANSWERS_KEY = "ls_wrong_answers"
        private const val LS_RIGHT_ANSWERS_IN_ROW_KEY = "ls_right_answers_in_row"
        private const val LS_AVERAGE_RESPONSE_TIME = "ls_average_response_time"
    }

    fun readAbsoluteRightAnswersInRow(): Int {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getInt(ABSOLUTE_RIGHT_ANSWERS_IN_ROW_KEY, 0)
    }

    fun writeAbsoluteRightAnswersInRow(newAbsoluteRightAnswersInRow: Int) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putInt(ABSOLUTE_RIGHT_ANSWERS_IN_ROW_KEY, newAbsoluteRightAnswersInRow).apply()
    }

    fun readAbsoluteAverageResponseTime(): Float {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getFloat(ABSOLUTE_AVERAGE_RESPONSE_TIME_KEY, 0F)
    }

    fun writeAbsoluteAverageResponseTime(newAbsoluteAverageResponseTime: Float) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putFloat(ABSOLUTE_AVERAGE_RESPONSE_TIME_KEY, newAbsoluteAverageResponseTime).apply()
    }

    fun readLsTimer(): Int {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getInt(LS_TIMER_KEY, 0)
    }

    fun writeLsTimer(newLsTimer: Int) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putInt(LS_TIMER_KEY, newLsTimer).apply()
    }

    fun readLsRightAnswers(): Int {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getInt(LS_RIGHT_ANSWERS_KEY, 0)
    }

    fun writeLsRightAnswers(newLsRightAnswers: Int) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putInt(LS_RIGHT_ANSWERS_KEY, newLsRightAnswers).apply()
    }

    fun readLsWrongAnswers(): Int {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getInt(LS_WRONG_ANSWERS_KEY, 0)
    }

    fun writeLsWrongAnswers(newLsWrongAnswers: Int) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putInt(LS_WRONG_ANSWERS_KEY, newLsWrongAnswers).apply()
    }

    fun readLsRightAnswersInRow(): Int {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getInt(LS_RIGHT_ANSWERS_IN_ROW_KEY, 0)
    }

    fun writeLsRightAnswersInRow(newLsRightAnswersInRow: Int) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putInt(LS_RIGHT_ANSWERS_IN_ROW_KEY, newLsRightAnswersInRow).apply()
    }

    fun readLsAverageResponseTime(): Float {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).getFloat(LS_AVERAGE_RESPONSE_TIME, 0F)
    }

    fun writeLsAverageResponseTime(newLsAverageResponseTime: Float) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE).edit().putFloat(LS_AVERAGE_RESPONSE_TIME, newLsAverageResponseTime).apply()
    }
}