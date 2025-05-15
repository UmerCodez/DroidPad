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
import com.github.umer0586.droidpad.data.JoyStickProperties
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.ui.components.ControlPadJoyStick


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