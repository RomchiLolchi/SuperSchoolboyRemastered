package com.easyeducation.superschoolboyremastered.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
fun SuperSchoolboyRemasteredTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}