package com.veroanggra.mochilamp.ui.lamp.components

import androidx.compose.animation.CapturedAnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.veroanggra.mochilamp.model.LampTint
import com.veroanggra.mochilamp.ui.theme.ChromeSurfaceDark
import com.veroanggra.mochilamp.ui.theme.ChromeSurfaceLight

@Composable
fun TintSelector(
    visible: Boolean,
    selectedTint: LampTint,
    onTintSelect: (LampTint) -> Unit,
    modifier: Modifier = Modifier,
) {
    CapturedAnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut(),
        modifier = modifier,
        label = "tintSelectorVisibility",
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(CircleShape)
                .background(ChromeSurfaceLight.copy(alpha = 0.88f))
                .border(width = 1.dp, color = ChromeSurfaceDark.copy(alpha = 0.06f), shape = CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            LampTint.entries.forEach { tint ->
                val isSelected = selectedTint == tint
                val dotSize by animateDpAsState(
                    targetValue = if (isSelected) 28.dp else 22.dp,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                    label = "tintDotSize"
                )

                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(tint.dotColor)
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) tint.accent else Color.Transparent,
                            shape = CircleShape
                        )
                        .semantics { contentDescription = tint.title }
                        .clickable { onTintSelect(tint) }
                )
            }
        }
    }
}
