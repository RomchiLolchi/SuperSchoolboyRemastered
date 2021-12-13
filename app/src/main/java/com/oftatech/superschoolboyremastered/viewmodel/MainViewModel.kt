package com.oftatech.superschoolboyremastered.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oftatech.superschoolboyremastered.activity.UIState
import com.oftatech.superschoolboyremastered.dao.MainSPDao
import com.oftatech.superschoolboyremastered.ui.theme.Madang
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val spDao: MainSPDao) : ViewModel() {
    var appTheme = MutableLiveData<UIState>(UIState.SystemSettings)
    var accentColor = MutableLiveData(Madang)
    var firstOpen = MutableLiveData(true)

    init {
        updateSettingsData()
        configureDataAutosave()
    }

    private fun updateSettingsData() {
        appTheme.value = spDao.readUITheme()
        accentColor.value = spDao.readAccentColor()
        firstOpen.value = spDao.readFirstOpen()
    }

    private fun configureDataAutosave() {
        appTheme.observeForever {
            spDao.writeUITheme(it)
        }
        accentColor.observeForever {
            spDao.writeAccentColor(it)
        }
        firstOpen.observeForever {
            spDao.writeFirstOpen(it)
        }
    }
}