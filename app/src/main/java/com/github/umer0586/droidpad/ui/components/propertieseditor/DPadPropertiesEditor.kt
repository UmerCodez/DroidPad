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
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.github.umer0586.droidpad.data.DPAD_STYLE
import com.github.umer0586.droidpad.data.DpadProperties
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.ui.components.ControlPadDpad
import com.github.umer0586.droidpad.ui.components.EnumDropdown


@Composable
fun DPadPropertiesEditor(
    modifier: Modifier = Modifier,
    controlPadItem: ControlPadItem,
    onDpadPropertiesChange: ((DpadProperties) -> Unit)? = null,
) {

    var dPadProperties by remember { mutableStateOf(DpadProperties.fromJson(controlPadItem.properties)) }
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

        EnumDropdown<DPAD_STYLE>(
            modifier = Modifier.fillMaxWidth(0.7f),
            label = "Style",
            selectedValue = dPadProperties.style,
            onValueSelected = {
                dPadProperties = dPadProperties.copy(style = it)
                onDpadPropertiesChange?.invoke(dPadProperties)
            }

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