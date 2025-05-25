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

import android.util.Log
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
import androidx.compose.material3.Slider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.github.umer0586.droidpad.data.StepSliderProperties
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.ui.components.ControlPadStepSlider

@Composable
fun StepSliderPropertiesEditor(
    modifier: Modifier = Modifier,
    controlPadItem: ControlPadItem,
    onStepSliderPropertiesChange: ((StepSliderProperties) -> Unit)? = null,
    hasError: ((Boolean) -> Unit)? = null,
) {

    var stepSliderProperties by remember { mutableStateOf(StepSliderProperties.fromJson(controlPadItem.properties)) }
    var minValue by remember { mutableStateOf(stepSliderProperties.minValue.toString()) }
    var maxValue by remember { mutableStateOf(stepSliderProperties.maxValue.toString()) }
    var steps by remember { mutableStateOf(stepSliderProperties.steps.toString()) }
    var minGreaterThanMaxError by remember { mutableStateOf(false) }

    LaunchedEffect(minValue, maxValue) {
        Log.d("Values", "min: $minValue, max: $maxValue")
        minValue.toFloatOrNull()?.also { minValueFloat ->
            maxValue.toFloatOrNull()?.also { maxValueFloat ->
                hasError?.invoke(minValueFloat >= maxValueFloat)
                minGreaterThanMaxError = minValueFloat >= maxValueFloat
                if(minValueFloat < maxValueFloat){
                    stepSliderProperties = stepSliderProperties.copy(minValue = minValueFloat, maxValue = maxValueFloat)
                    onStepSliderPropertiesChange?.invoke(stepSliderProperties)
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

        ControlPadStepSlider(
            offset = Offset.Zero,
            scale = 1f,
            rotation = 0f,
            showActionExpander = false,
            value = 5f,
            properties = stepSliderProperties.copy(minValue = 0f, maxValue = 10f),
        )

        if(minGreaterThanMaxError){
            Text(text = "Min should be less than Max")
        }

        OutlinedTextField(
            modifier = Modifier.testTag("sliderMinValueTextField"),
            singleLine = true,
            prefix = { Text("Min") },
            value = minValue,
            isError = minValue.toFloatOrNull() == null,
            onValueChange = { minValue = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = textFieldShape
        )

        OutlinedTextField(
            modifier = Modifier.testTag("sliderMaxValueTextField"),
            singleLine = true,
            prefix = { Text("Max") },
            value = maxValue,
            isError = maxValue.toFloatOrNull() == null,
            onValueChange = { maxValue = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = textFieldShape
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = {
                Slider(
                value = stepSliderProperties.steps.toFloat(),
                onValueChange = {
                    stepSliderProperties = stepSliderProperties.copy(steps = it.toInt())
                    onStepSliderPropertiesChange?.invoke(stepSliderProperties)
                },
                valueRange = 1f..50f,
            )
            },
            overlineContent = { Text("Steps") },
            supportingContent = {
                Text(stepSliderProperties.steps.toString())

            }
        )

        ListItem(
            modifier = Modifier.fillMaxWidth(0.7f),
            headlineContent = { Text(text = "Show Value") },
            trailingContent = {
                Switch(
                    checked = stepSliderProperties.showValue,
                    onCheckedChange = {
                        stepSliderProperties = stepSliderProperties.copy(showValue = it)
                        onStepSliderPropertiesChange?.invoke(stepSliderProperties)
                    }
                )
            }
        )

        var showColorPickerForThumb by remember { mutableStateOf(false) }
        var showColorPickerForTrack by remember { mutableStateOf(false) }

        AnimatedVisibility(visible = showColorPickerForThumb) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                initialColor = Color(stepSliderProperties.thumbColor),
                controller = rememberColorPickerController(),
                onColorChanged = {
                    stepSliderProperties = stepSliderProperties.copy(
                        thumbColor = it.color.value
                    )
                    onStepSliderPropertiesChange?.invoke(stepSliderProperties)
                }
            )
        }

        AnimatedVisibility(visible = showColorPickerForTrack) {
            HsvColorPicker(
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp),
                initialColor = Color(stepSliderProperties.trackColor),
                controller = rememberColorPickerController(),
                onColorChanged = {
                    stepSliderProperties = stepSliderProperties.copy(
                        trackColor = it.color.value
                    )
                    onStepSliderPropertiesChange?.invoke(stepSliderProperties)
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
                        .background(Color(stepSliderProperties.thumbColor))
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
                        .background(Color(stepSliderProperties.trackColor))
                        .clickable {
                            showColorPickerForTrack = !showColorPickerForTrack
                            showColorPickerForThumb = false
                        })
            }
        )

    }
}