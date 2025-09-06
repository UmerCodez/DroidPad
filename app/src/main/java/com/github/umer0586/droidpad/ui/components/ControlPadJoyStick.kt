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

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.data.JoyStickProperties
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

@Composable
fun ControlPadJoyStick(
    modifier: Modifier = Modifier,
    offset: Offset = Offset.Zero,
    rotation: Float = 0f,
    scale: Float = 1f,
    properties: JoyStickProperties = JoyStickProperties(),
    enabled: Boolean = true,
    transformableState: TransformableState? = null,
    showControls: Boolean = true,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onMove: ((Float, Float) -> Unit)? = null

    ) {

    ControlPadItemBase(
        modifier = modifier,
        offset = offset,
        rotation = rotation,
        scale = scale,
        transformableState = transformableState,
        showControls = showControls,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick
    ){
        Joystick(
            modifier = Modifier.size(150.dp),
            enable = enabled,
            backgroundColor = Color(properties.backgroundColor),
            handleColor = Color(properties.handleColor),
            handleRadiusFactor = properties.handleRadiusFactor,
            showCoordinates = properties.showCoordinates,
            showValues = properties.showValues,
            onMove = { x, y ->
                onMove?.invoke(x, y)
            }
        )
    }
}






@Preview(showBackground = true)
@Composable
private fun ControlPadJoyStickPreview() {
    DroidPadTheme {
        ControlPadJoyStick(
            offset = Offset.Zero,
            rotation = 0f,
            scale = 1f,
            showControls = true
        )
    }
}


