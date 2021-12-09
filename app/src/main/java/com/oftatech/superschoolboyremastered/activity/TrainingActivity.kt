package com.oftatech.superschoolboyremastered.activity

import android.app.Activity
import android.os.Bundle
import android.service.autofill.OnClickAction
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oftatech.superschoolboyremastered.R
import com.oftatech.superschoolboyremastered.ui.PrimaryTextButton
import com.oftatech.superschoolboyremastered.ui.SecondaryTextButton
import com.oftatech.superschoolboyremastered.ui.theme.Madang
import com.oftatech.superschoolboyremastered.ui.theme.MainAppContent
import com.oftatech.superschoolboyremastered.ui.theme.robotoFontFamily
import com.oftatech.superschoolboyremastered.util.Utils
import com.oftatech.superschoolboyremastered.util.Utils.appSetup
import com.oftatech.superschoolboyremastered.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by viewModels<MainViewModel>()
        setContent {
            MainAppContent(
                darkTheme = Utils.isDarkTheme(viewModel.appTheme.observeAsState().value!!),
                accentColor = animateColorAsState(targetValue = viewModel.accentColor.observeAsState().value!!).value
            ) {
                appSetup()
                TrainingActivityContent()
            }
        }
    }

    //TODO Сделать диалог при нажатии кнопки назад
}

@Composable
private fun TrainingActivityContent(
    modifier: Modifier = Modifier,
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
                Image(
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
                )
                StatsBar()
            }
            NumericKeyboard(
                Modifier
                    .padding(horizontal = standardHorizontalPadding)
                    .width(standardWidth)
                    .weight(1f, fill = false),
            )
            BottomFunctionalButtonsRow(
                Modifier
                    .padding(horizontal = standardHorizontalPadding)
                    .padding(bottom = 20.dp)
                    .width(standardWidth),
            )
        }
    }
}

@Composable
private fun QuestionAndAnswer(
    modifier: Modifier = Modifier,
    firstRowModifier: Modifier = Modifier,
    secondRowModifier: Modifier = Modifier,
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
                text = "9x7",
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
                text = "63",
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontSize = 48.sp
            )
        }
    }
}

@Composable
private fun StatsBar(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp), color = MaterialTheme.colors.secondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier.size(23.dp),
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = stringResource(
                        id = R.string.time_left_text
                    )
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "24",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    fontSize = 17.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier.size(23.dp),
                    imageVector = Icons.Outlined.Done,
                    contentDescription = stringResource(
                        id = R.string.done_right_text
                    )
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "14",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    fontSize = 17.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier.size(23.dp),
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(
                        id = R.string.done_incorrect_text
                    )
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "10",
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
) {
    Column(
        modifier = modifier,
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
            KeyboardButton(text = "7") {

            }
            KeyboardButton(text = "8") {

            }
            KeyboardButton(text = "9") {

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

            }
            KeyboardButton(text = "5") {

            }
            KeyboardButton(text = "6") {

            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KeyboardButton(text = "1") {

            }
            KeyboardButton(text = "2") {

            }
            KeyboardButton(text = "3") {

            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KeyboardButton(text = "<-") {

            }
            KeyboardButton(text = "0") {

            }
            KeyboardButton(text = "X") {

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
    modifier: Modifier = Modifier
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

        }
    }
}