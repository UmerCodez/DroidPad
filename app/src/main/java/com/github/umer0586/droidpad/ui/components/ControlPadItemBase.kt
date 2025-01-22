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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun ControlPadItemBase(
    modifier: Modifier = Modifier,
    offset: Offset,
    rotation: Float,
    scale: Float,
    transformableState: TransformableState? = null,
    showControls: Boolean = true,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    content: @Composable() (() -> Unit)
) {
    val handleSize = 15.dp

    Box(
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .then(
                if (transformableState != null)
                    Modifier.transformable(state = transformableState)
                else
                    Modifier
            )
            .then(
                if (showControls) Modifier.border(
                    width = 1.dp,
                    color = Color.LightGray
                ) else Modifier
            ),
        contentAlignment = Alignment.Center

    ) {


        content()

        if (showControls) {
            Icon(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-15).dp)
                    .size(handleSize)
                    .clickable {
                        onEditClick?.invoke()
                    },
                imageVector = Icons.Filled.Edit,
                contentDescription = "editHandle",
            )

            Icon(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-15).dp)
                    .size(handleSize)
                    .clickable {
                        onDeleteClick?.invoke()
                    },
                imageVector = Icons.Filled.Delete,
                contentDescription = "deleteHandle",
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun TransformableSample() {
    // set up all transformation states
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange.rotateBy(rotation) * scale
    }
    Box(
        Modifier.fillMaxSize()
    ) {
        ControlPadItemBase(
            modifier = Modifier.background(Color.Green),
            offset = offset,
            rotation = rotation,
            scale = scale,
            transformableState = state,
            showControls = false,

            ) {
            var sliderValue by remember { mutableFloatStateOf(1f) }
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 0.5f..2f
            )
        }
    }
}


// Credits : https://stackoverflow.com/a/78732066/9193164
/**
 * Rotates the given offset around the origin by the given angle in degrees.
 *
 * A positive angle indicates a counterclockwise rotation around the right-handed 2D Cartesian
 * coordinate system.
 *
 * See: [Rotation matrix](https://en.wikipedia.org/wiki/Rotation_matrix)
 */
fun Offset.rotateBy(
    angle: Float
): Offset {
    val angleInRadians = ROTATION_CONST * angle
    val newX = x * cos(angleInRadians) - y * sin(angleInRadians)
    val newY = x * sin(angleInRadians) + y * cos(angleInRadians)
    return Offset(newX, newY)
}

internal const val ROTATION_CONST = (Math.PI / 180f).toFloat()