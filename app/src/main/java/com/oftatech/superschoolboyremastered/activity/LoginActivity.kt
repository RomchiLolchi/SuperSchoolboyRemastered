package com.oftatech.superschoolboyremastered.activity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
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
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.images.ImageManager
import com.google.android.gms.games.Games
import com.oftatech.superschoolboyremastered.R
import com.oftatech.superschoolboyremastered.ui.PrimaryTextButton
import com.oftatech.superschoolboyremastered.ui.SecondaryTextButton
import com.oftatech.superschoolboyremastered.ui.theme.MainAppContent
import com.oftatech.superschoolboyremastered.util.Utils
import com.oftatech.superschoolboyremastered.util.Utils.appSetup
import com.oftatech.superschoolboyremastered.viewmodel.GPGProfileViewModel
import com.oftatech.superschoolboyremastered.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    companion object {
        const val GPG_SIGN_IN = 100
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GPG_SIGN_IN) {
            val result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            if (result?.isSuccess != true) {
                var message = result!!.status.statusMessage
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.gpg_error_text)
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                val gpgProfileViewModel by viewModels<GPGProfileViewModel>()
                val player = Games.getPlayersClient(this, result.signInAccount!!)
                player.currentPlayer.addOnSuccessListener {
                    it.hiResImageUri?.let { it1 ->
                        ImageManager.create(this).loadImage({ uri, drawable, boolean ->
                            gpgProfileViewModel.avatar.value = (drawable as BitmapDrawable).bitmap
                        }, it1)
                    } ?: run { gpgProfileViewModel.avatar.value = null }
                    gpgProfileViewModel.username.value = it.displayName
                }
            }
            onBackPressed()
        }
    }
}

@Preview
@Composable
private fun LoginActivityScreenContent() {
    val activity = LocalContext.current as Activity

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp),
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

        Image(
            modifier = Modifier
                .size(246.dp)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.superschoolboy_remastered_round_icon_with_inner_shadow),
            contentDescription = stringResource(id = R.string.app_icon_text),
            contentScale = ContentScale.FillBounds,
        )

        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PrimaryTextButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = R.string.log_in_with_google_play_games_text),
            ) {
                if (GoogleSignIn.getLastSignedInAccount(activity) == null) {
                    gamesSignIn(activity)
                } else {
                    activity.onBackPressed()
                }
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

private fun gamesSignIn(activity: Activity) {
    val signInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
    activity.startActivityForResult(signInClient.signInIntent, LoginActivity.GPG_SIGN_IN)
}