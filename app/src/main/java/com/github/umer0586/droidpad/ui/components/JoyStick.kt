/*
 *     This file is a part of DroidPad (https://www.github.com/UmerCodez/DroidPad)
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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    enable: Boolean = false,
    showCoordinates: Boolean = false,
    showValues: Boolean = false,
    backgroundColor: Color = Color.LightGray,
    handleColor: Color = Color.Blue,
    handleRadiusFactor: Float = 0.4f, // Ratio of handle radius to joystick radius, min:0.4 max:0.9
    onMove: (Float, Float) -> Unit
) {

    var handlePosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var isDraggingHandle by remember { mutableStateOf(false) } // Track if the handle is being dragged
    var normalizedX by remember { mutableFloatStateOf(0f) }
    var normalizedY by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(modifier = modifier) {
        val size = min(maxWidth, maxHeight) // Dp value for joystick size

        Canvas(
            modifier = Modifier
                .size(size) // Use size as Dp
                .pointerInput(enable) { // Reinitialize interaction based on `disable`
                    if (enable) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val canvasCenter = Offset(size.toPx() / 2, size.toPx() / 2)

                                // Distance from touch point to joystick handle
                                val distanceToHandle =
                                    (offset - (canvasCenter + handlePosition)).getDistance()

                                // Start dragging only if within the handle radius
                                val handleRadius = (size.toPx() / 2) * handleRadiusFactor
                                isDraggingHandle = distanceToHandle <= handleRadius
                            },
                            onDrag = { change, dragAmount ->
                                if (isDraggingHandle) {
                                    // Calculate new handle position
                                    val joystickRadius = size.toPx() / 2
                                    val newOffset =
                                        handlePosition + Offset(dragAmount.x, dragAmount.y)

                                    // Clamp the handle within the circular boundary
                                    val distance = sqrt(newOffset.x.pow(2) + newOffset.y.pow(2))
                                    handlePosition = if (distance <= joystickRadius) {
                                        newOffset
                                    } else {
                                        // Scale to the boundary of the circle
                                        val scale = joystickRadius / distance
                                        Offset(newOffset.x * scale, newOffset.y * scale)
                                    }

                                    // Normalize to range [-1, 1]
                                    // The joystick's normalized value gets very close to 1 or -1 (e.g., 0.9999324) but never reaches exactly 1 due to floating-point precision.
                                    // TODO: To handle this, set a threshold: if the value is greater than or equal to 0.9999, treat it as 1 (or -1).
                                    normalizedX = handlePosition.x / joystickRadius
                                    normalizedY = -handlePosition.y / joystickRadius

                                    onMove(normalizedX, normalizedY)

                                    // Consume the gesture
                                    change.consume()
                                }
                            },
                            onDragEnd = {
                                // Snap handle back to center

                                handlePosition = Offset(0f, 0f)
                                normalizedX = 0f
                                normalizedY = 0f

                                onMove(0f, 0f)

                                isDraggingHandle = false
                            }
                        )
                    }
                }
        ) {
            val joystickRadius = size.toPx() / 2
            val canvasCenter = Offset(joystickRadius, joystickRadius)
            val handleRadius = joystickRadius * handleRadiusFactor

            // Draw the joystick area
            drawCircle(
                color = backgroundColor,
                radius = joystickRadius,
                center = canvasCenter
            )


            if(showCoordinates) {

                // Coordinate lines (X and Y axes) between joystick area and handle
                drawLine(
                    color = backgroundColor.contrastColor(),
                    strokeWidth = 2f,
                    start = Offset(0f, canvasCenter.y),
                    end = Offset(size.toPx(), canvasCenter.y)
                )
                drawLine(
                    color = backgroundColor.contrastColor(),
                    strokeWidth = 2f,
                    start = Offset(canvasCenter.x, 0f),
                    end = Offset(canvasCenter.x, size.toPx())
                )

                // A small dot
                drawCircle(
                    color = backgroundColor.contrastColor(),
                    radius = 15f,
                    center = canvasCenter + handlePosition
                )
            }

            // Draw the joystick handle
            drawCircle(
                color = handleColor.copy(alpha = if(showCoordinates) 0.3f else 1f),
                radius = handleRadius,
                center = canvasCenter + handlePosition
            )
        }

        // Box scope
        if(showValues){

            Text(
                modifier = Modifier.align( if(normalizedY >= 0) Alignment.TopCenter else Alignment.BottomCenter),
                text = normalizedY.toString(),
                color = backgroundColor.contrastColor(),
                fontSize = 8.sp
            )
            Text(
                modifier = Modifier.align(if(normalizedX >= 0) Alignment.CenterEnd else Alignment.CenterStart),
                text = normalizedX.toString(),
                color = backgroundColor.contrastColor(),
                fontSize = 8.sp
            )
        }

    }
}

private fun Color.contrastColor(): Color {
    // Convert to luminance using the sRGB luminance formula
    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue)
    return if (luminance > 0.5) Color.Black else Color.White
}


@Preview(showBackground = true)
@Composable
private fun JoyStickPreview(modifier: Modifier = Modifier) {
    DroidPadTheme {
        Box{
            Joystick(
                backgroundColor = Color.Green,
                showValues = true,
                showCoordinates = true,
                enable = true,
                onMove = { x, y ->

                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun JoyStickInteractivePreview(modifier: Modifier = Modifier) {
    DroidPadTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            var cords by remember {
                mutableStateOf("(0,0)")
            }

            Text(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp),
                text = cords,
                style = MaterialTheme.typography.headlineMedium
            )

            Joystick(
                modifier = modifier.size(250.dp),
                showCoordinates = true,
                showValues = true,
                enable = true,
                backgroundColor = Color.Yellow,
                handleRadiusFactor = 0.5f,
                onMove = { x, y ->
                    cords = "($x,$y)"
                }
            )
        }
    }
}
