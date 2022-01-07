package com.oftatech.superschoolboyremastered.dao

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.oftatech.superschoolboyremastered.R
import java.io.ByteArrayOutputStream

class GPGProfileSPDao(val context: Context) {
    companion object {
        private const val DEFAULT_SP_NAME = "GPGProfileSP"

        private const val USER_AVATAR = "user_avatar"
        private const val USER_USERNAME = "user_username"
        private const val USER_RANK = "user_rank"
    }

    fun readUserAvatar(): Bitmap? {
        val stringImage = context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            .getString(USER_AVATAR, null) ?: return null
        val byteArrayImage = Base64.decode(stringImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArrayImage, 0, byteArrayImage.size)
    }

    fun writeUserAvatar(avatar: Bitmap?) {
        val baos = ByteArrayOutputStream()
        avatar?.compress(Bitmap.CompressFormat.PNG, 100, baos) ?: run {
            context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(USER_AVATAR, null)
                .apply()
            return
        }
        val decodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(USER_AVATAR, decodedImage)
            .apply()
    }

    fun readUserUsername(): String {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            .getString(USER_USERNAME, context.getString(R.string.log_in_with_google_play_games_text)).toString()
    }

    fun writeUserUsername(username: String) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(USER_USERNAME, username)
            .apply()
    }

    fun readUserRank(): String {
        return context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            .getString(USER_RANK, "-").toString()
    }

    fun writeUserRank(rank: String) {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(USER_RANK, rank)
            .apply()
    }

    fun deleteAllData() {
        context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(USER_AVATAR)
            .remove(USER_USERNAME)
            .remove(USER_RANK)
            .apply()
    }
}