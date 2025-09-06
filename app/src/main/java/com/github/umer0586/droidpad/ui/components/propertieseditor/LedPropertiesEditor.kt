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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.github.umer0586.droidpad.data.LEDProperties
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.ui.components.ControlPadLED

@Composable
fun LEDPropertiesEditor(
    modifier: Modifier = Modifier,
    controlPadItem: ControlPadItem,
    onLEDPropertiesChange: ((LEDProperties) -> Unit)? = null
) {
    var ledProperties by remember { mutableStateOf(LEDProperties.fromJson(controlPadItem.properties)) }
    var showColorPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ControlPadLED(
            showControls = false,
            properties = ledProperties,
        )

        AnimatedVisibility(showColorPicker) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                controller = rememberColorPickerController(),
                initialColor = Color(ledProperties.color),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    ledProperties =
                        ledProperties.copy(color = colorEnvelope.color.value)
                    onLEDPropertiesChange?.invoke(ledProperties)
                    // do something
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
                        .background(Color(ledProperties.color))
                        .clickable {
                            showColorPicker = !showColorPicker
                        })
            }
        )
    }
}