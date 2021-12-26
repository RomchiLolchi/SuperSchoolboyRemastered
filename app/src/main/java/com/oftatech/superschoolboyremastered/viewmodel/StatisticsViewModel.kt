package com.oftatech.superschoolboyremastered.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oftatech.superschoolboyremastered.dao.StatisticsSPDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val spDao: StatisticsSPDao) : ViewModel() {
    val absoluteRightAnswersInRow = MutableLiveData(0)
    val absoluteAverageResponseTime = MutableLiveData(0F)
    //ls = Last [Training] Session
    val lsTimer = MutableLiveData(0)
    val lsRightAnswers = MutableLiveData(0)
    val lsWrongAnswers = MutableLiveData(0)
    val lsRightAnswersInRow = MutableLiveData(0)
    val lsAverageResponseTime = MutableLiveData(0F)

    init {
        updateStatsData()
        configureDataAutosave()
    }

    fun writeStatsData(trainingViewModel: TrainingViewModel) {
        lsTimer.value = trainingViewModel.maximumTime
        lsRightAnswers.value = trainingViewModel.rightAnswers.value
        lsWrongAnswers.value = trainingViewModel.wrongAnswers.value
        val averageResponseTime = trainingViewModel.responseTimes.average().toFloat()
        lsAverageResponseTime.value = averageResponseTime
        absoluteAverageResponseTime.value = kotlin.math.min(absoluteAverageResponseTime.value ?: 0F, averageResponseTime)
        trainingViewModel.updateRightAnswersInRow()
        val rightAnswersInRow = trainingViewModel.rightAnswersInRow.value
        lsRightAnswersInRow.value = rightAnswersInRow
        absoluteRightAnswersInRow.value = kotlin.math.max(absoluteRightAnswersInRow.value ?: 0, rightAnswersInRow ?: 0)
    }

    fun updateStatsData() {
        absoluteRightAnswersInRow.value = spDao.readAbsoluteRightAnswersInRow()
        absoluteAverageResponseTime.value = spDao.readAbsoluteAverageResponseTime()
        lsTimer.value = spDao.readLsTimer()
        lsRightAnswers.value = spDao.readLsRightAnswers()
        lsWrongAnswers.value = spDao.readLsWrongAnswers()
        lsRightAnswersInRow.value = spDao.readLsRightAnswersInRow()
        lsAverageResponseTime.value = spDao.readLsAverageResponseTime()
    }

    fun isEmpty(): Boolean {
        return absoluteRightAnswersInRow.value == 0 &&
        absoluteAverageResponseTime.value == 0F &&
        lsTimer.value == 0 &&
        lsRightAnswers.value == 0 &&
        lsWrongAnswers.value == 0 &&
        lsRightAnswersInRow.value == 0 &&
        lsAverageResponseTime.value == 0F
    }

    private fun configureDataAutosave() {
        absoluteRightAnswersInRow.observeForever {
            spDao.writeAbsoluteRightAnswersInRow(it)
        }
        absoluteAverageResponseTime.observeForever {
            spDao.writeAbsoluteAverageResponseTime(it)
        }
        lsTimer.observeForever {
            spDao.writeLsTimer(it)
        }
        lsRightAnswers.observeForever {
            spDao.writeLsRightAnswers(it)
        }
        lsWrongAnswers.observeForever {
            spDao.writeLsWrongAnswers(it)
        }
        lsRightAnswersInRow.observeForever {
            spDao.writeLsRightAnswersInRow(it)
        }
        lsAverageResponseTime.observeForever {
            spDao.writeLsAverageResponseTime(it)
        }
    }
}