package com.oftatech.superschoolboyremastered.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.oftatech.superschoolboyremastered.dao.SessionsSettingsSPDao
import com.oftatech.superschoolboyremastered.util.Application
import com.oftatech.superschoolboyremastered.viewmodel.SessionsSettingsViewModel.Companion.getInStandardIntForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

class TrainingViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionsSettingsViewModel = ViewModelProvider(application, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SessionsSettingsViewModel(SessionsSettingsSPDao(application.applicationContext)) as T
        }
    }).get(SessionsSettingsViewModel::class.java)
    var maximumTime = sessionsSettingsViewModel.timer.value!!.getInStandardIntForm()
    private var firstNumbers = ArrayList<Int>()
    val rightAnswers = MutableLiveData(0)
    val rightAnswersInRow = MutableLiveData(0)
    private val allRightAnswersInRow = arrayListOf(0)
    val wrongAnswers = MutableLiveData(0)
    val timeLeft = MutableLiveData(maximumTime)
    val responseTimes = ArrayList<Int>()
    val question = MutableLiveData("")
    val userAnswer = MutableLiveData("")
    private var rightAnswer = 0
    private var timer = Timer(true)

    val isRightAnswersIconTintStandard = MutableLiveData(true)
    val isWrongAnswersIconTintStandard = MutableLiveData(true)

    init {
        updateValuesFromSettings()
        /*setupAutoUpdate()
        for (pair in sessionsSettingsViewModel.numbers.value!!) {
            if (pair.value) {
                firstNumbers.add(pair.key)
            }
        }*/
    }

    fun updateValuesFromSettings() {
        sessionsSettingsViewModel.updateSettingsData()
        firstNumbers = ArrayList<Int>()
        for (pair in sessionsSettingsViewModel.numbers.value!!) {
            if (pair.value) {
                firstNumbers.add(pair.key)
            }
        }
        maximumTime = sessionsSettingsViewModel.timer.value!!.getInStandardIntForm()
    }

    /*private fun setupAutoUpdate() {
        sessionsSettingsViewModel.timer.observeForever {
            val oldMaximumTime = maximumTime
            maximumTime = sessionsSettingsViewModel.timer.value!!.getInStandardIntForm()
            Log.d("ViewModel debugging", "Maximum time has been changed due to change in external view model.\nOld value:$oldMaximumTime\nNew value:$maximumTime")
        }
        sessionsSettingsViewModel.numbers.observeForever {
            val oldNumbers = sessionsSettingsViewModel.numbers
            for (pair in it) {
                if (pair.value) {
                    firstNumbers.add(pair.key)
                }
            }
            Log.d("ViewModel debugging", "Allowed numbers has been changed due to change in external view model.\nOld value:$oldNumbers\nNew value:${sessionsSettingsViewModel.numbers}")
        }
    }*/

    private fun generateQuestionAndAnswer() {
        val firstNumber = firstNumbers.random()
        val secondNumber = (1..10).random()

        question.postValue("${firstNumber}x$secondNumber")
        rightAnswer = firstNumber * secondNumber
    }

    private fun checkAndAppend() {
        responseTimes.add(maximumTime - timeLeft.value!!)

        val parsedUserAnswer = if (userAnswer.value != null && userAnswer.value!!.isNotBlank()) {
            try {
                userAnswer.value!!.toLong()
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        } else {
            0
        }

        if (parsedUserAnswer == rightAnswer.toLong()) {
            rightAnswers.postValue(rightAnswers.value!! + 1)
            allRightAnswersInRow[allRightAnswersInRow.lastIndex] = allRightAnswersInRow.last() + 1
            viewModelScope.launch {
                startRightAnswersTintAnimation()
            }
        } else {
            wrongAnswers.postValue(wrongAnswers.value!! + 1)
            allRightAnswersInRow.add(0)
            viewModelScope.launch {
                startWrongAnswersTintAnimation()
            }
        }
    }

    fun updateRightAnswersInRow() {
        rightAnswersInRow.value = allRightAnswersInRow.maxOrNull() ?: 0
    }

    fun appendToUserAnswer(text: String) {
        if (userAnswer.value!! == "0" && text != "0") {
            userAnswer.postValue(text)
        } else {
            if (userAnswer.value != "0" || text != "0") {
                userAnswer.postValue(userAnswer.value + text)
            }
        }
    }

    fun clearLastUserAnswerSymbol() {
        if (userAnswer.value!!.isNotBlank()) {
            userAnswer.postValue(userAnswer.value!!.substring(0, userAnswer.value!!.length - 1))
        }
    }

    fun clearUserAnswer() {
        userAnswer.postValue("")
    }

    fun completeTask(check: Boolean = true) {
        if (check) checkAndAppend()
        stop()
        clearUserAnswer()
        timeLeft.postValue(maximumTime)
        generateQuestionAndAnswer()
        timer.schedule(timerTask {
            timeLeft.postValue(timeLeft.value!! - 1)
        },1000L, 1000L)
        timer.schedule(timerTask {
            completeTask()
        }, maximumTime * 1000L)
    }

    fun stop() {
        try {
            timer.cancel()
            timer.purge()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        timer = Timer(true)
    }

    private suspend fun startRightAnswersTintAnimation() {
        isRightAnswersIconTintStandard.value = false
        delay(500)
        isRightAnswersIconTintStandard.value = true
    }

    private suspend fun startWrongAnswersTintAnimation() {
        isWrongAnswersIconTintStandard.value = false
        delay(500)
        isWrongAnswersIconTintStandard.value = true
    }
}