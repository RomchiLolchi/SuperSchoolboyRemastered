package com.oftatech.superschoolboyremastered.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.images.ImageManager
import com.google.android.gms.games.Games
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.oftatech.superschoolboyremastered.R
import com.oftatech.superschoolboyremastered.ui.*
import com.oftatech.superschoolboyremastered.ui.theme.*
import com.oftatech.superschoolboyremastered.util.TextHtmlTagHandler
import com.oftatech.superschoolboyremastered.util.Utils
import com.oftatech.superschoolboyremastered.util.Utils.appSetup
import com.oftatech.superschoolboyremastered.util.Utils.containsValueTimes
import com.oftatech.superschoolboyremastered.util.Utils.toOldColor
import com.oftatech.superschoolboyremastered.viewmodel.GPGProfileViewModel
import com.oftatech.superschoolboyremastered.viewmodel.MainViewModel
import com.oftatech.superschoolboyremastered.viewmodel.SessionsSettingsViewModel
import com.oftatech.superschoolboyremastered.viewmodel.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable
import java.text.DecimalFormat
import kotlin.math.roundToInt
import com.oftatech.superschoolboyremastered.viewmodel.SessionsSettingsViewModel.Companion.getInStandardIntForm

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var logouted: MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val viewModel by viewModels<MainViewModel>()
        val statsViewModel by viewModels<StatisticsViewModel>()
        val gpgProfileViewModel by viewModels<GPGProfileViewModel>()
        val sessionSettingsViewModel by viewModels<SessionsSettingsViewModel>()
        setContent {
            logouted = remember {
                mutableStateOf(
                    GoogleSignIn.getLastSignedInAccount(this) == null || !GoogleSignIn.hasPermissions(
                        GoogleSignIn.getLastSignedInAccount(this)
                    )
                )
            }

            MainAppContent(
                darkTheme = Utils.isDarkTheme(viewModel.appTheme.observeAsState().value!!),
                accentColor = animateColorAsState(targetValue = viewModel.accentColor.observeAsState().value!!).value
            ) {
                appSetup()

                MainActivityScreenContent(
                    uiThemeState = viewModel.appTheme.observeAsState().value!!,
                    onUIThemeStateChange = { viewModel.appTheme.value = it },
                    accentColor = viewModel.accentColor.observeAsState().value!!,
                    onAccentColorChange = { viewModel.accentColor.value = it },
                    statsViewModel = statsViewModel,
                    gpgProfileViewModel = gpgProfileViewModel,
                    logouted = logouted.value,
                    onLogoutedChange = { logouted.value = it },
                    ranked = sessionSettingsViewModel.ranked.observeAsState().value!!,
                    onRankedChange = { sessionSettingsViewModel.ranked.value = it },
                    timer = sessionSettingsViewModel.timer.observeAsState().value!!,
                    onTimerChange = { sessionSettingsViewModel.timer.value = it },
                    numbers = sessionSettingsViewModel.numbers.observeAsState().value!!,
                    difficulty = sessionSettingsViewModel.difficulty.observeAsState().value!!,
                    onDifficultyChange = { sessionSettingsViewModel.difficulty.value = it },
                    onWriteNewNumbers = { sessionSettingsViewModel.writeNewNumbers(it) }
                )
            }
        }

        if (viewModel.firstOpen.value!!) {
            viewModel.firstOpen.value = false
            startActivity(Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoginActivity.GPG_SIGN_IN) {
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
                logouted.value = false
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainActivityScreenContent(
    modifier: Modifier = Modifier,
    uiThemeState: UIState,
    onUIThemeStateChange: (UIState) -> Unit,
    accentColor: Color,
    onAccentColorChange: (Color) -> Unit,
    statsViewModel: StatisticsViewModel,
    gpgProfileViewModel: GPGProfileViewModel,
    logouted: Boolean,
    onLogoutedChange: (Boolean) -> Unit,
    ranked: Boolean,
    onRankedChange: (Boolean) -> Unit,
    timer: Float,
    onTimerChange: (Float) -> Unit,
    numbers: SnapshotStateMap<Int, Boolean>,
    difficulty: Float,
    onDifficultyChange: (Float) -> Unit,
    onWriteNewNumbers: (SnapshotStateMap<Int, Boolean>) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val activity = LocalContext.current as Activity

    val screensList = listOf(
        Screen.Training,
        Screen.Settings,
        Screen.Statistics,
        Screen.AdultInfo,
        Screen.KidsInfo,
        Screen.AboutApp
    )

    ModalBottomSheetLayout(
        sheetContent = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxSize()
            ) {
                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = stringResource(id = R.string.session_settings),
                    fontFamily = robotoFontFamily,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                )
                Spacer(modifier = Modifier.height(20.dp))
                /*Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterStart),
                        text = stringResource(id = R.string.ranked_text),
                        fontFamily = robotoFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    )
                    Switch(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        checked = ranked,
                        onCheckedChange = { onRankedChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colors.secondary,
                            checkedTrackColor = MaterialTheme.colors.secondary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(id = R.string.difficulty_text),
                        fontFamily = robotoFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Slider(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(300.dp),
                            value = difficulty,
                            onValueChange = { onDifficultyChange(it) },
                            valueRange = 0.01f..0.03f,
                            steps = 1,
                            enabled = ranked,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colors.secondary,
                                activeTrackColor = MaterialTheme.colors.secondary,
                                activeTickColor = MaterialTheme.colors.secondary,
                            ),
                        )
                        Text(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            text = "${difficulty.getInStandardIntForm()}",
                            fontFamily = robotoFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))*/
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(id = R.string.timer_text),
                        fontFamily = robotoFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Slider(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(300.dp),
                            value = timer,
                            onValueChange = { onTimerChange(it) },
                            valueRange = 0.05f..0.6f,
                            enabled = !ranked,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colors.secondary,
                                activeTrackColor = MaterialTheme.colors.secondary,
                                activeTickColor = MaterialTheme.colors.secondary,
                            ),
                        )
                        Text(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            text = "${timer.getInStandardIntForm()}${stringResource(id = R.string.sec_text)}",
                            fontFamily = robotoFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(id = R.string.numbers_text),
                        fontFamily = robotoFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        for (i in numbers) {
                            numberColumn(
                                number = i.key,
                                checked = i.value,
                                onCheckedChange = {
                                    if (it || (!it && numbers.containsValueTimes(true) > 1)) {
                                        numbers[i.key] = it
                                        onWriteNewNumbers(numbers)
                                    } else {
                                        Toast.makeText(activity, R.string.need_more_than_one_number_text, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                isActive = !ranked,
                            )
                        }
                    }
                }
            }
        },
        sheetState = bottomState,
    ) {
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
                    Icon(
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
                MainActivityDrawerContent(
                    screens = screensList,
                    navController = navController,
                    drawerState = scaffoldState.drawerState,
                    username = gpgProfileViewModel.username.observeAsState().value!!,
                    avatar = gpgProfileViewModel.avatar.observeAsState().value ?: run {
                        val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
                        val canvas = android.graphics.Canvas(bmp)
                        canvas.drawColor(android.graphics.Color.TRANSPARENT)
                        bmp
                    },
                    rank = gpgProfileViewModel.rank.observeAsState().value!!,
                    onUserDataDelete = {
                        gpgProfileViewModel.deleteUserData()
                    },
                    logouted = logouted,
                    onLogoutedChange = { onLogoutedChange(it) },
                )
            },
            drawerBackgroundColor = MaterialTheme.colors.background,
            drawerShape = RectangleShape,
            drawerElevation = 10.dp,
            scaffoldState = scaffoldState,
        ) { paddingValues ->
            NavHost(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                navController = navController,
                startDestination = Screen.Training.route
            ) {
                composable(Screen.Training.route) {
                    TrainingScreen(
                        modifier = Modifier.padding(
                            paddingValues
                        ),
                        onBottomSheetOpen = {
                            coroutineScope.launch {
                                bottomState.show()
                            }
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(horizontal = 13.dp),
                        uiThemeState = uiThemeState,
                        onUIThemeStateChange = onUIThemeStateChange,
                        accentColor = accentColor,
                        onAccentColorChange = onAccentColorChange
                    )
                }
                composable(Screen.Statistics.route) {
                    StatisticsScreen(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(horizontal = 13.dp), statsViewModel = statsViewModel
                    )
                }
                composable(Screen.AdultInfo.route) {
                    TextScreen(
                        header = stringResource(id = R.string.info_for_adults),
                        text = Html.fromHtml(
                            stringResource(id = R.string.adult_info_content),
                            Html.FROM_HTML_MODE_COMPACT and Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST and Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
                        )
                    )
                }
                composable(Screen.KidsInfo.route) {
                    TextScreen(
                        header = stringResource(id = R.string.info_for_kids),
                        text = Html.fromHtml(
                            stringResource(id = R.string.kids_info_content),
                            Html.FROM_HTML_MODE_COMPACT and Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST and Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
                        )
                    )
                }
                composable(Screen.AboutApp.route) {
                    TextScreen(
                        header = stringResource(id = R.string.about_app),
                        text = Html.fromHtml(
                            stringResource(id = R.string.about_app_content),
                            Html.FROM_HTML_MODE_COMPACT and Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST and Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM,
                            null,
                            TextHtmlTagHandler(MaterialTheme.colors.secondary)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun numberColumn(number: Int, checked: Boolean, onCheckedChange: (Boolean) -> Unit, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = number.toString(),
            fontFamily = robotoFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
        )
        Spacer(modifier = Modifier.height(3.dp))
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange(it) },
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colors.secondary),
            enabled = isActive,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TrainingScreen(
    modifier: Modifier = Modifier,
    onBottomSheetOpen: () -> Unit,
) {
    val activity = LocalContext.current as Activity

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = modifier
                .align(Alignment.Center),
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
            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onBottomSheetOpen()
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(id = R.string.settings_text),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    modifier = Modifier.alpha(0.6F),
                    text = stringResource(id = R.string.edit_training_session_settings_text),
                    fontSize = 18.sp
                )
            }
        }

        if (Firebase.remoteConfig.getBoolean(Utils.REMOTE_CONFIG_ADS_KEY)) {
            FullWidthAdaptiveBannerAds(
                modifier = modifier
                    .align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    uiThemeState: UIState,
    onUIThemeStateChange: (UIState) -> Unit,
    accentColor: Color,
    onAccentColorChange: (Color) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        WindowHeader(text = stringResource(id = R.string.settings_text))
        Spacer(modifier = Modifier.height(30.dp))
        LazyColumn {
            item {
                SettingsBigPart(partName = stringResource(id = R.string.app_theming_text)) {
                    SettingsPart(partName = stringResource(id = R.string.ui_theme_text)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextRadioButton(
                                text = stringResource(id = R.string.light_text),
                                isSelected = uiThemeState == UIState.LightTheme
                            ) {
                                onUIThemeStateChange(UIState.LightTheme)
                            }
                            TextRadioButton(
                                text = stringResource(id = R.string.dark_text),
                                isSelected = uiThemeState == UIState.DarkTheme
                            ) {
                                onUIThemeStateChange(UIState.DarkTheme)
                            }
                            TextRadioButton(
                                text = stringResource(id = R.string.system_text),
                                isSelected = uiThemeState == UIState.SystemSettings
                            ) {
                                onUIThemeStateChange(UIState.SystemSettings)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    SettingsPart(partName = stringResource(id = R.string.accent_color_text)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextRadioButton(
                                text = stringResource(id = R.string.green_text),
                                isSelected = accentColor == Madang
                            ) {
                                onAccentColorChange(Madang)
                            }
                            TextRadioButton(
                                text = stringResource(id = R.string.orange_text),
                                isSelected = accentColor == Tacao
                            ) {
                                onAccentColorChange(Tacao)
                            }
                            TextRadioButton(
                                text = stringResource(id = R.string.pink_text),
                                isSelected = accentColor == WePeep
                            ) {
                                onAccentColorChange(WePeep)
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class UIState(private val stringRepresentation: String) : Serializable {
    companion object {
        fun valueOf(stringRepresentation: String): UIState {
            return when (stringRepresentation) {
                "light" -> LightTheme
                "dark" -> DarkTheme
                "system" -> SystemSettings
                else -> throw IllegalArgumentException("In UIState class there is no such string representation: $stringRepresentation!")
            }
        }
    }

    object LightTheme : UIState("light")
    object DarkTheme : UIState("dark")
    object SystemSettings : UIState("system")

    override fun toString(): String {
        return stringRepresentation
    }
}

@Composable
private fun SettingsBigPart(
    modifier: Modifier = Modifier,
    partName: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = partName,
            fontFamily = robotoFontFamily,
            fontSize = 36.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            color = MaterialTheme.colors.secondary,
        )
        Spacer(modifier = Modifier.height(20.dp))
        content()
    }
}

@Composable
private fun SettingsPart(
    modifier: Modifier = Modifier,
    partName: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = partName,
            fontFamily = robotoFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
        )
        Spacer(modifier = Modifier.height(20.dp))
        content()
    }
}

@Composable
private fun StatisticsScreen(
    modifier: Modifier = Modifier,
    statsViewModel: StatisticsViewModel,
) {
    statsViewModel.updateStatsData()

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        WindowHeader(
            text = stringResource(id = R.string.statistics_text)
        )
        when (statsViewModel.isEmpty()) {
            true -> PlugScreen()
            false -> {
                LazyColumn {
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                        SettingsBigPart(partName = stringResource(id = R.string.absolute_text)) {
                            StatisticsField(
                                text = stringResource(id = R.string.maximum_number_of_unmistakable_answers_in_a_row_text),
                                value = statsViewModel.absoluteRightAnswersInRow.value.toString()
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            val mart =
                                if (statsViewModel.absoluteAverageResponseTime.value!!.isNaN()) "-" else "${
                                    DecimalFormat("#.##").format(statsViewModel.absoluteAverageResponseTime.value)
                                }${stringResource(id = R.string.sec_text)}"
                            StatisticsField(
                                text = stringResource(id = R.string.minimal_average_response_time_text),
                                value = mart
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        SettingsBigPart(partName = stringResource(id = R.string.last_session_text)) {
                            StatisticsField(
                                text = stringResource(id = R.string.timer_text),
                                value = "${statsViewModel.lsTimer.value.toString()}${
                                    stringResource(id = R.string.sec_text)
                                }"
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            StatisticsField(
                                text = stringResource(id = R.string.right_answers_text),
                                value = statsViewModel.lsRightAnswers.value.toString()
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            StatisticsField(
                                text = stringResource(id = R.string.wrong_answers_text),
                                value = statsViewModel.lsWrongAnswers.value.toString()
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            StatisticsField(
                                text = stringResource(id = R.string.maximum_number_of_unmistakable_answers_in_a_row_text),
                                value = statsViewModel.lsRightAnswersInRow.value.toString()
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            val art =
                                if (statsViewModel.lsAverageResponseTime.value!!.isNaN()) "-" else "${
                                    DecimalFormat("#.##").format(statsViewModel.lsAverageResponseTime.value)
                                }${stringResource(id = R.string.sec_text)}"
                            StatisticsField(
                                text = stringResource(id = R.string.average_response_time_text),
                                value = art
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticsField(modifier: Modifier = Modifier, text: String, value: String) {
    Box(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = text,
            fontSize = 19.sp,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
        )
        Text(
            modifier = Modifier.align(Alignment.CenterEnd),
            text = value,
            fontSize = 24.sp,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal,
        )
    }
}

@Composable
private fun PlugScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.alpha(0.6F),
            text = stringResource(id = R.string.empty_so_far_text),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            fontSize = 19.sp
        )
    }
}

@Composable
private fun TextScreen(
    modifier: Modifier = Modifier,
    header: String,
    text: Spanned,
) {
    val scrollState = rememberScrollState()
    val onBackground = MaterialTheme.colors.onBackground.toOldColor()
    val textLinksColor = MaterialTheme.colors.secondary.toOldColor()

    val states = arrayOf(
        intArrayOf(android.R.attr.state_enabled),
        intArrayOf(-android.R.attr.state_enabled),
        intArrayOf(-android.R.attr.state_checked),
        intArrayOf(android.R.attr.state_pressed)
    )
    val colors = intArrayOf(
        textLinksColor,
        textLinksColor,
        textLinksColor,
        textLinksColor
    )


    Column(
        modifier = modifier
            .padding(horizontal = 13.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        WindowHeader(text = header)
        Spacer(modifier = Modifier.height(30.dp))
        AndroidView(modifier = Modifier.verticalScroll(scrollState),
            factory = {
                TextView(it).apply {
                    setText(text)
                    setTextColor(onBackground)
                    typeface = ResourcesCompat.getFont(it, R.font.roboto)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 19F)
                    movementMethod = LinkMovementMethod.getInstance()
                    setLinkTextColor(ColorStateList(states, colors))
                }
            })
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
    object AdultInfo :
        Screen("adult_info", Icons.Outlined.EscalatorWarning, R.string.info_for_adults)

    object KidsInfo : Screen("kids_info", Icons.Outlined.ChildCare, R.string.info_for_kids)
    object AboutApp : Screen("app_info", Icons.Outlined.Info, R.string.about_app)
}

private fun openTrainingActivity(context: Context) {
    FirebaseAnalytics.getInstance(context).logEvent("training_session_start", null)
    context.startActivity(Intent(context, TrainingActivity::class.java))
}

@Composable
private fun MainActivityDrawerContent(
    modifier: Modifier = Modifier,
    screens: List<Screen>,
    navController: NavController,
    drawerState: DrawerState,
    username: String,
    avatar: Bitmap,
    rank: String,
    onUserDataDelete: () -> Unit,
    logouted: Boolean,
    onLogoutedChange: (Boolean) -> Unit,
) {
    val activity = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        item {
            ProfileDrawerTab(
                username = username,
                avatar = avatar,
                rank = rank,
                logouted = logouted,
                onLogoutedChange = { onLogoutedChange(it) },
                onUserDataDelete = { onUserDataDelete() })
        }
        itemsIndexed(screens) { index, screen ->
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