package com.veroanggra.mochilamp.ui.lamp

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.computedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veroanggra.mochilamp.audio.KawaiiSoundSynthesizer
import com.veroanggra.mochilamp.model.FloatingHeart
import com.veroanggra.mochilamp.model.LampFace
import com.veroanggra.mochilamp.model.LampTint
import com.veroanggra.mochilamp.ui.lamp.components.FloatingHeartsOverlay
import com.veroanggra.mochilamp.ui.lamp.components.LampCanvas
import com.veroanggra.mochilamp.ui.lamp.components.LampHeader
import com.veroanggra.mochilamp.ui.lamp.components.StarryNightBackground
import com.veroanggra.mochilamp.ui.lamp.components.TintSelector
import com.veroanggra.mochilamp.ui.theme.DayTextPrimary
import com.veroanggra.mochilamp.ui.theme.DaySubtitleText
import com.veroanggra.mochilamp.ui.theme.GlowYellow
import com.veroanggra.mochilamp.ui.theme.KawaiiBlush
import com.veroanggra.mochilamp.ui.theme.KawaiiLampTheme
import com.veroanggra.mochilamp.ui.theme.NightBg
import com.veroanggra.mochilamp.ui.theme.NightOuter
import com.veroanggra.mochilamp.ui.theme.SleepyBlush
import com.veroanggra.mochilamp.ui.theme.StarLavender
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Wording matches the Figma design's 5 reference screens (night-sleepy, pulling-cord, and the 3
// day-tint variants) — see https://www.figma.com/design/7sU9yhSve57MA76gQuTBT0/Compose
private enum class GreetingState { NIGHT, PULLING, DAY }

@Composable
fun KawaiiLampScreen(
    modifier: Modifier = Modifier,
    soundSynth: KawaiiSoundSynthesizer = remember { KawaiiSoundSynthesizer() },
) {
    var isLightOn by remember { mutableStateOf(false) }
    var selectedTint by remember { mutableStateOf(LampTint.WARM) }
    var isBlushing by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }
    var passedThreshold by remember { mutableStateOf(value = false) }
    var soundEnabled by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    // Eye tracking state
    var pupilTargetX by remember { mutableStateOf(0f) }
    var pupilTargetY by remember { mutableStateOf(0f) }

    // Floating hearts state
    val hearts = remember { mutableStateListOf<FloatingHeart>() }

    // Heart movement loop
    LaunchedEffect(hearts.size) {
        if (hearts.isNotEmpty()) {
            while (hearts.isNotEmpty()) {
                val toRemove = mutableListOf<FloatingHeart>()
                for (i in hearts.indices) {
                    val heart = hearts[i]
                    val newProgress = heart.progress + 0.035f
                    if (newProgress >= 1f) {
                        toRemove.add(heart)
                    } else {
                        hearts[i] = heart.copy(progress = newProgress)
                    }
                }
                hearts.removeAll(toRemove)
                delay(16)
            }
        }
    }

    // Fisika Spring Tarikan Tali
    val pullX = remember { Animatable(0f) }
    val pullY = remember { Animatable(0f) }
    val swayAngle = remember { Animatable(0f) }
    val squish = remember { Animatable(0f) }

    // Kedipan Mata Otomatis
    val blinkScaleY = remember { Animatable(1f) }

    val thresholdY = 46f
    val maxY = 82f

    // LampCanvas draws in a fixed 360-unit logical space. The Box below must stay within the
    // screen width (it gets silently clamped otherwise, which is why just bumping lampSizeDp
    // stopped making a visible difference), so extraZoom makes the bear bigger by zooming into
    // the unused margin around its silhouette instead (must match LampCanvas's own extraZoom).
    // zoomPivotX/Y pin the spot (where the wire meets the ceiling) that extraZoom scales around.
    val baseLampUnits = 360f
    val lampSizeDp = 400.dp
    val lampScale = lampSizeDp.value / baseLampUnits
    val extraZoom = 1.35f
    val zoomPivotX = 180f
    val zoomPivotY = 10f

    // Converts a point in LampCanvas's logical space to a dp offset within the lamp Box,
    // accounting for both the box-fit scale and the extra zoom, so overlays (the cord's touch
    // target, the pull hint) stay visually aligned with where the artwork actually renders.
    fun logicalToOverlayDp(px: Float, py: Float): Offset {
        val effX = zoomPivotX + extraZoom * (px - zoomPivotX)
        val effY = zoomPivotY + extraZoom * (py - zoomPivotY)
        return Offset(effX * lampScale, effY * lampScale)
    }

    // Loop otomatis kedip saat lampu menyala
    LaunchedEffect(isLightOn) {
        if (isLightOn) {
            while (true) {
                delay(Random.nextLong(2600, 5800))
                if (!isDragging && !isBlushing) {
                    blinkScaleY.animateTo(0.08f, tween(100))
                    blinkScaleY.animateTo(1f, tween(100))
                }
            }
        }
    }

    // State Mesin Wajah Kawaii — computedStateOf re-evaluates every read (e.g. each pullY
    // tick while dragging at 60fps) without derivedStateOf's caching overhead, since this
    // calculation is cheap and only needs to skip recomposition when the resulting face differs.
    val currentFace by remember {
        computedStateOf {
            when {
                isDragging && (pullY.value >= thresholdY) -> LampFace.SURPRISED
                isDragging && (pullY.value > 8f) -> LampFace.DRAGGING
                isBlushing -> LampFace.BLUSH
                isLightOn -> LampFace.HAPPY
                else -> LampFace.SLEEPY
            }
        }
    }

    val greetingState = when {
        isDragging -> GreetingState.PULLING
        isLightOn -> GreetingState.DAY
        else -> GreetingState.NIGHT
    }

    // Animasi Warna Latar Belakang Halus
    val animatedBurstBgColor by animateColorAsState(
        targetValue = if (isLightOn) selectedTint.burstBg else NightBg,
        animationSpec = tween(700),
        label = "burstBgColor",
    )
    val animatedOuterBgColor by animateColorAsState(
        targetValue = if (isLightOn) selectedTint.outerBg else NightOuter,
        animationSpec = tween(700),
        label = "outerBgColor",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (isLightOn) {
                    Brush.radialGradient(
                        colors = listOf(animatedBurstBgColor, animatedOuterBgColor)
                    )
                } else {
                    SolidColor(animatedBurstBgColor)
                }
            )
            .pointerInput(isLightOn) {
                if (isLightOn) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            val lampCenterX = size.width / 2f
                            val lampCenterY = 140f
                            val normX = ((change.position.x - lampCenterX) / 180f).coerceIn(-1f, 1f)
                            val normY = ((change.position.y - lampCenterY) / 180f).coerceIn(-1f, 1f)
                            pupilTargetX = normX * 3.2f
                            pupilTargetY = normY * 2.4f
                        }
                    )
                }
            }
    ) {
        // 1. Layer Bintang Bergerak Melayang & Berkedip (Malam)
        if (!isLightOn) {
            StarryNightBackground(modifier = Modifier.fillMaxSize())
        }

        // 2. Header
        LampHeader(
            isLightOn = isLightOn,
            soundEnabled = soundEnabled,
            onSoundToggle = {
                soundEnabled = !soundEnabled
                if (soundEnabled) soundSynth.playTintPop()
            }
        )

        // 3. Lampu Mochi & Area Tarik Tali
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(lampSizeDp)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 0.05f)
                        rotationZ = swayAngle.value
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                // Kanvas Utama Lampu
                LampCanvas(
                    isLightOn = isLightOn,
                    tint = selectedTint,
                    face = currentFace,
                    pullX = pullX.value,
                    pullY = pullY.value,
                    squish = squish.value,
                    blinkScaleY = blinkScaleY.value,
                    pupilOffsetX = pupilTargetX,
                    pupilOffsetY = pupilTargetY,
                    onLampTap = {
                        coroutineScope.launch {
                            isBlushing = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (soundEnabled) soundSynth.playBlushSound()

                            repeat(4) { index ->
                                hearts.add(
                                    FloatingHeart(
                                        id = System.nanoTime() + index,
                                        startX = 180f + ((Random.nextFloat() * 60f) - 30f),
                                        startY = 150f,
                                        driftX = (Random.nextFloat() - 0.5f) * 70f,
                                        color = if (index % 2 == 0) KawaiiBlush else SleepyBlush,
                                    )
                                )
                            }

                            // Squish Bounce Fisika
                            squish.animateTo(0.12f, tween(100))
                            squish.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))

                            delay(1150)
                            isBlushing = false
                        }
                    }
                )

                // Render floating heart particles overlay
                FloatingHeartsOverlay(hearts = hearts)

                // Handle Sensor Sentuh Tali Tarik
                val cordHandleCenter = logicalToOverlayDp(222f, 272f)
                val cordHandleSize = 80f * lampScale * extraZoom
                val cordHandleLeft = cordHandleCenter.x - cordHandleSize / 2f
                val cordHandleTop = cordHandleCenter.y - cordHandleSize / 2f
                // This Box is TopCenter-aligned within the lampSizeDp parent, so its own
                // auto-centered left edge must be subtracted back out before offsetting.
                val cordHandleBaseLeft = (lampSizeDp.value - cordHandleSize) / 2f
                Box(
                    modifier = Modifier
                        .offset(x = (cordHandleLeft - cordHandleBaseLeft).dp, y = cordHandleTop.dp)
                        .size(cordHandleSize.dp)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = {
                                    isDragging = true
                                    passedThreshold = false
                                },
                                onDragEnd = {
                                    isDragging = false
                                    val shouldToggle = passedThreshold
                                    coroutineScope.launch {
                                        launch { pullX.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) }
                                        launch { pullY.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) }
                                        launch { swayAngle.animateTo(0f, spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)) }
                                    }

                                    if (shouldToggle) {
                                        isLightOn = !isLightOn
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        if (soundEnabled) soundSynth.playSwitchSound(isLightOn)

                                        coroutineScope.launch {
                                            squish.animateTo(0.085f, tween(80))
                                            squish.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
                                        }
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    // dragAmount is reported in raw pixels; pullX/pullY are logical
                                    // units (1 unit == 1dp, matching LampCanvas's 360-unit space),
                                    // so convert to dp before accumulating.
                                    val dragAmountDp = dragAmount / density
                                    val newX = (pullX.value + dragAmountDp.x * 0.5f).coerceIn(-36f, 36f)
                                    val rawY = pullY.value + dragAmountDp.y
                                    val newY = if (rawY > thresholdY) {
                                        thresholdY + (rawY - thresholdY) * 0.32f
                                    } else {
                                        rawY
                                    }.coerceIn(-8f, maxY)

                                    coroutineScope.launch {
                                        pullX.snapTo(newX)
                                        pullY.snapTo(newY)
                                        swayAngle.snapTo(-newX * 0.22f)
                                    }

                                    if (!passedThreshold && newY >= thresholdY) {
                                        passedThreshold = true
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                }
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. Area Teks Sapaan & 3-Dot Tint Selector
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 88.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Selector 3 Varian Warna (Warm, Sakura, Sky) — fixed-height slot so it
                // appearing/disappearing between day and night never shifts the text below.
                Box(
                    modifier = Modifier.height(52.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    TintSelector(
                        visible = isLightOn,
                        selectedTint = selectedTint,
                        onTintSelect = { tint ->
                            selectedTint = tint
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (soundEnabled) soundSynth.playTintPop()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Crossfade(
                    targetState = greetingState,
                    modifier = Modifier.fillMaxWidth(),
                    animationSpec = tween(450),
                    label = "textCrossfade"
                ) { state ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = when (state) {
                                GreetingState.DAY -> "Good morning!"
                                GreetingState.PULLING -> "Pull a Little More!"
                                GreetingState.NIGHT -> "Time for a break"
                            },
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLightOn) DayTextPrimary else Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = when (state) {
                                GreetingState.DAY -> selectedTint.subtitle
                                GreetingState.PULLING -> "Pull down until the light turns on"
                                GreetingState.NIGHT -> "Shhh... the lamp is sleeping. Pull the cord, okay?"
                            },
                            fontSize = 18.sp,
                            color = when {
                                isLightOn -> DaySubtitleText
                                state == GreetingState.PULLING -> GlowYellow
                                else -> StarLavender
                            },
                            textAlign = TextAlign.Center,
                            // Pinned to exactly 2 lines so the block's total height — and
                            // therefore its position (bottom-anchored via the weighted Spacer
                            // above) — stays constant regardless of which state's subtitle
                            // happens to wrap to 1 line vs 2, instead of shifting per state.
                            minLines = 2,
                            maxLines = 2,
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Kawaii Lamp Light On", showBackground = true)
@Composable
fun KawaiiLampScreenPreview() {
    KawaiiLampTheme {
        KawaiiLampScreen()
    }
}
