package com.oftatech.superschoolboyremastered.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oftatech.superschoolboyremastered.dao.SessionsSettingsSPDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class SessionsSettingsViewModel @Inject constructor(private val spDao: SessionsSettingsSPDao) : ViewModel() {

    companion object {
        @JvmStatic
        fun Float.getInStandardIntForm(): Int {
            return (this * 100).roundToInt()
        }
    }

    val ranked = MutableLiveData(false)
    val timer = MutableLiveData(0.1f)
    val numbers = MutableLiveData(
        mutableStateMapOf(
            1 to true,
            2 to true,
            3 to true,
            4 to true,
            5 to true,
            6 to true,
            7 to true,
            8 to true,
            9 to true,
            10 to true
        )
    )
    val difficulty = MutableLiveData(0.01f)
    val amountOfExamplesRestrictionActive = MutableLiveData(false)
    val amountOfExamplesRestriction = MutableLiveData(0.01F)
    val timerRestrictionActive = MutableLiveData(false)
    val timerRestriction = MutableLiveData(1F) //Minutes

    init {
        updateSettingsData()
        configureDataAutosave()
    }

    fun updateSettingsData() {
        ranked.value = spDao.readRanked()
        timer.value = spDao.readTimer()
        numbers.value = spDao.readNumbers()
        difficulty.value = spDao.readDifficulty()
        amountOfExamplesRestrictionActive.value = spDao.readAmountOfExamplesRestrictionActive()
        amountOfExamplesRestriction.value = spDao.readAmountOfExamplesRestriction()
        timerRestrictionActive.value = spDao.readTimerRestrictionActive()
        timerRestriction.value = spDao.readTimerRestriction()
    }

    fun writeNewNumbers(newNumbers: SnapshotStateMap<Int, Boolean>) {
        spDao.writeNumbers(numbers.value!!)
    }

    private fun configureDataAutosave() {
        ranked.observeForever {
            spDao.writeRanked(it)
        }
        timer.observeForever {
            spDao.writeTimer(it)
        }
        numbers.observeForever {
            spDao.writeNumbers(it)
        }
        difficulty.observeForever {
            spDao.writeDifficulty(it)
        }
        amountOfExamplesRestrictionActive.observeForever {
            spDao.writeAmountOfExamplesRestrictionActive(it)
        }
        amountOfExamplesRestriction.observeForever {
            spDao.writeAmountOfExamplesRestriction(it)
        }
        timerRestrictionActive.observeForever {
            spDao.writeTimerRestrictionActive(it)
        }
        timerRestriction.observeForever {
            spDao.writeTimerRestriction(it)
        }
    }
}