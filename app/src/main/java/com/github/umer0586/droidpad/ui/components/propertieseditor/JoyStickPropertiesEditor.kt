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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.github.umer0586.droidpad.data.JoyStickProperties
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.ui.components.ControlPadJoyStick
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoyStickPropertiesEditor(
    modifier: Modifier = Modifier,
    controlPadItem: ControlPadItem,
    onJoyStickPropertiesChange: ((JoyStickProperties) -> Unit)? = null,
) {

    var joyStickProperties by remember { mutableStateOf(JoyStickProperties.fromJson(controlPadItem.properties)) }
    var showColorPickerForBackground by remember { mutableStateOf(false) }
    var showColorPickerForHandle by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ControlPadJoyStick(
            offset = Offset.Zero,
            scale = 1f,
            rotation = 0f,
            showControls = false,
            enabled = false,
            properties = joyStickProperties,
        )


        AnimatedVisibility(visible = showColorPickerForBackground) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                controller = rememberColorPickerController(),
                initialColor = Color(joyStickProperties.backgroundColor),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    joyStickProperties =
                        joyStickProperties.copy(backgroundColor = colorEnvelope.color.value)
                    onJoyStickPropertiesChange?.invoke(joyStickProperties)
                    // do something
                }
            )
        }

        AnimatedVisibility(visible = showColorPickerForHandle) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                controller = rememberColorPickerController(),
                initialColor = Color(joyStickProperties.handleColor),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    joyStickProperties = joyStickProperties.copy(handleColor = colorEnvelope.color.value)
                    onJoyStickPropertiesChange?.invoke(joyStickProperties)
                }
            )
        }

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Show Coordinates") },
            trailingContent = {
                Switch(
                    checked = joyStickProperties.showCoordinates,
                    onCheckedChange = {
                        joyStickProperties = joyStickProperties.copy(showCoordinates = it)
                        onJoyStickPropertiesChange?.invoke(joyStickProperties)
                    }
                )
            }
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = {
                Slider(
                    value = joyStickProperties.handleRadiusFactor,
                    onValueChange = {
                        joyStickProperties = joyStickProperties.copy(handleRadiusFactor = it)
                        onJoyStickPropertiesChange?.invoke(joyStickProperties)
                    },
                    valueRange = 0.4f..0.9f
                )
            },
            overlineContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    val tooltipState = rememberTooltipState()
                    val scope = rememberCoroutineScope()

                    Text(text = "Handle Radius Factor")

                    Spacer(modifier = Modifier.width(8.dp))

                    TooltipBox(
                        positionProvider = TooltipDefaults. rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            Text(text = "Ratio of handle radius to joystick radius\n Min:0.4 Max:0.9")
                        },
                        state = tooltipState,
                    ) {
                        Icon(
                            modifier = Modifier.clickable {
                                scope.launch {
                                    tooltipState.show()
                                }
                            },
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                        )
                    }
                }
            },
            supportingContent = {
                //Text("Ratio of handle radius to joystick radius")
                Text(joyStickProperties.handleRadiusFactor.toString())
            },
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Handle Color") },
            trailingContent = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(joyStickProperties.handleColor))
                        .clickable {
                            showColorPickerForHandle = !showColorPickerForHandle
                            showColorPickerForBackground = false

                        })
            }
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Background Color") },
            trailingContent = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(joyStickProperties.backgroundColor))
                        .clickable {
                            showColorPickerForBackground = !showColorPickerForBackground
                            showColorPickerForHandle = false
                        })
            }
        )


    }
}

@Preview
@Composable
private fun JoyStickPropertiesEditorPreview() {
    DroidPadTheme {
        var joyStickProperties by remember { mutableStateOf(JoyStickProperties()) }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
        ) {
            JoyStickPropertiesEditor(
                modifier = Modifier.fillMaxWidth(),
                controlPadItem = ControlPadItem(
                    itemIdentifier = "joystick",
                    controlPadId = 1,
                    itemType = ItemType.JOYSTICK,
                    properties = joyStickProperties.toJson()
                ),
                onJoyStickPropertiesChange = {
                    joyStickProperties = it
                }

            )
        }
    }
}
