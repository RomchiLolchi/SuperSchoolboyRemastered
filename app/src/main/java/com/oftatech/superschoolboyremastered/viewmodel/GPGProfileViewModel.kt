package com.oftatech.superschoolboyremastered.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oftatech.superschoolboyremastered.dao.GPGProfileSPDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GPGProfileViewModel @Inject constructor(private val spDao: GPGProfileSPDao) : ViewModel() {
    val username = MutableLiveData<String>()
    val avatar = MutableLiveData<Bitmap?>()
    val rank = MutableLiveData<String>()

    init {
        updateProfileData()
        configureDataAutosave()
    }

    fun deleteUserData() {
        spDao.deleteAllData()
        updateProfileData()
    }

    private fun updateProfileData() {
        username.value = spDao.readUserUsername()
        avatar.value = spDao.readUserAvatar()
        rank.value = spDao.readUserRank()
    }

    private fun configureDataAutosave() {
        username.observeForever {
            spDao.writeUserUsername(it)
        }
        avatar.observeForever {
            spDao.writeUserAvatar(it)
        }
        rank.observeForever {
            spDao.writeUserRank(it)
        }
    }
}