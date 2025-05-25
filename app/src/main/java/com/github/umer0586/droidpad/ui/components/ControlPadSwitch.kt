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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.data.SwitchProperties
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

@Composable
fun ControlPadSwitch(
    modifier: Modifier = Modifier,
    offset: Offset,
    rotation: Float,
    scale: Float,
    properties: SwitchProperties = SwitchProperties(),
    transformableState: TransformableState? = null,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    checked: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    enabled: Boolean = true,
    showActionExpander: Boolean = true
    ){

    ControlPadItemBase(
        modifier = modifier,
        offset = offset,
        rotation = rotation,
        scale = scale,
        transformableState = transformableState,
        showActionExpander = showActionExpander,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,

    ) {
        Switch(
            modifier = Modifier.padding(10.dp),
            enabled = enabled,
            checked = checked,
            onCheckedChange = { onCheckedChange?.invoke(it) },
            colors = SwitchDefaults.colors(
               checkedTrackColor = Color(properties.trackColor),
                checkedThumbColor = Color(properties.thumbColor),
                disabledCheckedTrackColor = Color(properties.trackColor),
                disabledCheckedThumbColor = Color(properties.thumbColor),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ControlPadSwitchPreview(modifier: Modifier = Modifier) {
    DroidPadTheme {
        ControlPadSwitch(
            offset = Offset(0f, 0f),
            rotation = 0f,
            scale = 1f,
            checked = false,
            properties = SwitchProperties(
                trackColor = MaterialTheme.colorScheme.primary.value,
                thumbColor = MaterialTheme.colorScheme.onPrimary.value,
            )
        )
    }
}