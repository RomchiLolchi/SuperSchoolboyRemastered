package com.oftatech.superschoolboyremastered.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oftatech.superschoolboyremastered.R
import com.oftatech.superschoolboyremastered.ui.PrimaryTextButton
import com.oftatech.superschoolboyremastered.ui.theme.MainAppContent
import com.oftatech.superschoolboyremastered.ui.theme.robotoFontFamily
import com.oftatech.superschoolboyremastered.util.Utils.appSetup
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainAppContent {
                appSetup()
                MainActivityScreenContent()
            }
        }

        //TODO Убрать заглушку и переписать по нормальному
        if (intent.getBooleanExtra("TEST", true)) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            })
        }
    }
}

@Composable
private fun MainActivityScreenContent() {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }, painter = painterResource(id = R.drawable.ic_baseline_menu_24), contentDescription = "menu")
            }
        },
        drawerContent = {
            MainActivityDrawerContent()
        },
        drawerBackgroundColor = MaterialTheme.colors.background,
        drawerShape = RectangleShape,
        drawerElevation = 10.dp,
        scaffoldState = scaffoldState,
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //TODO Внести все фразы в strings.xml
            //TODO Настроить drawer (открытие, его дизайн)
            Text(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(bottom = 105.dp),
                text = "You can start\ntraining right now",
                style = MaterialTheme.typography.h2.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center
            )
            PrimaryTextButton(modifier = Modifier
                .width(254.dp)
                .padding(bottom = 107.dp), text = "Start Training".toUpperCase()) {

            }
            Row {
                Image(imageVector = Icons.Outlined.Settings, contentDescription = "Settings")
                Spacer(modifier = Modifier.width(10.dp))
                Text(modifier = Modifier.alpha(0.6F), text = "Edit training session settings", fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun MainActivityDrawerContent() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        ProfileDrawerTab()
    }
}

@Composable
private fun ProfileDrawerTab() {
    //TODO Добавить параметры вместо placeholder'ов
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .padding(top = 17.dp, bottom = 17.dp, start = 17.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .padding(end = 14.dp)
                    .size(80.dp)
                    .clip(CircleShape),
                painter = painterResource(id = R.drawable.superschoolboy_remastered_round_icon_with_inner_shadow),
                contentDescription = "User's avatar",
                contentScale = ContentScale.Crop,
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(text = "romchi_lolchi", fontFamily = robotoFontFamily, fontWeight = FontWeight.Light, fontStyle = FontStyle.Normal, fontSize = 28.sp)
                Text(modifier = Modifier.padding(top = 7.dp), text = "rank: grandmaster", fontFamily = robotoFontFamily, fontWeight = FontWeight.Thin, fontStyle = FontStyle.Normal, fontSize = 18.sp)
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colors.secondary,
            thickness = 3.dp
        )
    }
}

@Composable
private fun DrawerTab() {
    //TODO Добавить параметры
}