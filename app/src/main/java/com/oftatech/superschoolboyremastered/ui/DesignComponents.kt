package com.oftatech.superschoolboyremastered.ui

import android.app.Activity
import android.graphics.Bitmap
import android.util.DisplayMetrics
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.oftatech.superschoolboyremastered.ui.theme.Silver
import com.oftatech.superschoolboyremastered.ui.theme.White
import com.oftatech.superschoolboyremastered.ui.theme.robotoFontFamily
import com.oftatech.superschoolboyremastered.R
import com.oftatech.superschoolboyremastered.activity.LoginActivity
import com.oftatech.superschoolboyremastered.activity.UIState
import java.lang.reflect.TypeVariable
import java.util.*

@Composable
fun PrimaryTextButton(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit = 16.sp,
    textVerticalPadding: Dp = 20.dp,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.secondary,
            disabledBackgroundColor = Silver,
            disabledContentColor = White
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
    ) {
        Text(
            modifier = Modifier.padding(vertical = textVerticalPadding, horizontal = 17.dp),
            text = text,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            fontSize = textSize
        )
    }
}

@Composable
fun SecondaryTextButton(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit = 16.sp,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        border = BorderStroke(3.dp, MaterialTheme.colors.secondary),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
    ) {
        Text(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 17.dp),
            text = text,
            color = Silver,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            fontSize = textSize
        )
    }
}

@Composable
fun WindowHeader(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        fontFamily = robotoFontFamily,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal,
        fontSize = 48.sp,
    )
}

@Composable
fun TextRadioButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    actionOnClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                actionOnClick()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = actionOnClick,
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    actionOnClick()
                },
            text = text,
            fontFamily = robotoFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
        )
    }
}

@Composable
fun ProfileDrawerTab(
    modifier: Modifier = Modifier,
    username: String,
    avatar: Bitmap,
    rank: String,
    logouted: Boolean,
    onLogoutedChange: (Boolean) -> Unit,
    onUserDataDelete: () -> Unit,
) {
    val activity = LocalContext.current as Activity
    val logoutDialogState = remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (logouted) {
                    val signInClient =
                        GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                    activity.startActivityForResult(
                        signInClient.signInIntent,
                        LoginActivity.GPG_SIGN_IN
                    )
                } else {
                    logoutDialogState.value = true
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .padding(top = 17.dp, bottom = 17.dp, start = 17.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!logouted) {
                Image(
                    modifier = Modifier
                        .padding(end = 14.dp)
                        .size(80.dp)
                        .clip(CircleShape),
                    bitmap = avatar.asImageBitmap(),
                    contentDescription = stringResource(id = R.string.user_avatar_text),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = username,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Normal,
                    fontSize = 28.sp
                )
                Text(
                    modifier = Modifier.padding(top = 7.dp),
                    text = "${stringResource(id = R.string.rank_text)}: $rank",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Thin,
                    fontStyle = FontStyle.Normal,
                    fontSize = 18.sp
                )
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colors.secondary,
            thickness = 3.dp,
        )
    }

    if (logoutDialogState.value) {
        AlertDialog(
            onDismissRequest = { logoutDialogState.value = false },
            text = {
                Text(
                    text = stringResource(id = R.string.logout_gpg_text),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    fontSize = 19.sp,
                )
            },
            buttons = {
                Box(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                ) {
                    TextButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = {
                            logoutDialogState.value = false
                        }
                    ) {
                        Text(
                            modifier = Modifier.alpha(0.6F),
                            text = stringResource(id = R.string.cancel_text),
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            fontSize = 16.sp
                        )
                    }

                    TextButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                            onUserDataDelete()
                            val signInClient =
                                GoogleSignIn.getClient(
                                    activity,
                                    GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
                                )
                            signInClient
                                .signOut()
                                .addOnCompleteListener {
                                    onLogoutedChange(true)
                                }
                            logoutDialogState.value = false
                        }) {
                        Text(
                            text = stringResource(id = R.string.exit_text),
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.secondary
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun DrawerTab(
    modifier: Modifier = Modifier,
    sectionName: String,
    icon: ImageVector,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(22.dp),
                imageVector = icon,
                contentDescription = "$sectionName ${stringResource(id = R.string.drawer_tab_icon_text)}",
            )
            Text(
                text = sectionName,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                fontSize = 14.sp,
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colors.secondary,
            thickness = 1.dp,
        )
    }
}

@Composable
fun FullWidthAdaptiveBannerAds(
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as Activity
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = {
            val adView = AdView(it)
            //TODO Заменить adUnitId
            adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

            val display = activity.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density
            val adWidthPixels = outMetrics.widthPixels.toFloat()
            val adWidth = (adWidthPixels / density).toInt()

            adView.adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(it, adWidth)
            adView.loadAd(
                AdRequest.Builder()
                    .build()
            )

            adView
        })
}