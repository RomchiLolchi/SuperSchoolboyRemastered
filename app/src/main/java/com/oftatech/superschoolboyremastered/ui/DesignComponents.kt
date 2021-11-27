package com.oftatech.superschoolboyremastered.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oftatech.superschoolboyremastered.ui.theme.Silver
import com.oftatech.superschoolboyremastered.ui.theme.White
import com.oftatech.superschoolboyremastered.ui.theme.robotoFontFamily

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

@Composable
fun ProfileDrawerTab() {
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
                painter = painterResource(id = com.oftatech.superschoolboyremastered.R.drawable.superschoolboy_remastered_round_icon_with_inner_shadow),
                contentDescription = stringResource(id = com.oftatech.superschoolboyremastered.R.string.user_avatar_text),
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
            thickness = 3.dp,
        )
    }
}

@Composable
fun DrawerTab(sectionName: String, icon: ImageVector) {
    Column(
        modifier = Modifier
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
            Image(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(22.dp),
                imageVector = icon,
                contentDescription = "$sectionName ${stringResource(id = com.oftatech.superschoolboyremastered.R.string.drawer_tab_icon_text)}",
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