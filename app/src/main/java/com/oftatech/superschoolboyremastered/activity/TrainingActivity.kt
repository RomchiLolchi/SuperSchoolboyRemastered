package com.oftatech.superschoolboyremastered.activity

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oftatech.superschoolboyremastered.R
import com.oftatech.superschoolboyremastered.ui.PrimaryTextButton
import com.oftatech.superschoolboyremastered.ui.SecondaryTextButton
import com.oftatech.superschoolboyremastered.ui.theme.MainAppContent
import com.oftatech.superschoolboyremastered.ui.theme.MintGreen
import com.oftatech.superschoolboyremastered.ui.theme.Red
import com.oftatech.superschoolboyremastered.ui.theme.robotoFontFamily
import com.oftatech.superschoolboyremastered.util.Utils
import com.oftatech.superschoolboyremastered.util.Utils.appSetup
import com.oftatech.superschoolboyremastered.viewmodel.MainViewModel
import com.oftatech.superschoolboyremastered.viewmodel.StatisticsViewModel
import com.oftatech.superschoolboyremastered.viewmodel.TrainingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingActivity : ComponentActivity() {

    private val trainingViewModel by viewModels<TrainingViewModel>()
    private val statsViewModel by viewModels<StatisticsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val mainViewModel by viewModels<MainViewModel>()
        setContent {
            MainAppContent(
                darkTheme = Utils.isDarkTheme(mainViewModel.appTheme.observeAsState().value!!),
                accentColor = animateColorAsState(targetValue = mainViewModel.accentColor.observeAsState().value!!).value
            ) {
                appSetup()
                val standardColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                TrainingActivityContent(
                    question = trainingViewModel.question.observeAsState().value!!,
                    userAnswer = trainingViewModel.userAnswer.observeAsState().value!!,
                    timeLeft = trainingViewModel.timeLeft.observeAsState().value!!,
                    correctAnswers = trainingViewModel.rightAnswers.observeAsState().value!!,
                    incorrectAnswers = trainingViewModel.wrongAnswers.observeAsState().value!!,
                    onTextButtonClicked = {
                        trainingViewModel.appendToUserAnswer(it)
                    },
                    onClearLastClicked = {
                        trainingViewModel.clearLastUserAnswerSymbol()
                    },
                    onClearAllClicked = {
                        trainingViewModel.clearUserAnswer()
                    },
                    actionOnSubmitPressed = {
                        trainingViewModel.completeTask()
                    },
                    rightAnswersIconTint = if (trainingViewModel.isRightAnswersIconTintStandard.observeAsState().value == true) {
                        animateColorAsState(targetValue = standardColor)
                    } else {
                        animateColorAsState(targetValue = MintGreen)
                    }.value,
                    wrongAnswersIconTint = if (trainingViewModel.isWrongAnswersIconTintStandard.observeAsState().value == true) {
                        animateColorAsState(targetValue = standardColor)
                    } else {
                        animateColorAsState(targetValue = Red)
                    }.value,
                )
            }
            trainingViewModel.completeTask(check = false)
        }
    }

    override fun onBackPressed() {
        trainingViewModel.stop()
        statsViewModel.writeStatsData(trainingViewModel)
        super.onBackPressed()
    }
}

@Composable
private fun TrainingActivityContent(
    modifier: Modifier = Modifier,
    question: String,
    userAnswer: String,
    timeLeft: Int,
    correctAnswers: Int,
    incorrectAnswers: Int,
    onTextButtonClicked: (String) -> Unit,
    onClearLastClicked: () -> Unit,
    onClearAllClicked: () -> Unit,
    actionOnSubmitPressed: () -> Unit,
    rightAnswersIconTint: Color,
    wrongAnswersIconTint: Color,
) {
    val activity = LocalContext.current as Activity

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            activity.onBackPressed()
                        },
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_text),
                )
            }
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            val standardWidth = 380.dp
            val standardHorizontalPadding = 16.dp

            Column {
                QuestionAndAnswer(
                    modifier = Modifier.padding(top = 16.dp),
                    firstRowModifier = Modifier
                        .padding(horizontal = 16.dp),
                    secondRowModifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 20.dp),
                    question = question,
                    userAnswer = userAnswer,
                )
                StatsBar(
                    correctAnswers = correctAnswers,
                    incorrectAnswers = incorrectAnswers,
                    timeLeft = timeLeft,
                    rightAnswersIconTint = rightAnswersIconTint,
                    wrongAnswersIconTint = wrongAnswersIconTint,
                )
            }
            NumericKeyboard(
                Modifier
                    .padding(horizontal = standardHorizontalPadding)
                    .width(standardWidth)
                    .weight(1f, fill = false),
                onTextButtonClicked = onTextButtonClicked,
                onClearLastClicked = onClearLastClicked,
                onClearAllClicked = onClearAllClicked,
            )
            BottomFunctionalButtonsRow(
                Modifier
                    .padding(horizontal = standardHorizontalPadding)
                    .padding(bottom = 20.dp)
                    .width(standardWidth),
                actionOnSubmitPressed = actionOnSubmitPressed,
            )
        }
    }
}

@Composable
private fun QuestionAndAnswer(
    modifier: Modifier = Modifier,
    firstRowModifier: Modifier = Modifier,
    secondRowModifier: Modifier = Modifier,
    question: String,
    userAnswer: String,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = firstRowModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = question,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontSize = 48.sp,
                color = MaterialTheme.colors.secondary
            )
        }

        Row(
            modifier = secondRowModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = userAnswer,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontSize = 48.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun StatsBar(
    modifier: Modifier = Modifier,
    correctAnswers: Int,
    incorrectAnswers: Int,
    timeLeft: Int,
    rightAnswersIconTint: Color,
    wrongAnswersIconTint: Color,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp), color = MaterialTheme.colors.secondary
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(23.dp),
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = stringResource(
                        id = R.string.timer_text
                    )
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = timeLeft.toString(),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    fontSize = 17.sp
                )
            }

            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(23.dp),
                    imageVector = Icons.Outlined.Done,
                    contentDescription = stringResource(
                        id = R.string.right_answers_text
                    ),
                    tint = rightAnswersIconTint,
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = correctAnswers.toString(),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    fontSize = 17.sp
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(23.dp),
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(
                        id = R.string.wrong_answers_text
                    ),
                    tint = wrongAnswersIconTint,
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = incorrectAnswers.toString(),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    fontSize = 17.sp
                )
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp), color = MaterialTheme.colors.secondary
        )
    }
}

@Composable
private fun NumericKeyboard(
    modifier: Modifier = Modifier,
    onTextButtonClicked: (String) -> Unit,
    onClearLastClicked: () -> Unit,
    onClearAllClicked: () -> Unit,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KeyboardButton(text = "1") {
                onTextButtonClicked("1")
            }
            KeyboardButton(text = "2") {
                onTextButtonClicked("2")
            }
            KeyboardButton(text = "3") {
                onTextButtonClicked("3")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KeyboardButton(text = "4") {
                onTextButtonClicked("4")
            }
            KeyboardButton(text = "5") {
                onTextButtonClicked("5")
            }
            KeyboardButton(text = "6") {
                onTextButtonClicked("6")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KeyboardButton(text = "7") {
                onTextButtonClicked("7")
            }
            KeyboardButton(text = "8") {
                onTextButtonClicked("8")
            }
            KeyboardButton(text = "9") {
                onTextButtonClicked("9")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KeyboardButton(text = "<-") {
                onClearLastClicked()
            }
            KeyboardButton(text = "0") {
                onTextButtonClicked("0")
            }
            KeyboardButton(text = "X") {
                onClearAllClicked()
            }
        }
    }
}

@Composable
private fun KeyboardButton(
    modifier: Modifier = Modifier,
    text: String,
    onClickAction: () -> Unit,
) {
    PrimaryTextButton(
        modifier = modifier
            .width(99.dp),
        text = text,
        onClick = onClickAction,
        textSize = 23.sp,
        textVerticalPadding = 13.dp,
    )
}

@Composable
private fun BottomFunctionalButtonsRow(
    modifier: Modifier = Modifier,
    actionOnSubmitPressed: () -> Unit,
) {
    val activity = LocalContext.current as Activity

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        SecondaryTextButton(
            modifier = Modifier
                .width(170.dp),
            text = stringResource(id = R.string.exit_text).uppercase(),
        ) {
            activity.onBackPressed()
        }

        PrimaryTextButton(
            modifier = Modifier
                .width(170.dp),
            text = stringResource(id = R.string.submit_text).uppercase(),
        ) {
            actionOnSubmitPressed()
        }
    }
}