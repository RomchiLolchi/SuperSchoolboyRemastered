package com.oftatech.superschoolboyremastered.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oftatech.superschoolboyremastered.R
import com.oftatech.superschoolboyremastered.ui.PrimaryTextButton
import com.oftatech.superschoolboyremastered.ui.SecondaryTextButton
import com.oftatech.superschoolboyremastered.ui.theme.Madang
import com.oftatech.superschoolboyremastered.ui.theme.MainAppContent
import com.oftatech.superschoolboyremastered.util.Utils
import com.oftatech.superschoolboyremastered.util.Utils.appSetup
import com.oftatech.superschoolboyremastered.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by viewModels<MainViewModel>()
        setContent {
            MainAppContent(
                darkTheme = Utils.isDarkTheme(viewModel.appTheme.observeAsState().value!!),
                accentColor = animateColorAsState(targetValue = viewModel.accentColor.observeAsState().value!!).value
            ) {
                appSetup()
                LoginActivityScreenContent()
            }
        }
    }
}

@Preview
@Composable
private fun LoginActivityScreenContent() {
    val activity = LocalContext.current as Activity

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = buildAnnotatedString {
                    append("${stringResource(id = R.string.welcome_to_text)}\n")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        withStyle(style = SpanStyle(color = MaterialTheme.colors.secondary)) {
                            append(stringResource(id = R.string.superschoolboy_text))
                        }
                        append(" ${stringResource(id = R.string.remastered_text)}")
                    }
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1.copy(fontSize = 28.sp)
            )
            Spacer(modifier = Modifier.height(59.dp))
            Text(
                text = stringResource(id = R.string.better_version_text),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2
            )
        }

        //TODO Центрировать лого
        Image(
            modifier = Modifier.size(246.dp),
            painter = painterResource(id = R.drawable.superschoolboy_remastered_round_icon_with_inner_shadow),
            contentDescription = stringResource(id = R.string.app_icon_text),
            contentScale = ContentScale.FillBounds,
        )

        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PrimaryTextButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = R.string.log_in_with_google_play_games_text),
            ) {
                activity.onBackPressed()
            }
            Spacer(modifier = Modifier.height(16.dp))
            SecondaryTextButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = R.string.continue_without_linking_text),
            ) {
                activity.onBackPressed()
            }
        }
    }
}