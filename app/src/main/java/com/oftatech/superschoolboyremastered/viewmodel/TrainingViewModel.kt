package com.oftatech.superschoolboyremastered.viewmodel

import android.content.Context
import android.content.res.Resources
import android.inputmethodservice.Keyboard
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.firebase.analytics.FirebaseAnalytics
import com.oftatech.superschoolboyremastered.R
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

class TrainingViewModel(val application: Application) : AndroidViewModel(application) {
    private val sessionsSettingsViewModel =
        ViewModelProvider(application, object : ViewModelProvider.Factory {
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
    val showEndingDialog = MutableLiveData(false)

    val isRightAnswersIconTintStandard = MutableLiveData(true)
    val isWrongAnswersIconTintStandard = MutableLiveData(true)

    init {
        updateValuesFromSettings()
    }

    fun updateValuesFromSettings() {
        sessionsSettingsViewModel.updateSettingsData()
        if (!sessionsSettingsViewModel.ranked.value!!) {
            firstNumbers = ArrayList()
            for (pair in sessionsSettingsViewModel.numbers.value!!) {
                if (pair.value) {
                    firstNumbers.add(pair.key)
                }
            }
            maximumTime = sessionsSettingsViewModel.timer.value!!.getInStandardIntForm()
        } else {
            firstNumbers = arrayListOf(2, 3, 4, 5, 6, 7, 8, 9)
            when (sessionsSettingsViewModel.difficulty.value!!.getInStandardIntForm()) {
                1 -> {
                    maximumTime = 20
                }
                2 -> {
                    maximumTime = 10
                }
                3 -> {
                    maximumTime = 5
                }
            }
        }
    }

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
        if (userAnswer.value!!.length < 4) {
            if (userAnswer.value!! == "0" && text != "0") {
                userAnswer.postValue(text)
            } else {
                if (userAnswer.value != "0" || text != "0") {
                    userAnswer.postValue(userAnswer.value + text)
                }
            }
        } else {
            Toast.makeText(
                application.applicationContext,
                R.string.answer_is_too_big_text,
                Toast.LENGTH_LONG
            ).show()
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
        }, 1000L, 1000L)
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

    fun sendResultsToLeaderboard(rightAnswersInRowAmount: Long) {
        Games.getLeaderboardsClient(
            application.applicationContext,
            GoogleSignIn.getLastSignedInAccount(application.applicationContext)!!
        )
            .submitScore(
                when (sessionsSettingsViewModel.difficulty.value!!.getInStandardIntForm()) {
                    1 -> application.applicationContext.getString(R.string.leaderboard_maximum_right_answers_in_row_on_1st_difficulty)
                    2 -> application.applicationContext.getString(R.string.leaderboard_maximum_right_answers_in_row_on_2nd_difficulty)
                    3 -> application.applicationContext.getString(R.string.leaderboard_maximum_right_answers_in_row_on_3rd_difficulty)
                    else -> throw RuntimeException("Impossible. There are no more difficulties.")
                }, rightAnswersInRowAmount
            )
    }

    @Composable
    fun showEndingDialog(
        modifier: Modifier = Modifier,
        onBackPressed: () -> Unit,
        restartTraining: () -> Unit,
        goToStatistics: () -> Unit,
    ) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = {
                onBackPressed()
            },
            title = {
                Text(
                    text = getDialogHeader(
                        application.resources,
                        getPercentageOfRightAnswers(
                            rightAnswers.value!!,
                            rightAnswers.value!! + wrongAnswers.value!!
                        )
                    ),
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "${stringResource(id = R.string.right_answers_text)}: ${rightAnswers.value}\n${
                        stringResource(
                            id = R.string.wrong_answers_text
                        )
                    }: ${wrongAnswers.value}",
                    color = MaterialTheme.colors.onBackground,
                )
            },
            buttons = {
                Column(
                    modifier = Modifier.padding(start = 24.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    TextButton(
                        onClick = {
                            onBackPressed()
                        }
                    ) {
                        Text(
                            modifier = Modifier.alpha(0.7f),
                            text = stringResource(id = R.string.close_text).uppercase(),
                            color = MaterialTheme.colors.onBackground,
                        )
                    }
                    TextButton(onClick = {
                        goToStatistics()
                    }) {
                        Text(
                            text = stringResource(id = R.string.to_statistics_text).uppercase(),
                            color = MaterialTheme.colors.onBackground,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    TextButton(onClick = {
                        FirebaseAnalytics.getInstance(application.applicationContext)
                            .logEvent("training_session_start", null)
                        restartTraining()
                    }) {
                        Text(
                            text = stringResource(id = R.string.restart_text).uppercase(),
                            color = MaterialTheme.colors.secondary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            },
            backgroundColor = MaterialTheme.colors.background,
        )
    }

    private fun getPercentageOfRightAnswers(rightAnswers: Int, allAnswers: Int): Int {
        if (allAnswers == 0) return 0
        return (rightAnswers / allAnswers) * 100
    }

    private fun getDialogHeader(resources: Resources, rightAnswersPercentage: Int): String {
        val stringArray = when {
            rightAnswersPercentage >= 80 -> resources.getStringArray(R.array.good_results_final_texts)
            rightAnswersPercentage in 50..79 -> resources.getStringArray(R.array.average_results_final_texts)
            rightAnswersPercentage < 50 -> resources.getStringArray(R.array.bad_results_final_texts)
            else -> throw RuntimeException("Impossible.")
        }
        return stringArray.random()
    }
}