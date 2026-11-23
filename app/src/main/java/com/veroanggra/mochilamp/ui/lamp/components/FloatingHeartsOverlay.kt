package com.veroanggra.mochilamp.ui.lamp.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import com.veroanggra.mochilamp.model.FloatingHeart
import kotlin.math.sin

@Composable
fun FloatingHeartsOverlay(
    hearts: List<FloatingHeart>,
    modifier: Modifier = Modifier,
) {
    if (hearts.isEmpty()) return

    Canvas(modifier = modifier.fillMaxSize()) {
        // Heart coordinates (startX/startY, drift, size) are logical units matching
        // LampCanvas's fixed 360-unit space, so scale them the same way to the real canvas size.
        val canvasScale = size.minDimension / 360f

        scale(scale = canvasScale, pivot = Offset.Zero) {
            // Must match LampCanvas's extra zoom so hearts spawn aligned with the bigger face.
            val extraZoom = 1.35f
            drawContext.transform.scale(scaleX = extraZoom, scaleY = extraZoom, pivot = Offset(180f, 10f))

            hearts.forEach { heart ->
                val currentY = heart.startY - (heart.progress * 220f)
                val currentX = heart.startX + (sin(heart.progress * Math.PI.toFloat()) * heart.driftX)
                val alpha = (1f - heart.progress).coerceIn(0f, 1f)
                val heartScale = 0.6f + (heart.progress * 0.6f)

                scale(scale = heartScale, pivot = Offset(currentX, currentY)) {
                    drawHeart(
                        center = Offset(currentX, currentY),
                        size = 28f,
                        color = heart.color.copy(alpha = alpha)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawHeart(center: Offset, size: Float, color: Color) {
    val half = size / 2f
    val path = Path().apply {
        moveTo(center.x, center.y + half * 0.8f)
        cubicTo(
            center.x - half * 1.3f, center.y - half * 0.2f,
            center.x - half * 0.9f, center.y - half * 1.2f,
            center.x, center.y - half * 0.45f
        )
        cubicTo(
            center.x + half * 0.9f, center.y - half * 1.2f,
            center.x + half * 1.3f, center.y - half * 0.2f,
            center.x, center.y + half * 0.8f
        )
        close()
    }
    drawPath(path = path, color = color)
}
