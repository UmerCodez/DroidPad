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

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.github.umer0586.droidpad.data.DpadProperties
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme


@Composable
fun ControlPadDpad(
    modifier: Modifier = Modifier,
    offset: Offset = Offset.Zero,
    rotation: Float = 0f,
    scale: Float = 1f,
    properties: DpadProperties = DpadProperties(),
    enabled: Boolean = true,
    transformableState: TransformableState? = null,
    showControls: Boolean = true,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onClick: ((DPAD_BUTTON) -> Unit)? = null,
    onPressed: ((DPAD_BUTTON) -> Unit)? = null,
    onRelease: ((DPAD_BUTTON) -> Unit)? = null,
){

    ControlPadItemBase(
        modifier = modifier,
        offset = offset,
        rotation = rotation,
        scale = scale,
        transformableState = transformableState,
        showControls = showControls,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick
    ) {

        Dpad(
            backgroundColor = Color(properties.backgroundColor),
            buttonColor = Color(properties.buttonColor),
            useClickAction = properties.useClickAction,
            style = properties.style,
            enabled = enabled,
            onClick = onClick,
            onPressed = onPressed,
            onRelease = onRelease
        )


    }

}


@Preview
@Composable
fun ControlPadDpadPreview(modifier: Modifier = Modifier) {
    DroidPadTheme {
        ControlPadDpad(
            modifier = modifier,
            offset = Offset.Zero,
            rotation = 0f,
            scale = 1f,
            properties = DpadProperties(
                backgroundColor = MaterialTheme.colorScheme.primary.value,
                buttonColor = MaterialTheme.colorScheme.onPrimary.value,
                useClickAction = true
            )
        )
    }
}

