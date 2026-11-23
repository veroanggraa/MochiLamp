package com.veroanggra.mochilamp.model

import androidx.compose.ui.graphics.Color

data class FloatingHeart(
    val id: Long,
    val startX: Float,
    val startY: Float,
    val driftX: Float,
    val color: Color,
    val progress: Float = 0f,
)
