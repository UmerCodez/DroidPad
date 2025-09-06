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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.github.umer0586.droidpad.data.ButtonProperties
import com.github.umer0586.droidpad.data.ButtonShape
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.ui.components.ControlPadButton
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import java.util.Locale


@Composable
fun ButtonPropertiesEditor(
    modifier: Modifier = Modifier,
    buttonTextMaxLength: Int,
    controlPadItem: ControlPadItem,
    onButtonPropertiesChange: ((ButtonProperties) -> Unit)? = null,
    hasError: ((Boolean) -> Unit)? = null,
) {

    var buttonProperties by remember { mutableStateOf(ButtonProperties.fromJson(controlPadItem.properties)) }
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
            headlineContent = { Text(text = "Shape") },
            supportingContent = {Text(text = buttonProperties.shape.name.lowercase(Locale.getDefault()))},
            trailingContent = {
                var expanded by remember { mutableStateOf(false) }
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ButtonShape.entries.forEach { shape ->
                        DropdownMenuItem(
                            text = { Text(text = shape.name) },
                            onClick = {
                                expanded = false
                                buttonProperties = buttonProperties.copy(shape = shape)
                                onButtonPropertiesChange?.invoke(buttonProperties)
                            }
                        )
                    }
                }
            }
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

@Preview
@Composable
fun ButtonPropertiesEditorPreview(modifier: Modifier = Modifier) {
    DroidPadTheme {
        val buttonProperties = remember { mutableStateOf(ButtonProperties()) }
        Column(Modifier.verticalScroll(rememberScrollState())) {
            ButtonPropertiesEditor(
                modifier = Modifier.fillMaxWidth(),
                buttonTextMaxLength = 10,
                controlPadItem = ControlPadItem(
                    itemIdentifier = "button",
                    controlPadId = 1,
                    properties = buttonProperties.value.toJson(),
                    itemType = ItemType.BUTTON,
                ),
                onButtonPropertiesChange = {
                    buttonProperties.value = it
                }
            )
        }
    }
}