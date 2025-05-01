/*
 *     This file is a part of DroidPad (https://www.github.com/umer0586/DroidPad)
 *     Copyright (C) 2025 Umer Farooq (umerfarooq2383@gmail.com)
 *
 *     DroidPad is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     DroidPad is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with DroidPad. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.github.umer0586.droidpad.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.R
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import java.lang.Math.toDegrees
import kotlin.math.atan2


@Composable
fun SteeringWheel(
    modifier: Modifier = Modifier,
    drawableId: Int = R.drawable.ic_steering_wheel,
    color: Color = Color.Red,
    onRotate: ((Float) -> Unit)? = null,
    freeRotation: Boolean = false,
    maxAngle: Int = 360,
    selfCentering: Boolean = false,
    selfCenteringDuration: Int = 300 // Duration in milliseconds for self-centering animation
) {
    var rotation by remember { mutableFloatStateOf(0f) }
    var shouldRotateToZero by remember { mutableStateOf(false) }
    var center by remember { mutableStateOf<Offset?>(null) }
    var fingerOnWheel by remember { mutableStateOf(false) }

    // Animated rotation value
    val animatedRotation by animateFloatAsState(
        targetValue = if (shouldRotateToZero) 0f else rotation,
        animationSpec = tween(
            durationMillis = if(fingerOnWheel) 0 else selfCenteringDuration, // don't apply animation if user is rotating
            easing = if(fingerOnWheel) FastOutSlowInEasing else LinearOutSlowInEasing
        ),
        label = "rotationAnimation",
        finishedListener = {
            if (shouldRotateToZero) {
                rotation = 0f
                shouldRotateToZero = false
            }
        }
    )

    // Report the current rotation value to the caller
    LaunchedEffect(animatedRotation) {
        onRotate?.invoke(animatedRotation)
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                var previousAngle: Float? = null

                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val changes = event.changes
                        if (changes.isEmpty()) continue

                        val pointer = changes[0]
                        if (pointer.pressed && pointer.positionChanged()) {
                            shouldRotateToZero = false // Cancel any ongoing centering while user is interacting
                            val centerPos = center ?: continue
                            val touchPos = pointer.position

                            val angle = angleBetween(centerPos, touchPos)

                            previousAngle?.let { prev ->
                                val delta = angleDelta(prev, angle)

                                if (freeRotation) {
                                    rotation += delta
                                } else {
                                    rotation = (rotation + delta).coerceIn(-maxAngle.toFloat(), maxAngle.toFloat())
                                }
                                fingerOnWheel = true
                            }

                            previousAngle = angle
                        } else if (!pointer.pressed) {
                            fingerOnWheel = false
                            previousAngle = null
                            if (selfCentering) {
                                shouldRotateToZero = true // Trigger self-centering animation
                            }
                        }
                    }
                }
            }
    ) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = "Steering Wheel",
            modifier = Modifier
                .rotate(animatedRotation)
                .size(200.dp)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (center == null && event.changes.isNotEmpty()) {
                                val bounds = this@pointerInput.size
                                center = Offset(bounds.width / 2f, bounds.height / 2f)
                            }
                        }
                    }
                },
            colorFilter = ColorFilter.tint(color)
        )
    }
}

// Calculates angle between two points
private fun angleBetween(center: Offset, touch: Offset): Float {
    val dx = touch.x - center.x
    val dy = touch.y - center.y
    return toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
}

// Normalizes delta to -180° to +180°
private fun angleDelta(start: Float, end: Float): Float {
    var delta = end - start
    while (delta > 180f) delta -= 360f
    while (delta < -180f) delta += 360f
    return delta
}

@Preview(showBackground = true)
@Composable
fun SteeringWheelPreview(modifier: Modifier = Modifier) {

    var rotation by remember { mutableFloatStateOf(0f) }

    DroidPadTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
        ){
            Text(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 100.dp),
                text = rotation.toString(),
                style = MaterialTheme.typography.headlineLarge
            )

            SteeringWheel(
                modifier = modifier.size(200.dp).align(Alignment.Center),
                color = Color.Magenta,
                freeRotation = true,
                maxAngle = 90,
                selfCentering = true,
                onRotate = {
                    rotation = it
                }
            )
        }
    }
}