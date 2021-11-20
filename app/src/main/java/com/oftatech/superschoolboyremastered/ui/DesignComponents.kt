package com.oftatech.superschoolboyremastered.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oftatech.superschoolboyremastered.ui.theme.Silver
import com.oftatech.superschoolboyremastered.ui.theme.White

@Composable
fun PrimaryTextButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary, disabledBackgroundColor = Silver, disabledContentColor = White),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(modifier = Modifier.padding(vertical = 20.dp, horizontal = 17.dp), text = text)
    }
}

@Composable
fun SecondaryTextButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        border = BorderStroke(3.dp, MaterialTheme.colors.secondary),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp)
    ) {
        Text(modifier = Modifier.padding(vertical = 20.dp, horizontal = 17.dp), text = text, color = Silver)
    }
}