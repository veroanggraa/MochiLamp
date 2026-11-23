package com.veroanggra.mochilamp.ui.lamp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veroanggra.mochilamp.R
import com.veroanggra.mochilamp.ui.theme.ChromeSurfaceDark
import com.veroanggra.mochilamp.ui.theme.ChromeSurfaceLight
import com.veroanggra.mochilamp.ui.theme.HeaderTextDay
import com.veroanggra.mochilamp.ui.theme.LavenderMist

@Composable
fun LampHeader(
    isLightOn: Boolean,
    soundEnabled: Boolean,
    onSoundToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status Pill
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isLightOn) ChromeSurfaceLight.copy(alpha = 0.88f) else ChromeSurfaceLight.copy(alpha = 0.14f))
                .border(
                    width = 1.dp,
                    color = if (isLightOn) ChromeSurfaceDark.copy(alpha = 0.08f) else ChromeSurfaceLight.copy(alpha = 0.18f),
                    shape = CircleShape
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (isLightOn) "☀️ Day" else "🌙 Night",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isLightOn) HeaderTextDay else LavenderMist
            )
        }

        // Sound Toggle Button
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isLightOn) ChromeSurfaceLight.copy(alpha = 0.88f) else ChromeSurfaceLight.copy(alpha = 0.14f))
                .border(
                    width = 1.dp,
                    color = if (isLightOn) ChromeSurfaceDark.copy(alpha = 0.08f) else ChromeSurfaceLight.copy(alpha = 0.18f),
                    shape = CircleShape,
                )
                .clickable { onSoundToggle() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.sound),
                contentDescription = if (soundEnabled) "Sound on" else "Sound off",
                modifier = Modifier
                    .size(20.dp)
                    .alpha(if (soundEnabled) 1f else 0.35f)
            )
        }
    }
}
