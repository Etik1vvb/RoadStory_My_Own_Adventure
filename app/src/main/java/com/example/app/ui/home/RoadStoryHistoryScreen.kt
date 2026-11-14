package com.example.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.BoxWithConstraints
import com.example.app.R
import com.example.app.data.SavedTrip
import com.example.app.data.TripStatus
import com.example.app.ui.theme.AstaSans

// TravelHistory.png / TravelHistory_2.png: separate real screens, not tabs.
private val RS_VIOLET = Color(0xFF352ABA)
private val RS_BG = Color(0xFFF5F7FA)
private val RS_INK = Color(0xFF171717)
private val RS_SECONDARY = Color(0xFF585858)
private val RS_DELETE = Color(0xFFF04B68)

@Composable
private fun TripListLayout(
    referenceWidth: Float = 1704f,
    onBack: () -> Unit,
    content: @Composable ColumnScope.(Float) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(RS_BG)) {
        // Figma's X coordinates scale to the ACTUAL app viewport width, not device dp.
        val u = maxWidth.value / referenceWidth
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = (38f*u).dp, vertical = (25f*u).dp),
            verticalArrangement = Arrangement.spacedBy((10f*u).dp)
        ) {
            Row(Modifier.fillMaxWidth().height((60f*u).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.roadstory_logo), "Road story",
                    Modifier.width((220f * u).dp)
                        .height((58f * u).dp),
                    contentScale = ContentScale.Fit)
                Text("메인 화면으로 돌아가기  ❯", Modifier.clickable(onClick = onBack),
                    color = RS_VIOLET, fontFamily = AstaSans,
                    fontWeight = FontWeight.Bold, fontSize = (25f*u).sp)
            }
            content(u)
        }
    }
}

@Composable
private fun TripWhiteCard(u: Float, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .shadow((23f*u).dp, RoundedCornerShape((44f*u).dp))
            .clip(RoundedCornerShape((44f*u).dp)).background(Color.White)
            .padding(horizontal = (42f*u).dp, vertical = (25f*u).dp),
        verticalArrangement = Arrangement.spacedBy((8f*u).dp),
        content = content
    )
}

@Composable
private fun TripTitleRow(trip: SavedTrip, u: Float, onDelete: (SavedTrip) -> Unit, onTitleClick: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top) {
        Text(trip.info.destination.ifBlank { "여행 기록" },
            modifier = if (onTitleClick != null) Modifier.clickable { onTitleClick() } else Modifier,
            fontFamily = AstaSans, fontWeight = FontWeight.Bold,
            fontSize = (34f*u).sp, color = RS_INK)
        Text("여행 기록 삭제하기", Modifier.clickable { onDelete(trip) },
            fontFamily = AstaSans, fontSize = (18f*u).sp, color = RS_DELETE,
            style = TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline))
    }
}

@Composable
private fun TripMeta(trip: SavedTrip, u: Float, showActive: Boolean, destination: String? = null) {
    Text("${trip.info.startDate} - ${trip.info.endDate}", fontFamily = AstaSans,
        fontSize = (27f*u).sp, color = RS_SECONDARY)
    Spacer(Modifier.height((17f*u).dp))
    Text("동행자: ${trip.info.companions}", fontFamily = AstaSans,
        fontSize = (26f*u).sp, color = RS_SECONDARY)
    if (showActive) {
        Text(if (trip.status == TripStatus.ACTIVE) "여행 중 - 현재 선택" else "출발 전",
            fontFamily = AstaSans, fontSize = (24f*u).sp, color = RS_SECONDARY)
        if (trip.status == TripStatus.ACTIVE) {
            Text("현재 목적지: ${destination ?: "미설정"}", fontFamily = AstaSans,
                fontSize = (24f*u).sp, color = RS_SECONDARY)
        }
    }
}

@Composable
private fun TripPurpleButton(label: String, u: Float, onClick: () -> Unit, enabled: Boolean = true) {
    Box(
        Modifier.fillMaxWidth().height((82f*u).dp)
            .clip(RoundedCornerShape((32f*u).dp))
            .background(if (enabled) RS_VIOLET else Color(0xFFB4B4B9))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontFamily = AstaSans, fontSize = (27f*u).sp,
            fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
    }
}

/** TravelHistory.png: only completed journeys, exactly one responsibility. */
@Composable
fun RoadStoryHistoryScreen(
    trips: List<SavedTrip>,
    onBack: () -> Unit,
    onRepeat: (SavedTrip) -> Unit,
    onDelete: (SavedTrip) -> Unit,
    ratingContent: @Composable (SavedTrip) -> Unit
) {
    val completed = trips.filter { it.status == TripStatus.COMPLETED }
    TripListLayout(onBack = onBack) { u ->
        if (completed.isEmpty()) {
            TripWhiteCard(u) {
                Text("완료된 여행이 없습니다.", fontFamily = AstaSans,
                    color = RS_INK, fontSize = (27f*u).sp)
            }
        }
        completed.forEach { trip ->
            key(trip.id) {
                var showRating by remember(trip.id) { mutableStateOf(false) }
                TripWhiteCard(u) {
                    TripTitleRow(trip, u, onDelete, onTitleClick = { showRating = !showRating })
                    TripMeta(trip, u, showActive = false)
                    Spacer(Modifier.height((15f*u).dp))
                    TripPurpleButton("다시 여행하기", u, onClick = { onRepeat(trip) })
                    // Preserve the old rating capability without altering the Figma card at rest.
                    if (showRating) {
                        Spacer(Modifier.height((12f*u).dp))
                        ratingContent(trip)
                    }
                }
                Spacer(Modifier.height((62f*u).dp))
            }
        }
    }
}

/** TravelHistory_2.png: active and planned journeys only, independent of completed history. */
@Composable
fun RoadStoryOngoingTripsScreen(
    trips: List<SavedTrip>,
    destinations: Map<String, String>,
    onBack: () -> Unit,
    onContinue: (SavedTrip) -> Unit,
    onStart: (SavedTrip) -> Unit,
    onChooseDestination: (SavedTrip) -> Unit,
    onEdit: (SavedTrip) -> Unit,
    onComplete: (SavedTrip) -> Unit,
    onDelete: (SavedTrip) -> Unit,
    errorMessage: String? = null
) {
    val ongoing = trips.filter { it.status != TripStatus.COMPLETED }
        .sortedWith(compareBy<SavedTrip> { it.status != TripStatus.ACTIVE })
    TripListLayout(onBack = onBack) { u ->
        errorMessage?.let { message ->
            Text(message, fontFamily = AstaSans, color = Color(0xFFC0354F),
                fontSize = (23f*u).sp)
        }
        if (ongoing.isEmpty()) {
            TripWhiteCard(u) {
                Text("진행 중이거나 출발 전인 여행이 없습니다.", fontFamily = AstaSans,
                    color = RS_INK, fontSize = (27f*u).sp)
            }
        }
        ongoing.forEach { trip ->
            key(trip.id) {
                var expanded by remember(trip.id) { mutableStateOf(trip.status == TripStatus.ACTIVE) }
                TripWhiteCard(u) {
                    TripTitleRow(trip, u, onDelete, onTitleClick = { expanded = !expanded })
                    TripMeta(trip, u, showActive = expanded, destination = destinations[trip.id])
                    if (expanded) {
                        Row(Modifier.fillMaxWidth().padding(top = (7f*u).dp),
                            horizontalArrangement = Arrangement.spacedBy((58f*u).dp)) {
                            FigmaSecondaryButton("다음 목적지 선택 / 변경", u,
                                Modifier.weight(1f)) { onChooseDestination(trip) }
                            FigmaSecondaryButton("체크인 수정", u,
                                Modifier.weight(1f)) { onEdit(trip) }
                            FigmaSecondaryButton(
                                if (trip.status == TripStatus.ACTIVE) "여행 종료하기" else "여행 시작하기",
                                u, Modifier.weight(1f)
                            ) { if (trip.status == TripStatus.ACTIVE) onComplete(trip) else onStart(trip) }
                        }
                    }
                    Spacer(Modifier.height((6f*u).dp))
                    TripPurpleButton(if (trip.status == TripStatus.ACTIVE) "여행 계속 이어나가기" else "다시 여행하기",
                        u, { if (trip.status == TripStatus.ACTIVE) onContinue(trip) else onStart(trip) })
                    // Tap the card title to expand/collapse; no non-Figma controls added.
                }
                Spacer(Modifier.height((62f*u).dp))
            }
        }
    }
}

@Composable
private fun FigmaSecondaryButton(
    label: String, u: Float, modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Box(
        modifier.height((78f*u).dp)
            .clip(RoundedCornerShape((34f*u).dp))
            .background(Color(0xFFF4F4F4))
            .clickable(onClick = onClick), contentAlignment = Alignment.Center
    ) {
        Text(label, fontFamily = AstaSans, fontSize = (22f*u).sp,
            fontWeight = FontWeight.Medium, color = RS_SECONDARY, maxLines = 1)
    }
}
