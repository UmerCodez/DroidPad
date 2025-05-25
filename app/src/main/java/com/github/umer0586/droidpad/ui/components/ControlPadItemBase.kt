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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun ControlPadItemBase(
    modifier: Modifier = Modifier,
    offset: Offset,
    rotation: Float,
    scale: Float,
    transformableState: TransformableState? = null,
    showActionExpander: Boolean = true,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    content: @Composable() (() -> Unit)
) {
    val handleSize = 20.dp

    var showActions by remember { mutableStateOf(false) }

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
                if (transformableState != null) Modifier.transformable(state = transformableState)
                else Modifier
            )
            .then(
                if (showActionExpander) Modifier.border(
                    width = 1.dp, color = Color.LightGray
                ) else Modifier
            ),
        contentAlignment = Alignment.Center

    ) {


        content()


        AnimatedVisibility(
            visible = showActions,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-40).dp)
        ) {
            Actions(
                modifier = Modifier
                    .rotate(-rotation),
                onActionClick = {
                    when (it) {
                        ActionItem.EDIT -> onEditClick?.invoke()
                        ActionItem.DELETE -> onDeleteClick?.invoke()
                    }
                },
                onCloseClick = { showActions = false }
            )
        }

        if(showActionExpander && !showActions) {
            IconButton(
                modifier = Modifier
                    .size(handleSize)
                    .align(Alignment.TopCenter)
                    .offset(y = (-25).dp),
                onClick = { showActions = true },
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null,
                )
            }
        }

    }
}

private enum class ActionItem{
    EDIT,DELETE
}

@Composable
private fun Actions(
    modifier: Modifier = Modifier,
    iconButtonColors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    onActionClick: ((ActionItem) -> Unit)? = null,
    onCloseClick: (() -> Unit)? = null
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(Modifier
            .clip(CircleShape)
            .background(backgroundColor)) {
            ActionItem.entries.forEach { item ->
                when (item) {
                    ActionItem.EDIT -> {
                        IconButton(onClick = { onActionClick?.invoke(ActionItem.EDIT) }, colors = iconButtonColors) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        }
                    }

                    ActionItem.DELETE -> {
                        IconButton(onClick = { onActionClick?.invoke(ActionItem.DELETE) }, colors = iconButtonColors) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-30).dp, x = (25).dp),
            colors = iconButtonColors,
            onClick = { onCloseClick?.invoke() }) {
            Icon(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                imageVector = Icons.Filled.Close, contentDescription = null
            )
        }
    }
}

// Preview for the Tools composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF) // Added background color for better visibility
@Composable
private fun ActionsPreview() {
    DroidPadTheme  {
        Actions(
            modifier = Modifier
                .padding(16.dp), // Add some padding for better visual spacing in preview

        )
    }
}


@Preview(showBackground = true)
@Composable
private fun TransformableSample() {
    // set up all transformation states
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(45f) }
    var offset by remember { mutableStateOf(Offset(120f,150f)) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange.rotateBy(rotation) * scale
    }
    Box(
        Modifier.fillMaxSize()
    ) {
        ControlPadItemBase(
            offset = offset,
            rotation = rotation,
            scale = scale,
            transformableState = state,
            showActionExpander = true,

            ) {
            var sliderValue by remember { mutableFloatStateOf(1f) }
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 0.5f..2f,
                enabled = false
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