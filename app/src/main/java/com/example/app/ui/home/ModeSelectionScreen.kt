package com.example.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.app.ui.components.RoadStoryFigmaActionButton
import com.example.app.ui.components.RoadStoryFigmaCanvas
import com.example.app.ui.components.RoadStoryFigmaCard
import com.example.app.ui.components.RoadStoryFigmaHeading
import com.example.app.ui.components.RoadStoryPurple
import com.example.app.ui.components.figmaRect

/** Figma 1700x1184 reference layout. Existing navigation callbacks are unchanged. */
@Composable
fun ModeSelectionScreen(
    nickname: String?,
    onTravelClick: () -> Unit,
    onDailyClick: () -> Unit,
    onChangeProfile: () -> Unit
) {
    var chosenMode by rememberSaveable { mutableStateOf("travel") }
    RoadStoryFigmaCanvas { unit ->
        RoadStoryFigmaHeading(
            title = "오늘은 어떠한 여행을 떠나볼까요?",
            subtitle = "계획하고 있는 여행을 설명해 주세요!",
            unit = unit
        )
        Box(
            Modifier.figmaRect(1450, 40, 235, 65, unit)
                .clickable(role = Role.Button, onClick = onChangeProfile),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                "다른 프로필 선택 ›",
                fontSize = (25f * unit).sp,
                fontWeight = FontWeight.SemiBold,
                color = RoadStoryPurple,
                maxLines = 1
            )
        }
        Box(Modifier.figmaRect(373, 265, 434, 668, unit)) {
            RoadStoryFigmaCard(
                title = "멀리 떠나는 여행",
                subtitle = "여행 체크인, 목적지 설정, 이동 중 방문할 장소 추천",
                selected = chosenMode == "travel",
                unit = unit,
                onClick = { chosenMode = "travel" }
            )
        }
        Box(Modifier.figmaRect(891, 265, 434, 668, unit)) {
            RoadStoryFigmaCard(
                title = "가까운 곳으로의 산책",
                subtitle = "가까운 카페, 식당, 산책 장소 추천",
                selected = chosenMode == "daily",
                unit = unit,
                onClick = { chosenMode = "daily" }
            )
        }
        RoadStoryFigmaActionButton(
            title = if (chosenMode == "travel") "여행 모드 시작" else "일상 모드 시작",
            unit = unit,
            onClick = { if (chosenMode == "travel") onTravelClick() else onDailyClick() }
        )
    }
}
