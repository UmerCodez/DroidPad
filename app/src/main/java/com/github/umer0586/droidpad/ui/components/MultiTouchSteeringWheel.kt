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

import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.umer0586.droidpad.R
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

@Composable
fun MultiTouchSteeringWheel(
    modifier: Modifier = Modifier,
    drawableId: Int = R.drawable.ic_steering_wheel,
    color: Color = Color.Red,
    onRotate: ((Float) -> Unit)? = null,
    freeRotation: Boolean = false,
    maxAngle: Int = 360,
    selfCentering: Boolean = false,
    selfCenteringDuration: Int = 300
) {
    var rotation by remember { mutableFloatStateOf(0f) }
    var shouldRotateToZero by remember { mutableStateOf(false) }
    var fingersLifted by remember { mutableStateOf(true) }
    var activePointers by remember { mutableIntStateOf(0) }


    val animatedRotation by animateFloatAsState(
        targetValue = if (shouldRotateToZero) 0f else rotation,
        animationSpec = if(selfCentering && fingersLifted) tween(
            durationMillis = selfCenteringDuration,
            easing = LinearOutSlowInEasing
        ) else snap(),
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
        modifier = modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    val currentPointers = event.changes.count { it.pressed }

                    // Update active pointers count
                    if (currentPointers != activePointers) {
                        activePointers = currentPointers
                        fingersLifted = currentPointers > 0

                        // When all fingers are lifted
                        if (currentPointers == 0) {
                            // Handle finger lift here
                            Log.d("SteeringWheel", "All fingers lifted")
                            fingersLifted = true
                            if(selfCentering){
                                shouldRotateToZero = true
                            }
                        }
                    }
                }
            }
        }
    ) {
        // For rotation gestures
        Image(
            modifier = modifier
                .rotate(animatedRotation)
                .pointerInput(Unit) {
                    detectTransformGestures(panZoomLock = true) { _, _, _, rotationChange ->
                        shouldRotateToZero = false
                        if (freeRotation) {
                            rotation += rotationChange
                        } else {
                            rotation = (rotation + rotationChange).coerceIn(
                                -maxAngle.toFloat(),
                                maxAngle.toFloat()
                            )
                        }
                    }
                },
            painter = painterResource(id = drawableId),
            contentDescription = "Steering Wheel",
            colorFilter = ColorFilter.tint(color)
        )
    }



}

@Preview(showBackground = true)
@Composable
private fun MultiTouchSteeringWheelPreview() {

    var rotation by remember { mutableFloatStateOf(0f) }

    DroidPadTheme {
        Box(Modifier.fillMaxSize()) {

            Text(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 100.dp),
                text = rotation.toString(),
                fontSize = 20.sp
            )

            MultiTouchSteeringWheel(
                modifier = Modifier.size(200.dp).align(Alignment.Center),
                selfCentering = true,
                freeRotation = false,
                maxAngle = 90,
                onRotate = {
                    rotation = it
                }
            )
        }
    }

}