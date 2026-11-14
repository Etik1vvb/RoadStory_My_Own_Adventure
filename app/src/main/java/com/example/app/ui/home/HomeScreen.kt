package com.example.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.app.data.TripStatus
import com.example.app.ui.components.RoadStoryFigmaCanvas
import com.example.app.ui.components.RoadStoryPurple
import com.example.app.ui.components.figmaRect
import com.example.app.ui.theme.AstaSans

private val RoadStoryInk = Color(0xFF181818)
private val RoadStoryMuted = Color(0xFF777777)

@Composable
fun HomeScreen(
    nickname: String?,
    destination: String?,
    tripStatus: TripStatus?,
    onOngoingClick: () -> Unit,
    onContinueClick: () -> Unit,
    onNewTripClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onProfileSelectClick: () -> Unit = onProfileClick
) {
    val where = destination?.takeIf { it.isNotBlank() }
    val statusText = when (tripStatus) {
        TripStatus.PLANNED -> "${where ?: "선택한"} 여행이 출발을 기다려요."
        TripStatus.ACTIVE -> "${where ?: "선택한"} 여행이 진행 중이에요."
        TripStatus.COMPLETED -> "지난 여행을 완료했어요."
        null -> "가벼운 여행이 진행 중이에요."
    }
    RoadStoryFigmaCanvas { u ->
        Image(painterResource(R.drawable.roadstory_logo), "MoA",
            Modifier.figmaRect(38, 18, 235, 82, u), contentScale = ContentScale.Fit)
        Box(Modifier.figmaRect(1415, 28, 250, 80, u).clickable(onClick = onProfileSelectClick),
            contentAlignment = Alignment.CenterEnd) {
            Text("프로필 선택  ›", fontFamily = AstaSans,
                fontWeight = FontWeight.Bold, fontSize = (27f * u).sp, color = RoadStoryPurple)
        }
        HomePanel(36, 107, 1628, 261, u, Color.White, onClick = onOngoingClick) {
            Text("${nickname?.takeIf { it.isNotBlank() } ?: "가족"} 프로필로 어디를 떠나 볼까요?",
                modifier = Modifier.figmaRect(39, 36, 950, 75, u), fontFamily = AstaSans,
                fontWeight = FontWeight.Bold, fontSize = (42f * u).sp, color = RoadStoryInk)
            Text("모아가 당신의 여행과 함께할게요.",
                modifier = Modifier.figmaRect(39, 107, 850, 60, u), fontFamily = AstaSans,
                fontSize = (29f * u).sp, color = RoadStoryMuted)
            Image(painterResource(R.drawable.roadstory_car_art), null,
                modifier = Modifier.figmaRect(740, 0, 830, 258, u), contentScale = ContentScale.Crop)
        }
        HomePanel(36, 406, 788, 713, u, Color.White, onClick = onOngoingClick) {
            Text("나의 여행", modifier = Modifier.figmaRect(41, 35, 600, 60, u),
                fontFamily = AstaSans, fontWeight = FontWeight.Bold,
                fontSize = (40f * u).sp, color = RoadStoryInk)
            Text(statusText, modifier = Modifier.figmaRect(41, 103, 705, 60, u),
                fontFamily = AstaSans, fontSize = (29f * u).sp,
                maxLines = 2, overflow = TextOverflow.Ellipsis, color = RoadStoryMuted)
            Box(Modifier.figmaRect(41, 183, 706, 307, u)
                .clip(RoundedCornerShape((30f * u).dp))
                .background(Color(0xFFD6DAFF)).clickable(onClick = onContinueClick)) {
                Text("\"${where ?: "새로운"}\" 여행 마저 떠나기",
                    modifier = Modifier.figmaRect(40, 115, 410, 75, u),
                    fontFamily = AstaSans, fontWeight = FontWeight.Bold,
                    fontSize = (29f * u).sp, color = RoadStoryInk,
                    maxLines = 2)
                Image(painterResource(R.drawable.roadstory_route_art), null,
                    modifier = Modifier.figmaRect(385, 20, 290, 273, u),
                    contentScale = ContentScale.Fit)
            }
            Box(Modifier.figmaRect(41, 529, 706, 144, u)
                .clip(RoundedCornerShape((30f * u).dp))
                .background(Color(0xFFF2F2F2)).clickable(onClick = onNewTripClick),
                contentAlignment = Alignment.Center) {
                Text("새로운 여행 시작하기", fontFamily = AstaSans,
                    fontWeight = FontWeight.Bold, fontSize = (31f * u).sp,
                    color = RoadStoryInk)
            }
        }
        HomePanel(864, 406, 800, 337, u, Color(0xFFEAE5FF), onClick = onProfileClick) {
            Text("내 프로필", modifier = Modifier.figmaRect(39, 35, 455, 65, u),
                fontFamily = AstaSans, fontSize = (40f * u).sp,
                fontWeight = FontWeight.Bold, color = RoadStoryInk)
            Text("내 정보 확인 및 수정", modifier = Modifier.figmaRect(39, 110, 470, 60, u),
                fontFamily = AstaSans, fontSize = (29f * u).sp, color = RoadStoryMuted)
            Image(painterResource(R.drawable.roadstory_profile_art), null,
                modifier = Modifier.figmaRect(415, 93, 350, 240, u), contentScale = ContentScale.Fit)
        }
        HomePanel(864, 778, 800, 341, u, Color(0xFFDDF8F1), onClick = onHistoryClick) {
            Text("여행 기록", modifier = Modifier.figmaRect(39, 32, 455, 70, u),
                fontFamily = AstaSans, fontSize = (40f * u).sp,
                fontWeight = FontWeight.Bold, color = RoadStoryInk)
            Text("지난 여행 살펴보기", modifier = Modifier.figmaRect(39, 107, 470, 60, u),
                fontFamily = AstaSans, fontSize = (29f * u).sp, color = RoadStoryMuted)
            Image(painterResource(R.drawable.roadstory_history_art), null,
                modifier = Modifier.figmaRect(445, 70, 315, 268, u), contentScale = ContentScale.Fit)
        }
    }
}

@Composable
private fun HomePanel(
    x: Int, y: Int, w: Int, h: Int, u: Float, background: Color,
    onClick: () -> Unit, content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit
) {
    Box(Modifier.figmaRect(x, y, w, h, u)
        .shadow((19f * u).dp, RoundedCornerShape((44f * u).dp))
        .clip(RoundedCornerShape((44f * u).dp))
        .background(background)
        .clickable(onClick = onClick)) {
        content()
    }
}
