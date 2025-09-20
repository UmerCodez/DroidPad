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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.data.SliderProperties
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

@Composable
fun ControlPadSlider(
    modifier: Modifier = Modifier,
    offset: Offset = Offset.Zero,
    rotation: Float = 0f,
    scale: Float = 1f,
    transformableState: TransformableState? = null,
    properties: SliderProperties = SliderProperties(),
    showControls: Boolean = true,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    value: Float = 2.5f,
    onValueChange: ((Float) -> Unit)? = null,
    enabled: Boolean = true,

    ){

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

        Box(
            contentAlignment = Alignment.Center
        ) {

            if(properties.showValue) {
                Text(
                    modifier = Modifier.align(Alignment.TopCenter).offset(y = (-10).dp),
                    text = "%.2f".format(value)
                )
            }

            Slider(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(0.5f),
                enabled = enabled,
                value = value,
                valueRange = properties.minValue..properties.maxValue,
                onValueChange = { onValueChange?.invoke(it) },
                colors = SliderDefaults.colors(
                    thumbColor = Color(properties.thumbColor),
                    activeTrackColor = Color(properties.trackColor),
                    disabledThumbColor = Color(properties.thumbColor),
                    disabledActiveTrackColor = Color(properties.trackColor),

                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ControlPadSliderItemPreview(){

    var value by remember { mutableFloatStateOf(0f) }
    DroidPadTheme {
        Box(
            Modifier
                .fillMaxSize()
                ){
            ControlPadSlider(
                modifier = Modifier.align(Alignment.Center),
                properties = SliderProperties(showValue = true),
                offset = Offset.Zero,
                rotation = 0f,
                scale = 1f,
                value = value,
                onValueChange = { value = it }
            )

        }
    }
}