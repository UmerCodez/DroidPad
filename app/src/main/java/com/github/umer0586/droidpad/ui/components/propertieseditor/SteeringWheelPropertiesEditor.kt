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

package com.github.umer0586.droidpad.ui.components.propertieseditor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.github.umer0586.droidpad.data.SteeringWheelProperties
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.ui.components.ControlPadSteeringWheel


@Composable
fun SteeringWheelPropertiesEditor(
    modifier: Modifier = Modifier,
    controlPadItem: ControlPadItem,
    onSteeringWheelPropertiesChange: ((SteeringWheelProperties) -> Unit)? = null,
){
    var steeringWheelProperties by remember { mutableStateOf(SteeringWheelProperties.fromJson(controlPadItem.properties)) }
    var showColorPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ControlPadSteeringWheel(
            modifier = Modifier.size(150.dp),
            properties = steeringWheelProperties,
            showActionExpander = false,
            enabled = false
        )

        AnimatedVisibility(showColorPicker) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                controller = rememberColorPickerController(),
                initialColor = Color(steeringWheelProperties.color),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    steeringWheelProperties =
                        steeringWheelProperties.copy(color = colorEnvelope.color.value)
                    onSteeringWheelPropertiesChange?.invoke(steeringWheelProperties)
                }
            )
        }

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Color") },
            trailingContent = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(steeringWheelProperties.color))
                        .clickable {
                            showColorPicker = !showColorPicker
                        })
            }

        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Free Rotation") },
            trailingContent = {
                Switch(
                    checked = steeringWheelProperties.freeRotation,
                    onCheckedChange = {
                        steeringWheelProperties = steeringWheelProperties.copy(freeRotation = it)
                        onSteeringWheelPropertiesChange?.invoke(steeringWheelProperties)
                    }
                )
            }

        )

        if (!steeringWheelProperties.freeRotation) {
            ListItem(
                modifier = Modifier.fillMaxWidth(0.7f),
                headlineContent = {
                    Slider(
                        value = steeringWheelProperties.maxAngle.toFloat(),
                        onValueChange = {
                            steeringWheelProperties =
                                steeringWheelProperties.copy(maxAngle = it.toInt())
                            onSteeringWheelPropertiesChange?.invoke(steeringWheelProperties)
                        },
                        valueRange = 45f..360f,
                    )
                },
                overlineContent = { Text("Max Angle") },
                supportingContent = {
                    Text(steeringWheelProperties.maxAngle.toString())

                }
            )
        }

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Self Centering") },
            trailingContent = {
                Switch(
                    checked = steeringWheelProperties.selfCentering,
                    onCheckedChange = {
                        steeringWheelProperties = steeringWheelProperties.copy(selfCentering = it)
                        onSteeringWheelPropertiesChange?.invoke(steeringWheelProperties)
                    }
                )
            }

        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Multi Touch") },
            supportingContent = { Text("Use two fingers to rotate") },
            trailingContent = {
                Switch(
                    checked = steeringWheelProperties.multiTouch,
                    onCheckedChange = {
                        steeringWheelProperties = steeringWheelProperties.copy(multiTouch = it)
                        onSteeringWheelPropertiesChange?.invoke(steeringWheelProperties)
                    }
                )
            }

        )


    }



}