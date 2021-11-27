package com.oftatech.superschoolboyremastered.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oftatech.superschoolboyremastered.R
import com.oftatech.superschoolboyremastered.ui.DrawerTab
import com.oftatech.superschoolboyremastered.ui.PrimaryTextButton
import com.oftatech.superschoolboyremastered.ui.ProfileDrawerTab
import com.oftatech.superschoolboyremastered.ui.theme.MainAppContent
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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }, imageVector = Icons.Outlined.Menu, contentDescription = stringResource(id = R.string.menu_text))
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
            Text(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(bottom = 105.dp),
                text = stringResource(id = R.string.you_can_start_training_now_text),
                style = MaterialTheme.typography.h2.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center
            )
            PrimaryTextButton(modifier = Modifier
                .width(254.dp)
                .padding(bottom = 107.dp), text = stringResource(id = R.string.start_training_text).uppercase()
            ) {

            }
            Row {
                Image(imageVector = Icons.Outlined.Settings, contentDescription = stringResource(id = R.string.settings_text))
                Spacer(modifier = Modifier.width(10.dp))
                Text(modifier = Modifier.alpha(0.6F), text = stringResource(id = R.string.edit_training_session_settings_text), fontSize = 18.sp)
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
        DrawerTab(sectionName = stringResource(id = R.string.training_text), icon = Icons.Outlined.FitnessCenter)
        DrawerTab(sectionName = stringResource(id = R.string.settings_text), icon = Icons.Outlined.Settings)
        DrawerTab(sectionName = stringResource(id = R.string.statistics_text), icon = Icons.Outlined.Analytics)
        DrawerTab(sectionName = stringResource(id = R.string.info_for_adults), icon = Icons.Outlined.EscalatorWarning)
        DrawerTab(sectionName = stringResource(id = R.string.info_for_kids), icon = Icons.Outlined.ChildCare)
        DrawerTab(sectionName = stringResource(id = R.string.about_app), icon = Icons.Outlined.Info)
    }
}