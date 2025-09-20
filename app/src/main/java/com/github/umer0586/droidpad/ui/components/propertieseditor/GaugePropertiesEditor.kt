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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.github.umer0586.droidpad.data.GaugeProperties
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.ui.components.ControlPadGauge
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

@Composable
fun GaugePropertiesEditor(
    controlPadItem: ControlPadItem,
    modifier: Modifier = Modifier,
    onGaugePropertiesChange: ((GaugeProperties) -> Unit)? = null,
    hasError: ((Boolean) -> Unit)? = null
) {

    var gaugeProperties by remember { mutableStateOf(GaugeProperties.fromJson(controlPadItem.properties)) }
    var minValue by remember { mutableStateOf(gaugeProperties.minValue.toString()) }
    var maxValue by remember { mutableStateOf(gaugeProperties.maxValue.toString()) }
    var unit by remember { mutableStateOf(gaugeProperties.unit) }
    var minGreaterThanMaxError by remember { mutableStateOf(false) }

    LaunchedEffect(minValue, maxValue) {
        minValue.toFloatOrNull()?.also { minValueFloat ->
            maxValue.toFloatOrNull()?.also { maxValueFloat ->
                hasError?.invoke(minValueFloat >= maxValueFloat)
                minGreaterThanMaxError = minValueFloat >= maxValueFloat
                if(minValueFloat < maxValueFloat){
                    gaugeProperties = gaugeProperties.copy(minValue = minValueFloat, maxValue = maxValueFloat)
                    onGaugePropertiesChange?.invoke(gaugeProperties)
                }
            }?: hasError?.invoke(true)
        }?: hasError?.invoke(true)
    }

    val textFieldShape = RoundedCornerShape(50.dp)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ControlPadGauge(
            modifier = Modifier.size(250.dp),
            value = 50f,
            properties = gaugeProperties.copy(minValue = 0f, maxValue = 100f),
            showControls = false
        )

        if(minGreaterThanMaxError){
            Text(text = "Min should be less than Max")
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.7f),
            singleLine = true,
            prefix = { Text("Min") },
            value = minValue,
            isError = minValue.toFloatOrNull() == null,
            onValueChange = { minValue = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = textFieldShape
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.7f),
            singleLine = true,
            prefix = { Text("Max") },
            value = maxValue,
            isError = maxValue.toFloatOrNull() == null,
            onValueChange = { maxValue = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = textFieldShape
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.7f),
            singleLine = true,
            prefix = { Text("Unit") },
            value = unit,
            isError = unit.isEmpty().also {
                hasError?.invoke(it)
            },
            onValueChange = {
                unit = it
                gaugeProperties = gaugeProperties.copy(unit = it)
                onGaugePropertiesChange?.invoke(gaugeProperties)
            },
            shape = textFieldShape
        )

        var showColorPicker by remember { mutableStateOf(false) }

        AnimatedVisibility(showColorPicker) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                initialColor = Color(gaugeProperties.color),
                controller = rememberColorPickerController(),
                onColorChanged = {
                    gaugeProperties = gaugeProperties.copy(
                        color = it.color.value
                    )
                    onGaugePropertiesChange?.invoke(gaugeProperties)
                }
            )
        }

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Needle") },
            trailingContent = {
                Switch(
                    checked = gaugeProperties.needle,
                    onCheckedChange = {
                        gaugeProperties = gaugeProperties.copy(needle = it)
                        onGaugePropertiesChange?.invoke(gaugeProperties)
                    }
                )
            }
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Color") },
            trailingContent = {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(gaugeProperties.color))
                        .clickable {
                            showColorPicker = !showColorPicker
                        })
            }
        )

    }
}


@Preview
@Composable
fun GaugePropertiesEditorPreview() {

    val controlPadItem = ControlPadItem(
        controlPadId = 1,
        itemIdentifier = "my gauage",
        itemType = ItemType.GAUGE,
        properties = GaugeProperties().toJson()

    )

    DroidPadTheme {
        Surface {
            GaugePropertiesEditor(
                controlPadItem = controlPadItem
            )
        }
    }
}


