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


import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.R
import com.github.umer0586.droidpad.data.SteeringWheelProperties
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

@Composable
fun ControlPadSteeringWheel(
    modifier: Modifier = Modifier,
    offset: Offset = Offset.Zero,
    rotation: Float = 0f,
    scale: Float = 1f,
    enabled: Boolean = true,
    properties: SteeringWheelProperties = SteeringWheelProperties(),
    transformableState: TransformableState? = null,
    showControls: Boolean = true,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onRotate: ((Float) -> Unit)? = null
) {
    ControlPadItemBase(
        modifier = modifier,
        offset = offset,
        rotation = rotation,
        scale = scale,
        transformableState = transformableState,
        showControls = showControls,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
    ) {
        val wheelSize = 200.dp
        if(enabled) {
            if (!properties.multiTouch) {
                SteeringWheel(
                    modifier = Modifier.size(wheelSize),
                    color = Color(properties.color),
                    freeRotation = properties.freeRotation,
                    maxAngle = properties.maxAngle,
                    selfCentering = properties.selfCentering,
                    onRotate = onRotate
                )
            } else {
                MultiTouchSteeringWheel(
                    modifier = Modifier.size(wheelSize),
                    color = Color(properties.color),
                    freeRotation = properties.freeRotation,
                    maxAngle = properties.maxAngle,
                    selfCentering = properties.selfCentering,
                    onRotate = onRotate
                )
            }
        }else {
            Image(
                painter = painterResource(id = R.drawable.ic_steering_wheel),
                contentDescription = "Steering Wheel",
                modifier = Modifier
                    .size(wheelSize)
                    .pointerInput(Unit) {
                        // Consume no gestures; make this view transparent to touch
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent()
                            }
                        }
                    },
                colorFilter = ColorFilter.tint(Color(properties.color))
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControlPadSteeringWheelPreview(modifier: Modifier = Modifier) {

    DroidPadTheme {
        Box(Modifier.fillMaxSize()){
            ControlPadSteeringWheel(
                modifier = Modifier.align(Alignment.Center),
                enabled = false,
                properties = SteeringWheelProperties(
                    multiTouch = true,
                )
            )
        }
    }
}


