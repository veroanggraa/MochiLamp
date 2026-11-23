package com.veroanggra.mochilamp.ui.lamp.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.veroanggra.mochilamp.model.LampFace
import com.veroanggra.mochilamp.ui.theme.FaceStrokeDay
import com.veroanggra.mochilamp.ui.theme.GlowYellow
import com.veroanggra.mochilamp.ui.theme.KawaiiBlush
import com.veroanggra.mochilamp.ui.theme.NightEyeStroke
import com.veroanggra.mochilamp.ui.theme.NightMouthDot
import com.veroanggra.mochilamp.ui.theme.SleepyBlush
import com.veroanggra.mochilamp.ui.theme.StarLavender
import com.veroanggra.mochilamp.ui.theme.SurprisedPupil

fun DrawScope.drawLampFace(
    centerX: Float,
    centerY: Float,
    face: LampFace,
    isLightOn: Boolean,
    blinkScaleY: Float,
    pupilOffsetX: Float = 0f,
    pupilOffsetY: Float = 0f,
    zzzTime: Float = 0f,
) {
    val faceStrokeColor = if (isLightOn) FaceStrokeDay else GlowYellow
    val blushColor = KawaiiBlush

    when (face) {
        LampFace.SLEEPY -> {
            // Mata tidur damai 'u  u'
            val leftEye = Path().apply {
                moveTo(centerX - 32f, centerY)
                quadraticTo(centerX - 24f, centerY + 8f, centerX - 16f, centerY)
            }
            val rightEye = Path().apply {
                moveTo(centerX + 16f, centerY)
                quadraticTo(centerX + 24f, centerY + 8f, centerX + 32f, centerY)
            }
            drawPath(
                path = leftEye,
                color = NightEyeStroke,
                style = Stroke(width = 3.2f, cap = StrokeCap.Round)
            )
            drawPath(
                path = rightEye,
                color = NightEyeStroke,
                style = Stroke(width = 3.2f, cap = StrokeCap.Round)
            )

            // Pipi tidur lembut
            drawOval(
                color = SleepyBlush.copy(alpha = 0.28f),
                topLeft = Offset(centerX - 47f, centerY + 5.5f),
                size = Size(16f, 9f)
            )
            drawOval(
                color = SleepyBlush.copy(alpha = 0.28f),
                topLeft = Offset(centerX + 31f, centerY + 5.5f),
                size = Size(16f, 9f)
            )

            // Mulut kecil
            drawCircle(
                color = NightMouthDot.copy(alpha = 0.9f),
                radius = 2.6f,
                center = Offset(centerX, centerY + 11f)
            )

            // Gelembung zZz melayang
            drawZzzBubble(centerX + 62f, centerY - 26f, "z", 11f, zzzTime, 0f)
            drawZzzBubble(centerX + 72f, centerY - 38f, "Z", 14f, zzzTime, 1f)
            drawZzzBubble(centerX + 82f, centerY - 50f, "z", 10f, zzzTime, 2f)
        }

        LampFace.DRAGGING -> {
            // Mata menyipit tegang '> <'
            val leftEye = Path().apply {
                moveTo(centerX - 31f, centerY - 5f)
                lineTo(centerX - 19f, centerY + 1f)
                lineTo(centerX - 31f, centerY + 7f)
            }
            val rightEye = Path().apply {
                moveTo(centerX + 31f, centerY - 5f)
                lineTo(centerX + 19f, centerY + 1f)
                lineTo(centerX + 31f, centerY + 7f)
            }
            drawPath(
                path = leftEye,
                color = faceStrokeColor,
                style = Stroke(width = 3.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = rightEye,
                color = faceStrokeColor,
                style = Stroke(width = 3.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Pipi ekstra merona
            drawOval(
                color = blushColor.copy(alpha = 0.58f),
                topLeft = Offset(centerX - 49f, centerY + 6f),
                size = Size(18f, 10f)
            )
            drawOval(
                color = blushColor.copy(alpha = 0.58f),
                topLeft = Offset(centerX + 31f, centerY + 6f),
                size = Size(18f, 10f)
            )

            // Mulut tegang kucing
            val dragMouth = Path().apply {
                moveTo(centerX - 6f, centerY + 10f)
                quadraticTo(centerX - 3f, centerY + 14f, centerX, centerY + 10f)
                quadraticTo(centerX + 3f, centerY + 14f, centerX + 6f, centerY + 10f)
            }
            drawPath(
                path = dragMouth,
                color = faceStrokeColor,
                style = Stroke(width = 2.6f, cap = StrokeCap.Round)
            )
        }

        LampFace.SURPRISED -> {
            // Mata bulat kaget 'O O'
            drawCircle(SurprisedPupil, radius = 7.5f, center = Offset(centerX - 24f, centerY))
            drawCircle(Color.White, radius = 2.6f, center = Offset(centerX - 26.5f, centerY - 2.5f))
            drawCircle(SurprisedPupil, radius = 7.5f, center = Offset(centerX + 24f, centerY))
            drawCircle(Color.White, radius = 2.6f, center = Offset(centerX + 21.5f, centerY - 2.5f))

            // Pipi super merona
            drawOval(
                color = blushColor.copy(alpha = 0.65f),
                topLeft = Offset(centerX - 50.5f, centerY + 5.8f),
                size = Size(19f, 10.4f)
            )
            drawOval(
                color = blushColor.copy(alpha = 0.65f),
                topLeft = Offset(centerX + 31.5f, centerY + 5.8f),
                size = Size(19f, 10.4f)
            )

            // Mulut bulat kaget 'ᴥ'
            val surprisedMouth = Path().apply {
                moveTo(centerX - 7f, centerY + 8f)
                quadraticTo(centerX - 3.5f, centerY + 14f, centerX, centerY + 9f)
                quadraticTo(centerX + 3.5f, centerY + 14f, centerX + 7f, centerY + 8f)
            }
            drawPath(
                path = surprisedMouth,
                color = SurprisedPupil,
                style = Stroke(width = 2.6f, cap = StrokeCap.Round)
            )
        }

        LampFace.HAPPY -> {
            // Mata berbinar bangun + kedip & pupil tracking
            scale(scaleX = 1f, scaleY = blinkScaleY, pivot = Offset(centerX, centerY)) {
                // Mata Kiri
                drawCircle(
                    color = FaceStrokeDay,
                    radius = 7.2f,
                    center = Offset(centerX - 24f + pupilOffsetX, centerY + pupilOffsetY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.6f,
                    center = Offset(centerX - 26.4f + pupilOffsetX, centerY - 2.6f + pupilOffsetY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 1.3f,
                    center = Offset(centerX - 21.4f + pupilOffsetX, centerY + 2.8f + pupilOffsetY)
                )

                // Mata Kanan
                drawCircle(
                    color = FaceStrokeDay,
                    radius = 7.2f,
                    center = Offset(centerX + 24f + pupilOffsetX, centerY + pupilOffsetY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.6f,
                    center = Offset(centerX + 21.6f + pupilOffsetX, centerY - 2.6f + pupilOffsetY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 1.3f,
                    center = Offset(centerX + 26.6f + pupilOffsetX, centerY + 2.8f + pupilOffsetY)
                )
            }

            // Pipi merah muda
            drawOval(
                color = blushColor.copy(alpha = 0.45f),
                topLeft = Offset(centerX - 48.5f, centerY + 6f),
                size = Size(18f, 10f)
            )
            drawOval(
                color = blushColor.copy(alpha = 0.45f),
                topLeft = Offset(centerX + 30.5f, centerY + 6f),
                size = Size(18f, 10f)
            )

            // Senyum kucing (ω)
            val smilePath = Path().apply {
                moveTo(centerX - 9f, centerY + 8f)
                quadraticTo(centerX - 4.5f, centerY + 14f, centerX, centerY + 8.5f)
                quadraticTo(centerX + 4.5f, centerY + 14f, centerX + 9f, centerY + 8f)
            }
            drawPath(
                path = smilePath,
                color = FaceStrokeDay,
                style = Stroke(width = 2.8f, cap = StrokeCap.Round)
            )
        }

        LampFace.BLUSH -> {
            // Mata tersenyum '^ ^'
            val leftEye = Path().apply {
                moveTo(centerX - 32f, centerY + 3f)
                quadraticTo(centerX - 24f, centerY - 6f, centerX - 16f, centerY + 3f)
            }
            val rightEye = Path().apply {
                moveTo(centerX + 16f, centerY + 3f)
                quadraticTo(centerX + 24f, centerY - 6f, centerX + 32f, centerY + 3f)
            }
            drawPath(
                path = leftEye,
                color = if (isLightOn) FaceStrokeDay else NightEyeStroke,
                style = Stroke(width = 3.2f, cap = StrokeCap.Round)
            )
            drawPath(
                path = rightEye,
                color = if (isLightOn) FaceStrokeDay else NightEyeStroke,
                style = Stroke(width = 3.2f, cap = StrokeCap.Round)
            )

            // Pipi ekstra merona
            drawOval(
                color = blushColor.copy(alpha = 0.65f),
                topLeft = Offset(centerX - 50f, centerY + 5.5f),
                size = Size(20f, 11f)
            )
            drawOval(
                color = blushColor.copy(alpha = 0.65f),
                topLeft = Offset(centerX + 30f, centerY + 5.5f),
                size = Size(20f, 11f)
            )

            // Senyum manis
            val shySmile = Path().apply {
                moveTo(centerX - 7f, centerY + 8f)
                quadraticTo(centerX, centerY + 16f, centerX + 7f, centerY + 8f)
            }
            drawPath(
                path = shySmile,
                color = if (isLightOn) FaceStrokeDay else NightEyeStroke,
                style = Stroke(width = 2.8f, cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Satu gelembung "z" yang melayang naik sambil memudar, meniru @keyframes floatZzz pada versi web.
 * [timeSeconds] adalah waktu berjalan (detik, 0..3 lalu berulang) dan [delaySeconds] menggeser fase
 * tiap gelembung agar munculnya bertahap (staggered), sama seperti animation-delay di CSS.
 */
private fun DrawScope.drawZzzBubble(
    baseX: Float,
    baseY: Float,
    text: String,
    fontSize: Float,
    timeSeconds: Float,
    delaySeconds: Float,
) {
    val cycleDuration = 3f
    val progress = ((timeSeconds + delaySeconds) % cycleDuration) / cycleDuration

    val alpha = when {
        progress <= 0.25f -> lerp(0f, 0.85f, progress / 0.25f)
        progress <= 0.75f -> lerp(0.85f, 0.65f, (progress - 0.25f) / 0.5f)
        else -> lerp(0.65f, 0f, (progress - 0.75f) / 0.25f)
    }
    if (alpha <= 0.01f) return

    val dx = lerp(0f, 18f, progress)
    val dy = lerp(6f, -30f, progress)
    val bubbleScale = lerp(0.65f, 1.15f, progress)
    val rotationDeg = lerp(-10f, 12f, progress)

    drawContext.canvas.nativeCanvas.apply {
        save()
        translate(baseX + dx, baseY + dy)
        rotate(rotationDeg)
        scale(bubbleScale, bubbleScale)
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = StarLavender.copy(alpha = alpha).toArgb()
            textSize = fontSize
            isFakeBoldText = true
            textAlign = android.graphics.Paint.Align.LEFT
        }
        drawText(text, 0f, 0f, paint)
        restore()
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction
