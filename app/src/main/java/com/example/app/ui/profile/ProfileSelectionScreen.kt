package com.example.app.ui.profile

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app.data.UserProfile
import com.example.app.ui.components.RoadStoryFigmaActionButton
import com.example.app.ui.components.RoadStoryFigmaCanvas
import com.example.app.ui.components.RoadStoryFigmaCard
import com.example.app.ui.components.RoadStoryFigmaHeading
import com.example.app.ui.components.figmaRect

/** Figma 1700x1184 reference layout. Profile events stay wired to MainActivity. */
@Composable
fun ProfileSelectionScreen(
    profiles: List<UserProfile>,
    selectedProfileId: String?,
    onSelectProfile: (String) -> Unit,
    onAddProfile: () -> Unit,
    onDeleteProfile: (String) -> Unit
) {
    var deleteTarget by remember { mutableStateOf<UserProfile?>(null) }
    var warning by remember { mutableStateOf<String?>(null) }
    var pendingProfileId by remember(selectedProfileId) { mutableStateOf(selectedProfileId) }
    val effectiveId = pendingProfileId?.takeIf { id -> profiles.any { it.id == id } }
        ?: profiles.firstOrNull()?.id

    RoadStoryFigmaCanvas { unit ->
        RoadStoryFigmaHeading(
            title = "사용하실 프로필을 선택해 주세요",
            subtitle = "프로필마다 여행의 취향을 별도로 저장할 수 있어요!",
            unit = unit
        )
        // Three design-width cards, with horizontal scrolling when profiles exceed two.
        Box(Modifier.figmaRect(120, 244, 1460, 710, unit)) {
            Row(
                modifier = Modifier
                    .width((1460f * unit).dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = (23f * unit).dp, vertical = (21f * unit).dp),
                horizontalArrangement = Arrangement.spacedBy((54f * unit).dp)
            ) {
                profiles.forEach { profile ->
                    RoadStoryFigmaCard(
                        title = profile.nickname.ifBlank { "이름 없는 프로필" },
                        subtitle = when (profile.nickname.trim()) {
                            "가족" -> "우리 가족 여행 기록"
                            "연인" -> "♡과의 여행"
                            else -> "${profile.nickname.ifBlank { "나" }}의 여행 기록"
                        },
                        selected = effectiveId == profile.id,
                        unit = unit,
                        onClick = { warning = null; pendingProfileId = profile.id },
                        onManageClick = {
                            if (profile.id == selectedProfileId) {
                                warning = "현재 활성화된 프로필은 삭제할 수 없습니다."
                            } else {
                                deleteTarget = profile
                            }
                        }
                    )
                }
                RoadStoryFigmaCard(
                    title = "새 프로필 추가",
                    subtitle = "새로운 여행을 떠나볼까요?",
                    selected = false,
                    unit = unit,
                    onClick = onAddProfile,
                    isAdd = true
                )
            }
        }
        warning?.let {
            Text(
                text = it,
                modifier = Modifier.figmaRect(145, 958, 1410, 45, unit),
                color = Color(0xFFB3263E)
            )
        }
        RoadStoryFigmaActionButton(
            title = "여행 시작하기",
            unit = unit,
            enabled = effectiveId != null,
            onClick = { effectiveId?.let(onSelectProfile) }
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("프로필 삭제") },
            text = { Text("'${target.nickname}' 프로필을 삭제하시겠습니까? 삭제 후 복구할 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = {
                    if (target.id == selectedProfileId) {
                        warning = "현재 활성화된 프로필은 삭제할 수 없습니다."
                    } else {
                        onDeleteProfile(target.id)
                    }
                    deleteTarget = null
                }) { Text("삭제", color = Color(0xFFB3263E)) }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("취소") } }
        )
    }
}
