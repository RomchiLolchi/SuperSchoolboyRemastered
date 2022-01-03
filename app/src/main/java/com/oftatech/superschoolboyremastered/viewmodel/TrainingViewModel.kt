package com.oftatech.superschoolboyremastered.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask

@HiltViewModel
class TrainingViewModel @Inject constructor() : ViewModel() {
    var maximumTime = 50
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

    private fun generateQuestionAndAnswer() {
        val firstNumber = (1..10).random()
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
        } else {
            wrongAnswers.postValue(wrongAnswers.value!! + 1)
            allRightAnswersInRow.add(0)
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
}