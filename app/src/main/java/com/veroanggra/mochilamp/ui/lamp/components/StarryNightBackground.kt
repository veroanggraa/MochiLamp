package com.veroanggra.mochilamp.ui.lamp.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import com.veroanggra.mochilamp.ui.theme.GlowYellow
import com.veroanggra.mochilamp.ui.theme.LavenderMist
import com.veroanggra.mochilamp.ui.theme.StarLavender
import com.veroanggra.mochilamp.ui.theme.StarYellow
import kotlin.math.sin

data class StarData(
    val relX: Float,
    val relY: Float,
    val radius: Float,
    val color: Color,
    val isCross: Boolean,
    val speedFactor: Float,
    val phaseOffset: Float,
)

@Composable
fun StarryNightBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "StarryAnimation")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "animTime",
    )

    val stars = listOf(
        StarData(54f / 388f, 118f / 760f, 2.2f, StarYellow, isCross = false, speedFactor = 1.0f, phaseOffset = 0.2f),
        StarData(96f / 388f, 182f / 760f, 1.8f, StarLavender, isCross = false, speedFactor = 0.8f, phaseOffset = 1.1f),
        StarData(328f / 388f, 132f / 760f, 2.4f, GlowYellow, isCross = false, speedFactor = 1.2f, phaseOffset = 0.6f),
        StarData(348f / 388f, 224f / 760f, 1.8f, LavenderMist, isCross = false, speedFactor = 0.9f, phaseOffset = 1.5f),
        StarData(42f / 388f, 296f / 760f, 1.8f, LavenderMist, isCross = false, speedFactor = 0.7f, phaseOffset = 0.4f),
        StarData(316f / 388f, 340f / 760f, 1.6f, StarYellow, isCross = false, speedFactor = 1.0f, phaseOffset = 1.8f),
        StarData(72f / 388f, 445f / 760f, 1.5f, StarLavender, isCross = false, speedFactor = 0.9f, phaseOffset = 0.9f),
        StarData(332f / 388f, 488f / 760f, 1.8f, LavenderMist, isCross = false, speedFactor = 1.1f, phaseOffset = 1.3f),
        StarData(68f / 388f, 155f / 760f, 6.0f, GlowYellow, isCross = true, speedFactor = 0.9f, phaseOffset = 0.5f),
        StarData(312f / 388f, 189f / 760f, 5.5f, StarYellow, isCross = true, speedFactor = 1.1f, phaseOffset = 1.4f)
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        stars.forEach { (relX, relY, radius, color, isCross, speedFactor, phaseOffset) ->
            val phase = ((animProgress * speedFactor + phaseOffset) % (2 * Math.PI)).toFloat()
            val floatOffsetY = sin(phase) * 5f
            val alpha = (0.50f + 0.45f * sin(phase)).coerceIn(0.15f, 1f)
            val pulseScale = 0.92f + 0.22f * sin(phase + 0.5f)

            val currentCenter = Offset(
                x = relX * w,
                y = relY * h + floatOffsetY
            )

            scale(scale = pulseScale, pivot = currentCenter) {
                if (isCross) {
                    drawKawaiiCrossStar(
                        center = currentCenter,
                        size = radius * 2f,
                        color = color.copy(alpha = alpha)
                    )
                } else {
                    drawCircle(
                        color = color.copy(alpha = alpha),
                        radius = radius,
                        center = currentCenter
                    )
                    drawCircle(
                        color = color.copy(alpha = alpha * 0.35f),
                        radius = radius * 2.2f,
                        center = currentCenter
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawKawaiiCrossStar(center: Offset, size: Float, color: Color) {
    val half = size / 2f
    val inner = size * 0.18f

    val path = Path().apply {
        moveTo(center.x, center.y - half)
        quadraticTo(center.x, center.y - inner, center.x + half, center.y)
        quadraticTo(center.x + inner, center.y, center.x, center.y + half)
        quadraticTo(center.x, center.y + inner, center.x - half, center.y)
        quadraticTo(center.x - inner, center.y, center.x, center.y - half)
        close()
    }
    drawPath(path = path, color = color)
}
