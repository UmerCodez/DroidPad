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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

// TODO: color picker doesn't show dark values, add these later
@Composable
fun ItemPropertiesEditorSheet(
    modifier: Modifier = Modifier,
    controlPadItem: ControlPadItem,
    onSaveSubmit: ((ControlPadItem) -> Unit)? = null,
    itemIdentifierMaxLength: Int = 10,
    labelTextMaxLength: Int = 10,
    buttonTextMaxLength: Int = 8
) {


    var modifiedControlPadItem by remember { mutableStateOf(controlPadItem.copy()) }
    var hasError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
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

        }

        else if (controlPadItem.itemType == ItemType.STEP_SLIDER){
            StepSliderPropertiesEditor(
                controlPadItem = controlPadItem,
                onStepSliderPropertiesChange = { stepSliderProperties ->
                    modifiedControlPadItem = modifiedControlPadItem.copy(
                        properties = stepSliderProperties.toJson()
                    )
                },
                hasError = {
                    hasError = it
                    Log.d("hasError:", it.toString())
                }
            )
        }

        else if (controlPadItem.itemType == ItemType.BUTTON) {

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
        } else if(controlPadItem.itemType == ItemType.STEERING_WHEEL){
            SteeringWheelPropertiesEditor(
                controlPadItem = controlPadItem,
                onSteeringWheelPropertiesChange = { steeringWheelProperties ->
                    modifiedControlPadItem = modifiedControlPadItem.copy(
                        properties = steeringWheelProperties.toJson()
                    )
                }
            )
        }


        TextButton(
            modifier = Modifier
                .width(150.dp)
                .padding(16.dp)
                .testTag("saveBtn"),
            colors = ButtonDefaults.textButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = !hasError,
            onClick = {
                onSaveSubmit?.invoke(modifiedControlPadItem)
            },
            contentPadding = PaddingValues(16.dp),
            content = {
                Text("Save")
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
                itemIdentifier = "dpad",
                controlPadId = 1,
                itemType = ItemType.STEP_SLIDER,
            )
        )
    }
}
