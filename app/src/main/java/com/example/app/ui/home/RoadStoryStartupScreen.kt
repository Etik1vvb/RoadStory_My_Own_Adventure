package com.example.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.app.ui.components.RoadStoryFigmaCanvas
import com.example.app.ui.components.RoadStoryPurple
import com.example.app.ui.components.figmaRect
import com.example.app.ui.theme.AstaSans
import kotlinx.coroutines.delay

/** 앱 시작 화면.png: true Compose layout with the MoA logo as a transparent design asset. */
@Composable
fun RoadStoryStartupScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000L)
        onFinished()
    }
    RoadStoryFigmaCanvas { u ->
        Image(
            painter = painterResource(R.drawable.roadstory_logo),
            contentDescription = "Road story",
            modifier = Modifier.figmaRect(550, 440, 600, 190, u),
            contentScale = ContentScale.Fit
        )
        Box(Modifier.figmaRect(545, 668, 610, 85, u),
            contentAlignment = Alignment.Center) {
            Text(
                "당신을 위한 AI 여행 도슨트",
                fontFamily = AstaSans, fontWeight = FontWeight.Bold,
                fontSize = (48f*u).sp,
                textAlign = TextAlign.Center, color = RoadStoryPurple,
                maxLines = 1
            )
        }
    }
}
