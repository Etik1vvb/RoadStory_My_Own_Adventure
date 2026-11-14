package com.example.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Road story Figma reference is 967 x 674px, measured within app content (system bar excluded).
 * The Pleos emulator can expose far more dp than those Figma pixels, so fixed dp sizes
 * visually shrink everything. Scale ALL dimensions together, including typography.
 */
internal val IntroBackground = Color(0xFFF5F7FA)
internal val IntroBlue = Color(0xFF352ABA)
internal val IntroTitleBlue = Color(0xFF3028C9)
internal val IntroSubPurple = Color(0xFF7975E5)
private val IntroChosenCircle = Color(0xFFD6DAFF)
private val IntroNeutralCircle = Color(0xFFD9D9D9)

/** Scale design space to real Compose constraints while supporting narrower split-screen windows. */
internal fun introScreenScale(width: Dp, height: Dp, compact: Boolean): Float {
    val horizontal = width.value / (if (compact) 650f else 967f)
    val vertical = height.value / 674f
    return minOf(horizontal, vertical).coerceAtLeast(0.55f)
}

@Composable
fun RoadStoryIntroHeading(title: String, subtitle: String, compact: Boolean, scale: Float) {
    Text(
        text = title,
        color = IntroTitleBlue,
        fontWeight = FontWeight.Bold,
        fontSize = (if (compact) 24 else 26).sp * scale,
        lineHeight = (if (compact) 31 else 34).sp * scale,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(13.dp * scale))
    Text(
        text = subtitle,
        color = IntroSubPurple,
        fontSize = (if (compact) 16 else 18).sp * scale,
        lineHeight = (if (compact) 22 else 24).sp * scale,
        textAlign = TextAlign.Center
    )
}

/** 선택 카드, 오른쪽의 관리 메뉴는 기존 프로필 삭제 동작을 유지합니다. */
@Composable
fun RoadStoryChoiceCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    width: Dp,
    height: Dp,
    scale: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    addCard: Boolean = false,
    onManageClick: (() -> Unit)? = null
) {
    val circleSize = minOf(200.dp * scale, width * 0.81f, height * 0.56f)
    Card(
        onClick = onClick,
        modifier = modifier.width(width).height(height),
        shape = RoundedCornerShape(26.dp * scale),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(if (selected) 2.5.dp * scale else 0.6.dp * scale,
            if (selected) IntroTitleBlue else Color(0xFFE8E9EB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 9.dp * scale)
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxWidth().padding(
                    start = 21.dp * scale,
                    end = (if (onManageClick != null) 40 else 17).dp * scale,
                    top = 19.dp * scale
                )
            ) {
                Text(
                    title,
                    color = if (selected) IntroTitleBlue else Color(0xFF222222),
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp * scale,
                    lineHeight = 25.sp * scale
                )
                Spacer(Modifier.height(4.dp * scale))
                Text(
                    subtitle,
                    color = if (selected) IntroSubPurple else Color(0xFF737373),
                    fontSize = 14.sp * scale,
                    lineHeight = 20.sp * scale
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = height * 0.15f)
                    .size(circleSize)
                    .background(if (selected) IntroChosenCircle else IntroNeutralCircle, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (addCard) {
                    Text(
                        "+",
                        color = Color(0xFF808080),
                        fontSize = 80.sp * scale,
                        fontWeight = FontWeight.Light,
                        lineHeight = 84.sp * scale
                    )
                }
            }
            if (onManageClick != null) {
                TextButton(
                    onClick = onManageClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(end = 3.dp * scale, top = 2.dp * scale),
                    contentPadding = PaddingValues(horizontal = 4.dp * scale, vertical = 0.dp)
                ) {
                    Text("⋯", fontSize = 22.sp * scale, color = Color(0xFF888888))
                }
            }
        }
    }
}

@Composable
fun RoadStoryIntroButton(
    label: String,
    scale: Float,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(44.dp * scale),
        shape = RoundedCornerShape(50.dp * scale),
        colors = ButtonDefaults.buttonColors(containerColor = IntroBlue),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp * scale),
        contentPadding = PaddingValues(horizontal = 12.dp * scale, vertical = 0.dp)
    ) {
        Text(label, fontSize = 16.sp * scale, fontWeight = FontWeight.SemiBold)
    }
}
