package com.oftatech.superschoolboyremastered.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = White,
    secondary = Madang,
    background = MineShaft,
    onBackground = White,
    onSecondary = Black,
)

private val LightColorPalette = lightColors(
    primary = Black,
    secondary = Madang,
    background = White,
    onBackground = Black,
    onSecondary = Black,
)

@Composable
fun MainAppContent(
    content: @Composable () -> Unit,
) {
    SuperSchoolboyRemasteredTheme {
        Surface(
            color = MaterialTheme.colors.background,
        ) {
            content()
        }
    }
}

@Composable
fun SuperSchoolboyRemasteredTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    //TODO Поменять background цвета
    //TODO Придумать что сделать с автоматическим затемнением цветов при включённой тёмной теме
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}