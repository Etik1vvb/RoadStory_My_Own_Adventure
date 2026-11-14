package com.example.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val ROAD_STORY_FIGMA_WIDTH = 1700f
const val ROAD_STORY_FIGMA_HEIGHT = 1184f

val RoadStoryCanvasBackground = Color(0xFFF5F7FA)
val RoadStoryPurple = Color(0xFF352ABA)
val RoadStoryHeadingPurple = Color(0xFF352ABA)
val RoadStorySubtitlePurple = Color(0xFF7975E5)
val RoadStoryCircleSelected = Color(0xFFD6DAFF)
val RoadStoryCircleDefault = Color(0xFFD9D9D9)

@Composable
fun RoadStoryFigmaCanvas(content: @Composable BoxScope.(unit: Float) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize().background(RoadStoryCanvasBackground)) {
        // No arbitrary dp width cap, and no assumption that 1 Figma px == 1 Android dp.
        val unit = minOf(
            maxWidth.value / ROAD_STORY_FIGMA_WIDTH,
            maxHeight.value / ROAD_STORY_FIGMA_HEIGHT
        )
        Box(
            Modifier
                .align(Alignment.Center)
                .size((ROAD_STORY_FIGMA_WIDTH * unit).dp, (ROAD_STORY_FIGMA_HEIGHT * unit).dp)
        ) {
            content(unit)
        }
    }
}

/** Position and size in 1700x1184 Figma reference pixels, converted to dp. */
fun Modifier.figmaRect(x: Number, y: Number, width: Number, height: Number, unit: Float): Modifier =
    this.offset((x.toFloat() * unit).dp, (y.toFloat() * unit).dp)
        .size((width.toFloat() * unit).dp, (height.toFloat() * unit).dp)

@Composable
fun RoadStoryFigmaHeading(title: String, subtitle: String, unit: Float) {
    Box(Modifier.figmaRect(0, 83, 1700, 67, unit), contentAlignment = Alignment.Center) {
        Text(
            title, color = RoadStoryHeadingPurple, fontWeight = FontWeight.Bold,
            fontSize = (46f * unit).sp, lineHeight = (60f * unit).sp,
            textAlign = TextAlign.Center, maxLines = 1
        )
    }
    Box(Modifier.figmaRect(0, 163, 1700, 55, unit), contentAlignment = Alignment.Center) {
        Text(
            subtitle, color = RoadStorySubtitlePurple,
            fontSize = (31f * unit).sp, lineHeight = (43f * unit).sp,
            textAlign = TextAlign.Center, maxLines = 1
        )
    }
}

@Composable
fun RoadStoryFigmaActionButton(
    title: String, unit: Float, onClick: () -> Unit, enabled: Boolean = true
) {
    Box(
        Modifier.figmaRect(143, 1022, 1414, 77, unit)
            .shadow((12f * unit).dp, RoundedCornerShape((45f * unit).dp))
            .clip(RoundedCornerShape((45f * unit).dp))
            .background(if (enabled) RoadStoryPurple else Color(0xFFAAAAB5))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            title, color = Color.White, fontWeight = FontWeight.SemiBold,
            fontSize = (29f * unit).sp, lineHeight = (40f * unit).sp,
            textAlign = TextAlign.Center, maxLines = 1
        )
    }
}

/** One real Compose card; not a PNG screenshot or a canvas bitmap. */
@Composable
fun RoadStoryFigmaCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    unit: Float,
    onClick: () -> Unit,
    isAdd: Boolean = false,
    onManageClick: (() -> Unit)? = null
) {
    val radius = RoundedCornerShape((43f * unit).dp)
    Box(
        Modifier
            .size((434f * unit).dp, (668f * unit).dp)
            .shadow((19f * unit).dp, radius)
            .clip(radius)
            .background(Color.White)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        // Stroke is drawn over the card content so the outer reference dimensions remain fixed.
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            val strokePx = (if (selected) 5f else 1.4f) * unit * density
            drawRoundRect(
                color = if (selected) RoadStoryPurple else Color(0xFFE5E5E5),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(43f * unit * density),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokePx)
            )
        }
        if (isAdd) {
            Box(Modifier.figmaRect(0, 58, 434, 60, unit), contentAlignment = Alignment.Center) {
                Text(
                    title, fontSize = (34f * unit).sp,
                    color = Color(0xFF757575), fontWeight = FontWeight.Bold,
                    maxLines = 1, textAlign = TextAlign.Center
                )
            }
            Box(Modifier.figmaRect(0, 115, 434, 42, unit), contentAlignment = Alignment.Center) {
                Text(
                    subtitle, fontSize = (24f * unit).sp,
                    color = Color(0xFF777777), maxLines = 1, textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                title,
                modifier = Modifier.figmaRect(38, 34, if (onManageClick != null) 317 else 358, 55, unit),
                fontSize = (34f * unit).sp, lineHeight = (44f * unit).sp,
                color = if (selected) RoadStoryPurple else Color(0xFF242424),
                fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                subtitle,
                modifier = Modifier.figmaRect(38, 95, 366, 105, unit),
                fontSize = (24f * unit).sp, lineHeight = (33f * unit).sp,
                color = if (selected) RoadStorySubtitlePurple else Color(0xFF777777),
                maxLines = 3, overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            Modifier.figmaRect(41, 216, 352, 352, unit)
                .background(if (selected) RoadStoryCircleSelected else RoadStoryCircleDefault, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isAdd) {
                Text(
                    "+", color = Color(0xFF808080),
                    fontSize = (128f * unit).sp,
                    fontWeight = FontWeight.Light, lineHeight = (140f * unit).sp
                )
            }
        }
        if (onManageClick != null) {
            Box(
                Modifier.figmaRect(345, 10, 75, 75, unit)
                    .clickable(role = Role.Button, onClick = onManageClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⋯",
                    fontSize = (39f * unit).sp,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}
