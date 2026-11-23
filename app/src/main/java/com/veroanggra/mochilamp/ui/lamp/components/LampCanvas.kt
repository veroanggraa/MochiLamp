package com.veroanggra.mochilamp.ui.lamp.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerRounding
import androidx.compose.foundation.shape.MorphPolygonShape
import androidx.compose.foundation.shape.PolygonShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.LayoutDirection
import com.veroanggra.mochilamp.model.LampFace
import com.veroanggra.mochilamp.model.LampTint
import com.veroanggra.mochilamp.ui.theme.AmberAccent
import com.veroanggra.mochilamp.ui.theme.AmberDeep
import com.veroanggra.mochilamp.ui.theme.AmberPale
import com.veroanggra.mochilamp.ui.theme.DayRimShadow
import com.veroanggra.mochilamp.ui.theme.GlowYellow
import com.veroanggra.mochilamp.ui.theme.HaloFadeYellow
import com.veroanggra.mochilamp.ui.theme.NightBead
import com.veroanggra.mochilamp.ui.theme.NightBulb
import com.veroanggra.mochilamp.ui.theme.NightCord
import com.veroanggra.mochilamp.ui.theme.NightEarInner
import com.veroanggra.mochilamp.ui.theme.NightRimShadow
import com.veroanggra.mochilamp.ui.theme.NightShadeBottom
import com.veroanggra.mochilamp.ui.theme.NightShadeTop
import com.veroanggra.mochilamp.ui.theme.NightSocket
import com.veroanggra.mochilamp.ui.theme.NightWire
import com.veroanggra.mochilamp.ui.theme.StarYellow

@Composable
fun LampCanvas(
    modifier: Modifier = Modifier,
    isLightOn: Boolean,
    tint: LampTint,
    face: LampFace,
    pullX: Float,
    pullY: Float,
    squish: Float,
    blinkScaleY: Float,
    pupilOffsetX: Float = 0f,
    pupilOffsetY: Float = 0f,
    onLampTap: () -> Unit,
) {
    // MorphPolygonShape is annotated @RememberInComposition: it caches an expensive Morph
    // (vertex-pairing between the two polygons) internally per instance. Building a fresh one
    // every draw — which happens every frame while dragging — discarded that cache each time
    // and was the actual cause of the drag feeling sluggish. Hoisting it here via remember keeps
    // one instance alive across recompositions so the cache (and its per-frame Path reuse) holds;
    // only the live progress value (read through a plain, non-State holder to avoid any snapshot
    // overhead) needs to change each frame.
    val starCharmProgress = remember { MorphProgressHolder() }
    val starCharmMorphShape = remember {
        MorphPolygonShape(StarCharmCalmShape, StarCharmPuffedShape) { starCharmProgress.value }
    }

    val zzzTransition = rememberInfiniteTransition(label = "zzzFloat")
    val zzzTime by zzzTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "zzzTime",
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onLampTap,
            )
    ) {
        // The whole lamp is designed in a fixed 360x360 logical unit space (matching the
        // original SVG viewBox). The Canvas itself is a 360.dp box, so its actual pixel size
        // varies with device density — scale the drawing uniformly to that real pixel size
        // instead of treating the logical units as raw pixels (which shrank the lamp on any
        // non-mdpi device).
        val logicalSize = 360f
        val canvasScale = size.minDimension / logicalSize

        scale(scale = canvasScale, pivot = Offset.Zero) {
            val centerX = 180f
            val centerY = 140f
            val logicalBottom = size.height / canvasScale

            // Extra "camera zoom" on top of the box-fit scale above, centered where the wire
            // meets the ceiling. The bear's silhouette only fills a fraction of the 360-unit
            // canvas, so this makes it render bigger using that existing margin instead of
            // growing the Box itself (which gets clamped once it exceeds the screen width).
            val extraZoom = 1.35f
            drawContext.transform.scale(scaleX = extraZoom, scaleY = extraZoom, pivot = Offset(centerX, 10f))

            // 0. Soft Light Cone Beam in Light Mode
            if (isLightOn) {
                val lightConePath = Path().apply {
                    moveTo(centerX - 78f, centerY + 43f)
                    lineTo(centerX - 180f, logicalBottom)
                    lineTo(centerX + 180f, logicalBottom)
                    lineTo(centerX + 78f, centerY + 43f)
                    close()
                }
                drawPath(
                    path = lightConePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            tint.dotColor.copy(alpha = 0.38f),
                            tint.burstBg.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        startY = centerY + 43f,
                        endY = logicalBottom
                    )
                )
            }

            // 1. Ceiling Cap (M162 0 H198 C198 8 190 13 180 13 C170 13 162 8 162 0 Z)
            val ceilingCapPath = Path().apply {
                moveTo(centerX - 18f, 0f)
                lineTo(centerX + 18f, 0f)
                cubicTo(centerX + 18f, 8f, centerX + 10f, 13f, centerX, 13f)
                cubicTo(centerX - 10f, 13f, centerX - 18f, 8f, centerX - 18f, 0f)
                close()
            }
            drawPath(
                path = ceilingCapPath,
                color = if (isLightOn) tint.socket else NightSocket
            )

            // Main Wire from Ceiling (180, 10) to Socket (180, 88)
            drawLine(
                color = if (isLightOn) tint.wire else NightWire,
                start = Offset(centerX, 10f),
                end = Offset(centerX, 88f),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )

            // 2. Elastic Pull Cord & Beads
            val cordStartX = centerX + 42f
            val cordStartY = centerY + 38f
            val restLen = 88f
            val cordEndX = cordStartX + pullX
            val cordEndY = cordStartY + restLen + pullY
            val ctrlX = cordStartX + (pullX * 0.35f)
            val ctrlY = cordStartY + (restLen + pullY) * 0.52f

            val cordPath = Path().apply {
                moveTo(cordStartX, cordStartY)
                quadraticTo(ctrlX, ctrlY, cordEndX, cordEndY)
            }
            drawPath(
                path = cordPath,
                color = if (isLightOn) tint.wire else NightCord,
                style = Stroke(width = 2.8f, cap = StrokeCap.Round)
            )

            // Quadratic Bezier Bead position math
            fun getBeadPos(t: Float): Offset {
                val inv = 1f - t
                val bx = inv * inv * cordStartX + 2f * inv * t * ctrlX + t * t * cordEndX
                val by = inv * inv * cordStartY + 2f * inv * t * ctrlY + t * t * cordEndY
                return Offset(bx, by)
            }

            val bead1Pos = getBeadPos(0.32f)
            val bead2Pos = getBeadPos(0.66f)
            val beadColor = if (isLightOn) tint.socket else NightBead
            drawCircle(beadColor, radius = 3.2f, center = bead1Pos)
            drawCircle(beadColor, radius = 3.2f, center = bead2Pos)

            // Pull Progress Ring & Star Charm
            val charmCenter = Offset(cordEndX, cordEndY + 6f)
            val pullProgress = (pullY / 46f).coerceIn(0f, 1f)
            starCharmProgress.value = pullProgress

            drawStarCharm(
                center = charmCenter,
                rotation = -pullX * 0.65f,
                pullProgress = pullProgress,
                morphShape = starCharmMorphShape,
                glow = !isLightOn,
            )

            // 3. Floating Sparkles in Light Mode
            if (isLightOn) {
                drawSparkles(centerX = centerX, centerY = centerY)
            }

            // 4. Mochi Lamp Body (Squash & Stretch)
            val pullStretch = (pullY.coerceAtLeast(0f)) * 0.0022f
            val scaleY = 1f + pullStretch - squish
            val scaleX = 1f - (pullStretch * 0.55f) + (squish * 0.85f)
            val offsetY = (pullY.coerceAtLeast(0f) * 0.16f) + (squish * 14f)

            drawLampBody(
                centerX = centerX,
                centerY = centerY + offsetY,
                scaleX = scaleX,
                scaleY = scaleY,
                isLightOn = isLightOn,
                tint = tint,
                face = face,
                blinkScaleY = blinkScaleY,
                pupilOffsetX = pupilOffsetX,
                pupilOffsetY = pupilOffsetY,
                zzzTime = zzzTime,
            )
        }
    }
}

private fun DrawScope.drawLampBody(
    centerX: Float,
    centerY: Float,
    scaleX: Float,
    scaleY: Float,
    isLightOn: Boolean,
    tint: LampTint,
    face: LampFace,
    blinkScaleY: Float,
    pupilOffsetX: Float,
    pupilOffsetY: Float,
    zzzTime: Float,
) {
    scale(scaleX = scaleX, scaleY = scaleY, pivot = Offset(centerX, centerY)) {
        // Bulb Halo Glow in Light Mode
        if (isLightOn) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        GlowYellow.copy(alpha = 0.80f),
                        HaloFadeYellow.copy(alpha = 0f)
                    )
                ),
                radius = 44f,
                center = Offset(centerX, centerY + 52f)
            )
        }

        // Chubby Rounded Bulb Peeking Underneath
        val bulbPath = Path().apply {
            moveTo(centerX - 20f, centerY + 36f)
            cubicTo(
                centerX - 20f, centerY + 58f,
                centerX - 11f, centerY + 66f,
                centerX, centerY + 66f
            )
            cubicTo(
                centerX + 11f, centerY + 66f,
                centerX + 20f, centerY + 58f,
                centerX + 20f, centerY + 36f
            )
            close()
        }
        drawPath(
            path = bulbPath,
            color = if (isLightOn) GlowYellow else NightBulb
        )

        // Bulb Shine
        val bulbShine = Path().apply {
            moveTo(centerX - 12f, centerY + 46f)
            cubicTo(
                centerX - 12f, centerY + 52f,
                centerX - 9f, centerY + 56f,
                centerX - 6f, centerY + 57f
            )
        }
        drawPath(
            path = bulbShine,
            color = Color.White.copy(alpha = 0.5f),
            style = Stroke(width = 2.4f, cap = StrokeCap.Round)
        )

        val shadeBrush = if (isLightOn) {
            Brush.verticalGradient(listOf(tint.shadeTop, tint.shadeBottom))
        } else {
            Brush.verticalGradient(listOf(NightShadeTop, NightShadeBottom))
        }

        // Top Cute Mochi Ears
        drawCircle(shadeBrush, radius = 15f, center = Offset(centerX - 44f, centerY - 44f))
        drawCircle(
            color = if (isLightOn) tint.earInner else NightEarInner,
            radius = 7.5f,
            center = Offset(centerX - 44f, centerY - 43f)
        )

        drawCircle(shadeBrush, radius = 15f, center = Offset(centerX + 44f, centerY - 44f))
        drawCircle(
            color = if (isLightOn) tint.earInner else NightEarInner,
            radius = 7.5f,
            center = Offset(centerX + 44f, centerY - 43f)
        )

        // Tiny Round Socket Cap
        drawRoundRect(
            color = if (isLightOn) tint.socket else NightSocket,
            topLeft = Offset(centerX - 11f, centerY - 60f),
            size = Size(22f, 12f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )

        // Main Plump Mochi Lampshade Path
        val shadePath = Path().apply {
            moveTo(centerX, centerY - 54f)
            cubicTo(
                centerX - 48f, centerY - 54f,
                centerX - 76f, centerY - 22f,
                centerX - 78f, centerY + 22f
            )
            cubicTo(
                centerX - 78.5f, centerY + 34f,
                centerX - 70f, centerY + 43f,
                centerX - 58f, centerY + 43f
            )
            lineTo(centerX + 58f, centerY + 43f)
            cubicTo(
                centerX + 70f, centerY + 43f,
                centerX + 78.5f, centerY + 34f,
                centerX + 78f, centerY + 22f
            )
            cubicTo(
                centerX + 76f, centerY - 22f,
                centerX + 48f, centerY - 54f,
                centerX, centerY - 54f
            )
            close()
        }
        drawPath(path = shadePath, brush = shadeBrush)

        // Soft Pastel Rim Shadow Underneath
        drawOval(
            color = if (isLightOn) DayRimShadow.copy(alpha = 0.14f) else NightRimShadow.copy(alpha = 0.32f),
            topLeft = Offset(centerX - 70f, centerY + 36.5f),
            size = Size(140f, 9f)
        )

        // Cute Glossy Reflection on Shade
        val shinePath = Path().apply {
            moveTo(centerX - 56f, centerY - 6f)
            cubicTo(
                centerX - 52f, centerY - 26f,
                centerX - 38f, centerY - 39f,
                centerX - 22f, centerY - 43f
            )
        }
        drawPath(
            path = shinePath,
            color = Color.White.copy(alpha = 0.28f),
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        // Expressive Kawaii Face
        drawLampFace(
            centerX = centerX,
            centerY = centerY,
            face = face,
            isLightOn = isLightOn,
            blinkScaleY = blinkScaleY,
            pupilOffsetX = pupilOffsetX,
            pupilOffsetY = pupilOffsetY,
            zzzTime = zzzTime,
        )
    }
}

// Plain (non-Compose-State) mutable holder for the live morph progress: MorphPolygonShape's
// progress lambda is invoked from inside createOutline during the draw phase on every frame, so
// a regular var avoids any snapshot-system read/write overhead that a MutableState would add here.
private class MorphProgressHolder(var value: Float = 0f)

// Calm/puffed star endpoints for the charm's PolygonShape.star + MorphPolygonShape pull animation.
// PolygonShape caches only ONE resolved size per instance (see resolveBasePolygon's single-entry
// cache) — these two are used exclusively at the charm's fixed 29x29 logical size, every frame
// during drag, so that cache slot stays warm. Do NOT reuse them anywhere else at a different size
// (e.g. the night glow halo, which has its own dedicated StarGlowShape below for exactly this
// reason) — sharing one instance across two different sizes thrashes its cache on every frame and
// silently defeats MorphPolygonShape's own caching too, which is what made dragging feel sluggish.
private val StarCharmCalmShape = PolygonShape.star(
    numPoints = 5,
    innerRadiusRatio = 0.45f,
    outerRounding = CornerRounding.fraction(0.22f),
    innerRounding = CornerRounding.fraction(0.5f),
)
private val StarCharmPuffedShape = PolygonShape.star(
    numPoints = 5,
    innerRadiusRatio = 0.62f,
    outerRounding = CornerRounding.fraction(0.4f),
    innerRounding = CornerRounding.fraction(0.7f),
)

private fun DrawScope.drawStarCharm(
    center: Offset,
    rotation: Float,
    pullProgress: Float,
    morphShape: Shape,
    glow: Boolean,
) {
    if (glow) {
        drawCircle(
            color = StarYellow.copy(alpha = 0.35f),
            radius = 20f,
            center = center
        )
    }

    // Pull Progress Ring
    if (pullProgress > 0.05f) {
        drawArc(
            color = AmberAccent.copy(alpha = 0.85f),
            startAngle = -90f,
            sweepAngle = 360f * pullProgress,
            useCenter = false,
            topLeft = Offset(center.x - 21f, center.y - 21f),
            size = Size(42f, 42f),
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )
    }

    // Chubby Star Shape — morphs from calm to puffed as the cord is pulled. morphShape is
    // hoisted via remember in LampCanvas (see MorphProgressHolder) so its internal Morph cache
    // survives across frames instead of being rebuilt from scratch on every draw.
    rotate(rotation, pivot = center) {
        val starSize = 29f
        val starOutline = morphShape.createOutline(Size(starSize, starSize), LayoutDirection.Ltr, this)
        translate(left = center.x - starSize / 2f, top = center.y - starSize / 2f) {
            drawOutline(starOutline, GlowYellow)
            drawOutline(starOutline, AmberDeep, style = Stroke(width = 1.8f, join = StrokeJoin.Round))
        }
        drawCircle(Color.White.copy(alpha = 0.85f), radius = 2f, center = Offset(center.x - 3.2f, center.y - 3.5f))
    }
}

private fun DrawScope.drawSparkles(centerX: Float, centerY: Float) {
    // 4 tiny sparkles floating around lamp
    val s1 = Path().apply {
        moveTo(centerX - 96f, centerY - 12f)
        lineTo(centerX - 93.5f, centerY - 6f)
        lineTo(centerX - 87.5f, centerY - 3.5f)
        lineTo(centerX - 93.5f, centerY)
        lineTo(centerX - 96f, centerY + 6f)
        lineTo(centerX - 98.5f, centerY)
        lineTo(centerX - 104.5f, centerY - 3.5f)
        lineTo(centerX - 98.5f, centerY - 6f)
        close()
    }
    drawPath(s1, AmberAccent)

    val s2 = Path().apply {
        moveTo(centerX + 100f, centerY - 24f)
        lineTo(centerX + 102f, centerY - 19f)
        lineTo(centerX + 107f, centerY - 17f)
        lineTo(centerX + 102f, centerY - 15f)
        lineTo(centerX + 100f, centerY - 10f)
        lineTo(centerX + 98f, centerY - 15f)
        lineTo(centerX + 93f, centerY - 17f)
        lineTo(centerX + 98f, centerY - 19f)
        close()
    }
    drawPath(s2, AmberDeep)

    drawCircle(StarYellow, radius = 3f, center = Offset(centerX - 84f, centerY + 46f))
    drawCircle(AmberPale, radius = 2.6f, center = Offset(centerX + 92f, centerY + 38f))
}
