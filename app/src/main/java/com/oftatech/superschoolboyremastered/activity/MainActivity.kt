package com.oftatech.superschoolboyremastered.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                //TODO Реализовать проверку на установку последней версии!!! (Firebase)
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
private fun MainActivityScreenContent(
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()

    val screensList = listOf(
        Screen.Training,
        Screen.Settings,
        Screen.Statistics,
        Screen.AdultInfo,
        Screen.KidsInfo,
        Screen.AboutApp
    )

    Scaffold(
        modifier = modifier,
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
                            coroutineScope.launch {
                                scaffoldState.drawerState.open()
                            }
                        },
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = stringResource(id = R.string.menu_text),
                )
            }
        },
        drawerContent = {
            MainActivityDrawerContent(screens = screensList, navController = navController, drawerState = scaffoldState.drawerState)
        },
        drawerBackgroundColor = MaterialTheme.colors.background,
        drawerShape = RectangleShape,
        drawerElevation = 10.dp,
        scaffoldState = scaffoldState,
    ) {
        NavHost(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            navController = navController,
            startDestination = Screen.Training.route
        ) {
            composable(Screen.Training.route) { TrainingScreen() }
            composable(Screen.Settings.route) { TODO() }
            composable(Screen.Statistics.route) { TODO() }
            composable(Screen.AdultInfo.route) { TODO() }
            composable(Screen.KidsInfo.route) { TODO() }
            composable(Screen.AboutApp.route) { TODO() }
        }
    }
}

@Composable
private fun TrainingScreen(
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as Activity

    Column(
        modifier = modifier
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
        PrimaryTextButton(
            modifier = Modifier
                .width(254.dp)
                .padding(bottom = 107.dp),
            text = stringResource(id = R.string.start_training_text).uppercase()
        ) {
            openTrainingActivity(activity)
        }
        Row {
            Image(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(id = R.string.settings_text)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                modifier = Modifier.alpha(0.6F),
                text = stringResource(id = R.string.edit_training_session_settings_text),
                fontSize = 18.sp
            )
        }
    }
}

sealed class Screen(
    val route: String,
    val drawerImage: ImageVector,
    @StringRes val drawerTextId: Int
) {
    object Training : Screen("training", Icons.Outlined.FitnessCenter, R.string.training_text)
    object Settings : Screen("settings", Icons.Outlined.Settings, R.string.settings_text)
    object Statistics : Screen("stats", Icons.Outlined.Analytics, R.string.statistics_text)
    object AdultInfo : Screen("adult_info", Icons.Outlined.EscalatorWarning, R.string.info_for_adults)
    object KidsInfo : Screen("kids_info", Icons.Outlined.ChildCare, R.string.info_for_kids)
    object AboutApp : Screen("app_info", Icons.Outlined.Info, R.string.about_app)
}

private fun openTrainingActivity(context: Context) {
    context.startActivity(Intent(context, TrainingActivity::class.java))
}

@Composable
private fun MainActivityDrawerContent(
    modifier: Modifier = Modifier,
    screens: List<Screen>,
    navController: NavController,
    drawerState: DrawerState,
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        ProfileDrawerTab()
        for (screen in screens) {
            DrawerTab(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                sectionName = stringResource(id = screen.drawerTextId),
                icon = screen.drawerImage
            )
        }
    }
}