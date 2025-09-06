package com.github.umer0586.droidpad.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun LED(
    color: Color,
    state: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp   // renamed this
) {
    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f   // now 'this.size' refers to Canvas size
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        if (state) {
            // Outer halo glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.6f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 2f
                ),
                radius = radius * 2f,
                center = center
            )
        }

        // Main LED circle
        val brush = if (state) {
            Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    color.copy(alpha = 0.8f),
                    color.copy(alpha = 0.4f)
                ),
                center = center,
                radius = radius
            )
        } else {
            Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.9f),
                    color.copy(alpha = 0.4f),
                    Color.Black.copy(alpha = 0.6f)
                ),
                center = center,
                radius = radius
            )
        }

        drawCircle(brush = brush, radius = radius, center = center)
    }
}

@Preview
@Composable
private fun LEDPreview() {
    LED(
        color = Color.Green,
        state = true,
        size = 40.dp
    )
}

@Preview
@Composable
private fun LEDBlinkPreview() {
    var isOnState by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Blink every second
            isOnState = !isOnState
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LED(
            color = Color.Green,
            state = isOnState,
            size = 150.dp
        )
    }
}
