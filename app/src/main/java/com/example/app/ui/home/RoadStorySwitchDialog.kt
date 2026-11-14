package com.example.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.app.ui.theme.AstaSans

/** Figma TravelHistory_Warning.png: purely presentational, switching is delegated to the existing repository. */
@Composable
fun RoadStorySwitchDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val u = maxWidth.value / 1704f
            Column(
                Modifier.width((810f*u).dp)
                    .clip(RoundedCornerShape((47f*u).dp))
                    .background(Color.White)
                    .padding(horizontal = (44f*u).dp, vertical = (46f*u).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((24f*u).dp)
            ) {
                Box(Modifier.size((70f*u).dp).background(Color(0xFF343434), CircleShape),
                    contentAlignment = Alignment.Center) {
                    Text("!", fontFamily = AstaSans, color = Color.White,
                        fontWeight = FontWeight.Black, fontSize = (43f*u).sp)
                }
                Text("진행 중인 여행이 있습니다", color = Color(0xFF151515),
                    fontFamily = AstaSans, fontWeight = FontWeight.Bold,
                    fontSize = (39f*u).sp, textAlign = TextAlign.Center)
                Text("새 여행의 목적지를 설정하시겠습니까?", color = Color(0xFF353535),
                    fontFamily = AstaSans, fontWeight = FontWeight.SemiBold,
                    fontSize = (31f*u).sp, textAlign = TextAlign.Center)
                Text("새 여행을 시작하면 기존 여행은 준비 상태로 전환되어\n나중에 다시 시작하실 수 있습니다.",
                    fontFamily = AstaSans, fontSize = (26f*u).sp,
                    color = Color(0xFF424242), textAlign = TextAlign.Center,
                    lineHeight = (35f*u).sp)
                Spacer(Modifier.height((20f*u).dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy((20f*u).dp)) {
                    SwitchDialogButton("새 여행 시작하기", true, u, Modifier.weight(1f), onConfirm)
                    SwitchDialogButton("기존 여행으로 돌아가기", false, u, Modifier.weight(1f), onDismiss)
                }
            }
        }
    }
}

@Composable
private fun SwitchDialogButton(
    label: String, primary: Boolean, unit: Float, modifier: Modifier, onClick: () -> Unit
) {
    Box(modifier.height((96f*unit).dp)
        .clip(RoundedCornerShape((33f*unit).dp))
        .background(if (primary) Color(0xFF352ABA) else Color(0xFFF1F1F1))
        .clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, fontFamily = AstaSans, fontSize = (25f*unit).sp,
            fontWeight = FontWeight.Bold,
            color = if (primary) Color.White else Color(0xFF202020),
            textAlign = TextAlign.Center)
    }
}
