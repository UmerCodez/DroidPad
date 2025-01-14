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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.data.ButtonProperties
import com.github.umer0586.droidpad.data.DpadProperties
import com.github.umer0586.droidpad.data.JoyStickProperties
import com.github.umer0586.droidpad.data.LabelProperties
import com.github.umer0586.droidpad.data.SliderProperties
import com.github.umer0586.droidpad.data.SwitchProperties
import com.github.umer0586.droidpad.ui.components.ControlPadButton
import com.github.umer0586.droidpad.ui.components.ControlPadDpad
import com.github.umer0586.droidpad.ui.components.ControlPadJoyStick
import com.github.umer0586.droidpad.ui.components.ControlPadSlider
import com.github.umer0586.droidpad.ui.components.ControlPadSwitch
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

// TODO: color picker doesn't show dark values, add these later
@Composable
fun ItemPropertiesEditorSheet(
    modifier: Modifier = Modifier,
    controlPadItem: ControlPadItem,
    onSaveSubmit: ((ControlPadItem) -> Unit)? = null,
    itemIdentifierMaxLength: Int = 10,
    labelTextMaxLength: Int = 8,
    buttonTextMaxLength: Int = 8
) {


    var modifiedControlPadItem by remember { mutableStateOf(controlPadItem.copy()) }
    var hasError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        var itemIdentifier by remember { mutableStateOf(controlPadItem.itemIdentifier) }

        OutlinedTextField(
            modifier = Modifier.testTag("itemIdentifierTextField"),
            singleLine = true,
            value = itemIdentifier,
            isError = itemIdentifier.isEmpty(),
            onValueChange = {

                if (it.isEmpty())
                    hasError = true
                else
                    hasError = false

                if (it.length <= itemIdentifierMaxLength) {
                    itemIdentifier = it
                    modifiedControlPadItem = modifiedControlPadItem.copy(itemIdentifier = it)
                }
            },
            label = { Text("Item Identifier") },
            shape = RoundedCornerShape(50.dp)
        )

        if (controlPadItem.itemType == ItemType.LABEL) {

            LabelPropertiesEditor(
                labelTextMaxLength = labelTextMaxLength,
                controlPadItem = controlPadItem,
                onLabelPropertiesChange = { labelProperties ->
                    modifiedControlPadItem = modifiedControlPadItem.copy(
                        properties = labelProperties.toJson()
                    )
                },
                hasError = { hasError = it }
            )
        } else if (controlPadItem.itemType == ItemType.SWITCH) {

            SwitchPropertiesEditor(
                controlPadItem = controlPadItem,
                onSwitchPropertiesChange = { switchProperties ->
                    modifiedControlPadItem = modifiedControlPadItem.copy(
                        properties = switchProperties.toJson()
                    )
                }
            )
        } else if (controlPadItem.itemType == ItemType.SLIDER) {

            SliderPropertiesEditor(
                controlPadItem = controlPadItem,
                onSliderPropertiesChange = { sliderProperties ->
                    modifiedControlPadItem = modifiedControlPadItem.copy(
                        properties = sliderProperties.toJson()
                    )
                },
                hasError = { hasError = it }
            )


        } else if (controlPadItem.itemType == ItemType.BUTTON) {

            ButtonPropertiesEditor(
                controlPadItem = controlPadItem,
                buttonTextMaxLength = buttonTextMaxLength,
                onButtonPropertiesChange = { buttonProperties ->
                    modifiedControlPadItem = modifiedControlPadItem.copy(
                        properties = buttonProperties.toJson()
                    )
                },
                hasError = { hasError = it }
            )
        } else if(controlPadItem.itemType == ItemType.DPAD){
            DPadPropertiesEditor(
                controlPadItem = controlPadItem,
                onDpadPropertiesChange = { dpadProperties ->
                    modifiedControlPadItem = modifiedControlPadItem.copy(
                        properties = dpadProperties.toJson()
                    )
                }
            )
        } else if(controlPadItem.itemType == ItemType.JOYSTICK){
            JoyStickPropertiesEditor(
                controlPadItem = controlPadItem,
                onJoyStickPropertiesChange = { joyStickProperties ->
                    modifiedControlPadItem = modifiedControlPadItem.copy(
                        properties = joyStickProperties.toJson()
                    )
                }
            )
        }


        TextButton(
            modifier = Modifier
                .testTag("saveBtn")
                .fillMaxWidth(0.5f),
            colors = ButtonDefaults.textButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = !hasError,
            onClick = {
                onSaveSubmit?.invoke(modifiedControlPadItem)
            },
            content = {
                Text("Save")
            }
        )


    }
}

// TODO: Add color choose for label text
@Composable
private fun LabelPropertiesEditor(
    controlPadItem: ControlPadItem,
    labelTextMaxLength: Int = 8,
    onLabelPropertiesChange: ((LabelProperties) -> Unit)? = null,
    hasError: ((Boolean) -> Unit)? = null,
) {
    var labelProperties by remember { mutableStateOf(LabelProperties.fromJson(controlPadItem.properties)) }

    OutlinedTextField(
        modifier = Modifier.testTag("labelTextField"),
        singleLine = true,
        value = labelProperties.text,
        isError = labelProperties.text.isEmpty(),
        onValueChange = {

            if(it.isEmpty())
                hasError?.invoke(true)
            else
                hasError?.invoke(false)

            if (it.length <= labelTextMaxLength) {
                labelProperties = labelProperties.copy(text = it)
                onLabelPropertiesChange?.invoke(labelProperties)
            }
        },
        label = { Text("Label Text") },
        shape = RoundedCornerShape(50.dp)
    )
}

@Composable
private fun SliderPropertiesEditor(
    modifier: Modifier = Modifier,
    controlPadItem: ControlPadItem,
    onSliderPropertiesChange: ((SliderProperties) -> Unit)? = null,
    hasError: ((Boolean) -> Unit)? = null,
) {

    var sliderProperties by remember { mutableStateOf(SliderProperties.fromJson(controlPadItem.properties)) }
    var minValue by remember { mutableFloatStateOf(sliderProperties.minValue) }
    var maxValue by remember { mutableFloatStateOf(sliderProperties.maxValue) }

    val textFieldShape = RoundedCornerShape(50.dp)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ControlPadSlider(
            offset = Offset.Zero,
            scale = 1f,
            rotation = 0f,
            showControls = false,
            value = 5f,
            properties = sliderProperties.copy(minValue = 0f, maxValue = 10f),
        )

        OutlinedTextField(
            modifier = Modifier.testTag("sliderMinValueTextField"),
            singleLine = true,
            prefix = { Text("Min") },
            value = minValue.toString(),
            isError = minValue >= maxValue,
            onValueChange = onValueChange@{
                minValue = it.toFloatOrNull()?.also { value ->
                    if( value >= maxValue )
                        hasError?.invoke(true)
                    else
                        hasError?.invoke(false)
                } ?: return@onValueChange

                sliderProperties = sliderProperties.copy(minValue = minValue)
                onSliderPropertiesChange?.invoke(sliderProperties)


            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = textFieldShape
        )

        OutlinedTextField(
            modifier = Modifier.testTag("sliderMaxValueTextField"),
            singleLine = true,
            prefix = { Text("Max") },
            value = maxValue.toString(),
            isError = maxValue <= minValue,
            onValueChange = onValueChange@{

                maxValue = it.toFloatOrNull()?.also { value ->
                    if(value < minValue)
                        hasError?.invoke(true)
                    else
                        hasError?.invoke(false)
                } ?: return@onValueChange

                sliderProperties = sliderProperties.copy(maxValue = maxValue)
                onSliderPropertiesChange?.invoke(sliderProperties)


            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = textFieldShape
        )

        var showColorPickerForThumb by remember { mutableStateOf(false) }
        var showColorPickerForTrack by remember { mutableStateOf(false) }

        AnimatedVisibility(visible = showColorPickerForThumb) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                initialColor = Color(sliderProperties.thumbColor),
                controller = rememberColorPickerController(),
                onColorChanged = {
                    sliderProperties = sliderProperties.copy(
                        thumbColor = it.color.value
                    )
                    onSliderPropertiesChange?.invoke(sliderProperties)
                }
            )
        }

        AnimatedVisibility(visible = showColorPickerForTrack) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                initialColor = Color(sliderProperties.trackColor),
                controller = rememberColorPickerController(),
                onColorChanged = {
                    sliderProperties = sliderProperties.copy(
                        trackColor = it.color.value
                    )
                    onSliderPropertiesChange?.invoke(sliderProperties)
                }
            )
        }

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Thumb Color") },
            trailingContent = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(sliderProperties.thumbColor))
                        .clickable {
                            showColorPickerForThumb = !showColorPickerForThumb
                            showColorPickerForTrack = false
                        })
            }
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Track Color") },
            trailingContent = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(sliderProperties.trackColor))
                        .clickable {
                            showColorPickerForTrack = !showColorPickerForTrack
                            showColorPickerForThumb = false
                        })
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ButtonPropertiesEditor(
    modifier: Modifier = Modifier,
    buttonTextMaxLength: Int = 5,
    controlPadItem: ControlPadItem,
    onButtonPropertiesChange: ((ButtonProperties) -> Unit)? = null,
    hasError: ((Boolean) -> Unit)? = null,
) {

    var buttonProperties by remember { mutableStateOf(ButtonProperties.fromJson(controlPadItem.properties))}
    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPickerForButton by remember { mutableStateOf(false) }
    var showColorPickerForIcon by remember { mutableStateOf(false) }
    var showColorPickerForText by remember { mutableStateOf(false) }

    val textFieldShape = RoundedCornerShape(50.dp)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ControlPadButton(
            offset = Offset.Zero,
            scale = 1f,
            rotation = 0f,
            showControls = false,
            properties = buttonProperties,
        )

        OutlinedTextField(
            modifier = Modifier.testTag("buttonTextTextField"),
            singleLine = true,
            value = buttonProperties.text,
            enabled = !buttonProperties.useIcon,
            isError = buttonProperties.text.isEmpty(),
            onValueChange = {

                if(it.isEmpty())
                    hasError?.invoke(true)
                else
                    hasError?.invoke(false)

                if (it.length <= buttonTextMaxLength) {
                    buttonProperties = buttonProperties.copy(text = it)
                    onButtonPropertiesChange?.invoke(buttonProperties)
                }

            },
            label = { Text("Text") },
            shape = textFieldShape
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Click Action") },
            trailingContent = {
                Switch(
                    checked = buttonProperties.useClickAction,
                    onCheckedChange = {
                        buttonProperties = buttonProperties.copy(useClickAction = it)
                        onButtonPropertiesChange?.invoke(buttonProperties)
                    }
                )
            }
        )


        AnimatedVisibility(visible = showColorPickerForText) {

            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                controller = rememberColorPickerController(),
                initialColor = Color(buttonProperties.textColor),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    buttonProperties = buttonProperties.copy(textColor = colorEnvelope.color.value)
                    onButtonPropertiesChange?.invoke(buttonProperties)
                    // do something
                }
            )
        }

        AnimatedVisibility(visible = showColorPickerForButton) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                controller = rememberColorPickerController(),
                initialColor = Color(buttonProperties.buttonColor),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    buttonProperties =
                        buttonProperties.copy(buttonColor = colorEnvelope.color.value)
                    onButtonPropertiesChange?.invoke(buttonProperties)
                    // do something
                }
            )
        }

        AnimatedVisibility(visible = showColorPickerForIcon) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                controller = rememberColorPickerController(),
                initialColor = Color(buttonProperties.iconColor),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    buttonProperties = buttonProperties.copy(iconColor = colorEnvelope.color.value)
                    onButtonPropertiesChange?.invoke(buttonProperties)
                }
            )
        }

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Use Icon") },
            trailingContent = {
                Switch(
                    checked = buttonProperties.useIcon,
                    onCheckedChange = {
                        buttonProperties = buttonProperties.copy(useIcon = it)
                        onButtonPropertiesChange?.invoke(buttonProperties)
                    }
                )
            }
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Button Color") },
            trailingContent = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(buttonProperties.buttonColor))
                        .clickable {
                            showColorPickerForButton = !showColorPickerForButton
                            showColorPickerForIcon = false
                            showColorPickerForText = false
                        })
            }
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Text Color") },
            trailingContent = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(buttonProperties.textColor))
                        .clickable {
                            showColorPickerForText = !showColorPickerForText
                            showColorPickerForIcon = false
                            showColorPickerForButton = false
                        })
            }
        )

        AnimatedVisibility(visible = showIconPicker) {

            FlowRow(
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                ButtonProperties.iconIds.forEach { id ->
                    Icon(
                        modifier = Modifier
                            .size(35.dp)
                            .clickable {
                                showIconPicker = false
                                buttonProperties =
                                    buttonProperties.copy(iconId = id)
                                onButtonPropertiesChange?.invoke(buttonProperties)
                            },
                        painter = painterResource(ButtonProperties.getIconById(id)),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

        }

        AnimatedVisibility(visible = buttonProperties.useIcon) {


            Column {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .clickable {
                            showIconPicker = !showIconPicker
                        },
                    headlineContent = { Text(text = "Icon") },
                    trailingContent = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(
                                id = ButtonProperties.getIconById(
                                    buttonProperties.iconId
                                )
                            ),
                            contentDescription = null,
                        )
                    }
                )

                ListItem(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    headlineContent = { Text(text = "Icon Color") },
                    trailingContent = {
                        Box(
                            Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(buttonProperties.iconColor))
                                .clickable {
                                    showColorPickerForIcon = !showColorPickerForIcon
                                    showColorPickerForButton = false
                                    showColorPickerForText = false
                                })
                    }
                )
            }
        }
    }
}


@Composable
private fun DPadPropertiesEditor(
    modifier: Modifier = Modifier,
    controlPadItem: ControlPadItem,
    onDpadPropertiesChange: ((DpadProperties) -> Unit)? = null,
) {

    var dPadProperties by remember { mutableStateOf(DpadProperties.fromJson(controlPadItem.properties))}
    var showColorPickerForButton by remember { mutableStateOf(false) }
    var showColorPickerForBackground by remember { mutableStateOf(false) }


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ControlPadDpad(
            offset = Offset.Zero,
            scale = 1f,
            rotation = 0f,
            showControls = false,
            properties = dPadProperties,
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Click Action") },
            trailingContent = {
                Switch(
                    checked = dPadProperties.useClickAction,
                    onCheckedChange = {
                        dPadProperties = dPadProperties.copy(useClickAction = it)
                        onDpadPropertiesChange?.invoke(dPadProperties)
                    }
                )
            }
        )

        AnimatedVisibility(visible = showColorPickerForButton) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                controller = rememberColorPickerController(),
                initialColor = Color(dPadProperties.buttonColor),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    dPadProperties =
                        dPadProperties.copy(buttonColor = colorEnvelope.color.value)
                    onDpadPropertiesChange?.invoke(dPadProperties)
                    // do something
                }
            )
        }

        AnimatedVisibility(visible = showColorPickerForBackground) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                controller = rememberColorPickerController(),
                initialColor = Color(dPadProperties.backgroundColor),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    dPadProperties = dPadProperties.copy(backgroundColor = colorEnvelope.color.value)
                    onDpadPropertiesChange?.invoke(dPadProperties)
                }
            )
        }

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Button Color") },
            trailingContent = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(dPadProperties.buttonColor))
                        .clickable {
                            showColorPickerForButton = !showColorPickerForButton
                            showColorPickerForBackground = false
                        }
                )
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
                        .background(Color(dPadProperties.backgroundColor))
                        .clickable {
                            showColorPickerForBackground = !showColorPickerForBackground
                            showColorPickerForButton = false
                        }
                )
            }
        )

    }
}

@Composable
private fun JoyStickPropertiesEditor(
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

@Composable
private fun SwitchPropertiesEditor(
    modifier: Modifier = Modifier,
    controlPadItem: ControlPadItem,
    onSwitchPropertiesChange: ((SwitchProperties) -> Unit)? = null,
) {

    var switchProperties by remember { mutableStateOf(SwitchProperties.fromJson(controlPadItem.properties)) }
    var showColorPickerForTrack by remember { mutableStateOf(false) }
    var showColorPickerForThumb by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ControlPadSwitch(
            offset = Offset.Zero,
            scale = 1f,
            rotation = 0f,
            checked = true,
            showControls = false,
            properties = switchProperties,
        )


        AnimatedVisibility(visible = showColorPickerForTrack) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                controller = rememberColorPickerController(),
                initialColor = Color(switchProperties.trackColor),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    switchProperties =
                        switchProperties.copy(trackColor = colorEnvelope.color.value)
                    onSwitchPropertiesChange?.invoke(switchProperties)
                    // do something
                }
            )
        }

        AnimatedVisibility(visible = showColorPickerForThumb) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                controller = rememberColorPickerController(),
                initialColor = Color(switchProperties.thumbColor),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    switchProperties = switchProperties.copy(thumbColor = colorEnvelope.color.value)
                    onSwitchPropertiesChange?.invoke(switchProperties)
                }
            )
        }

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Thumb Color") },
            trailingContent = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(switchProperties.thumbColor))
                        .clickable {
                            showColorPickerForThumb = !showColorPickerForThumb
                            showColorPickerForTrack = false

                        })
            }
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Track Color") },
            trailingContent = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(switchProperties.trackColor))
                        .clickable {
                            showColorPickerForTrack = !showColorPickerForTrack
                            showColorPickerForThumb = false
                        })
            }
        )


    }
}

// Run this in emulator. Bottom Sheet doesn't work properly in interactive mode
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ItemEditorModalBottomSheetPreview() {

    DroidPadTheme {

        var showItemEditor by remember { mutableStateOf(true) }
        val controlPadItem = ControlPadItem(
            id = 1,
            itemIdentifier = "label",
            controlPadId = 1,
            itemType = ItemType.BUTTON,
        )

        Box(Modifier.fillMaxSize()) {
            TextButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { showItemEditor = true }
            ) { Text("Open") }


            if (showItemEditor) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxSize(),
                    onDismissRequest = { showItemEditor = false }
                ) {
                    ItemPropertiesEditorSheet(
                        controlPadItem = controlPadItem,
                        onSaveSubmit = {
                            showItemEditor = false
                        }
                    )
                }
            }

        }
    }

}

@PreviewLightDark
@Preview(showBackground = true)
@Composable
private fun ItemEditorPreview() {
    DroidPadTheme {
        ItemPropertiesEditorSheet(
            controlPadItem = ControlPadItem(
                id = 1,
                itemIdentifier = "label",
                controlPadId = 1,
                itemType = ItemType.DPAD,
            )
        )
    }
}
