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

package com.github.umer0586.droidpad.ui.screens.controlpadbuilderscreen

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.umer0586.droidpad.R
import com.github.umer0586.droidpad.data.ButtonProperties
import com.github.umer0586.droidpad.data.DpadProperties
import com.github.umer0586.droidpad.data.ExternalData
import com.github.umer0586.droidpad.data.JoyStickProperties
import com.github.umer0586.droidpad.data.LabelProperties
import com.github.umer0586.droidpad.data.Resolution
import com.github.umer0586.droidpad.data.SliderProperties
import com.github.umer0586.droidpad.data.SteeringWheelProperties
import com.github.umer0586.droidpad.data.StepSliderProperties
import com.github.umer0586.droidpad.data.SwitchProperties
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.data.database.entities.offset
import com.github.umer0586.droidpad.ui.bottomBarHeight
import com.github.umer0586.droidpad.ui.components.ControlPadButton
import com.github.umer0586.droidpad.ui.components.ControlPadDpad
import com.github.umer0586.droidpad.ui.components.ControlPadJoyStick
import com.github.umer0586.droidpad.ui.components.ControlPadLabel
import com.github.umer0586.droidpad.ui.components.ControlPadSlider
import com.github.umer0586.droidpad.ui.components.ControlPadSteeringWheel
import com.github.umer0586.droidpad.ui.components.ControlPadStepSlider
import com.github.umer0586.droidpad.ui.components.ControlPadSwitch
import com.github.umer0586.droidpad.ui.components.propertieseditor.ItemPropertiesEditorSheet
import com.github.umer0586.droidpad.ui.components.rotateBy
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import com.github.umer0586.droidpad.ui.utils.LockScreenOrientation
import kotlinx.coroutines.delay


// TODO: Add color picker for choosing background color of ControlPad
@Composable
fun ControlPadBuilderScreen(
    externalData: ExternalData? = null,
    controlPad: ControlPad,
    tempOpen: Boolean = false,
    viewModel: ControlPadBuilderScreenViewModel = hiltViewModel(),
    onSaveClick: (() -> Unit)? = null,
    onBackPress: (() -> Unit)? = null,
    onTempOpenCompleted: ((ExternalData?) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit){
        Log.d("ControlPadBuilderScreen","LaunchedEffect(Unit) : ${controlPad.id}")
        if (!tempOpen)
            viewModel.loadControlPadItemsFor(controlPad)
    }

    LockScreenOrientation(
        orientation = when(controlPad.orientation){
            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Orientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    )

    ControlPadBuilderScreenContent(
        tempOpen = tempOpen,
        controlPad = controlPad,
        uiState = uiState,
        onUiEvent = { event ->
            viewModel.onEvent(event)

            if(event is ControlPadBuilderScreenEvent.OnSaveClick)
                onSaveClick?.invoke()
            else if(event is ControlPadBuilderScreenEvent.OnBackPress)
                onBackPress?.invoke()
            else if(event is ControlPadBuilderScreenEvent.OnTempOpenCompleted)
                onTempOpenCompleted?.invoke(externalData)

        }
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlPadBuilderScreenContent(
    tempOpen: Boolean = false,
    controlPad: ControlPad,
    uiState: ControlPadBuilderScreenState,
    onUiEvent: (ControlPadBuilderScreenEvent) -> Unit

) {

    Scaffold(
        bottomBar = {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bottomBarHeight)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {

                    var showModificationAlert by remember { mutableStateOf(false) }

                    // When back button on device is pressed
                    BackHandler {
                        if(uiState.isModified){
                            showModificationAlert = true
                            return@BackHandler
                        }
                        onUiEvent(ControlPadBuilderScreenEvent.OnBackPress)
                    }

                    if(showModificationAlert){
                        AlertDialog(
                            onDismissRequest = { showModificationAlert = false },
                            title = { Text(text = "Unsaved Changes") },
                            text = { Text(text = "Interface has been modified") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showModificationAlert = false
                                        onUiEvent(ControlPadBuilderScreenEvent.OnBackPress)
                                    }
                                ) { Text("Discard Changes")}
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showModificationAlert = false
                                        onUiEvent(ControlPadBuilderScreenEvent.OnSaveClick)
                                    }
                                ) { Text("Save Changes")}
                            }
                        )
                    }

                    IconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = {
                            onUiEvent(ControlPadBuilderScreenEvent.OnAddItemClick)
                        },
                        content = {
                            Icon(
                                modifier = Modifier.clickable {

                                    if(uiState.isModified){
                                        showModificationAlert = true
                                        return@clickable
                                    }

                                    onUiEvent(ControlPadBuilderScreenEvent.OnBackPress)
                                },
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "AddIcon",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    )

                    Row(
                        Modifier
                            .clip(shape = RoundedCornerShape(50.dp))
                            .background(MaterialTheme.colorScheme.onPrimary),
                    ) {

                        IconButton(
                            onClick = {
                                onUiEvent(ControlPadBuilderScreenEvent.OnAddItemClick)
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "AddIcon"
                                )
                            }
                        )
                        IconButton(
                            onClick = {
                                onUiEvent(ControlPadBuilderScreenEvent.OnSaveClick)
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "SaveIcon"
                                )
                            }
                        )

                    }
                }

        }
    ) { innerPadding->
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .background(Color(controlPad.backgroundColor.toULong()))
                .padding(innerPadding)
                .clipToBounds()
        ) {

            val density = LocalDensity.current
            val widthPx = with(density) { maxWidth.toPx() }
            val heightPx = with(density) { maxHeight.toPx() }

            LaunchedEffect(Unit) {
                onUiEvent(
                    ControlPadBuilderScreenEvent.OnResolutionReported(
                        controlPad = controlPad,
                        builderScreenResolution = Resolution(
                            widthPx.toInt(),
                            heightPx.toInt()
                        ),
                        tempOpen = tempOpen
                    )
                )

                if(tempOpen) {
                    delay(5000)
                    onUiEvent(ControlPadBuilderScreenEvent.OnTempOpenCompleted)
                }
            }

            if(tempOpen){
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Please Wait...")
                    Spacer(modifier = Modifier.height(20.dp))
                    LinearProgressIndicator()
                }

                return@BoxWithConstraints
            }


            uiState.controlPadItems.forEach{controlPadItem ->

                if (controlPadItem.itemType == ItemType.SWITCH && uiState.transformableStatesMap[controlPadItem.id] != null) {

                    ControlPadSwitch(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        transformableState = uiState.transformableStatesMap[controlPadItem.id],
                        properties = SwitchProperties.fromJson(controlPadItem.properties),
                        enabled = false,
                        onDeleteClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnDeleteItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        },
                        onEditClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnEditItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        }

                    )
                }

                else if (controlPadItem.itemType == ItemType.SLIDER && uiState.transformableStatesMap[controlPadItem.id] != null) {
                    val properties = SliderProperties.fromJson(controlPadItem.properties)
                    ControlPadSlider(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        transformableState = uiState.transformableStatesMap[controlPadItem.id],
                        properties = properties,
                        enabled = false,
                        value = (properties.minValue + properties.maxValue)/2,
                        onDeleteClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnDeleteItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        },
                        onEditClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnEditItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        }


                    )
                }

                else if (controlPadItem.itemType == ItemType.STEP_SLIDER && uiState.transformableStatesMap[controlPadItem.id] != null) {
                    val properties = StepSliderProperties.fromJson(controlPadItem.properties)
                    ControlPadStepSlider(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        transformableState = uiState.transformableStatesMap[controlPadItem.id],
                        properties = properties,
                        enabled = false,
                        value = (properties.minValue + properties.maxValue)/2,
                        onDeleteClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnDeleteItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        },
                        onEditClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnEditItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        }
                    )
                }

                else if(controlPadItem.itemType == ItemType.LABEL && uiState.transformableStatesMap[controlPadItem.id] != null){
                    ControlPadLabel(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        transformableState = uiState.transformableStatesMap[controlPadItem.id],
                        properties = LabelProperties.fromJson(controlPadItem.properties),
                        onDeleteClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnDeleteItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        },
                        onEditClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnEditItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        }
                    )
                }

                else if(controlPadItem.itemType == ItemType.BUTTON && uiState.transformableStatesMap[controlPadItem.id] != null){

                    ControlPadButton(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        transformableState = uiState.transformableStatesMap[controlPadItem.id],
                        properties = ButtonProperties.fromJson(controlPadItem.properties),
                        enabled = false,
                        onDeleteClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnDeleteItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        },
                        onEditClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnEditItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        }
                    )
                }

                else if(controlPadItem.itemType == ItemType.DPAD && uiState.transformableStatesMap[controlPadItem.id] != null){

                    ControlPadDpad(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        transformableState = uiState.transformableStatesMap[controlPadItem.id],
                        properties = DpadProperties.fromJson(controlPadItem.properties),
                        enabled = false,
                        onDeleteClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnDeleteItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        },
                        onEditClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnEditItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        }
                    )
                }

                else if(controlPadItem.itemType == ItemType.JOYSTICK && uiState.transformableStatesMap[controlPadItem.id] != null){

                    ControlPadJoyStick(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        transformableState = uiState.transformableStatesMap[controlPadItem.id],
                        properties = JoyStickProperties.fromJson(controlPadItem.properties),
                        enabled = false,
                        onDeleteClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnDeleteItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        },
                        onEditClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnEditItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        }
                    )
                }
                else if(controlPadItem.itemType == ItemType.STEERING_WHEEL && uiState.transformableStatesMap[controlPadItem.id] != null){
                    ControlPadSteeringWheel(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        transformableState = uiState.transformableStatesMap[controlPadItem.id],
                        properties = SteeringWheelProperties.fromJson(controlPadItem.properties),
                        enabled = false,
                        onDeleteClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnDeleteItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        },
                        onEditClick = {
                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnEditItemClick(
                                    controlPad = controlPad,
                                    controlPadItem = controlPadItem
                                )
                            )
                        }
                    )
                }




            }

            val primary = MaterialTheme.colorScheme.primary
            val onPrimary = MaterialTheme.colorScheme.onPrimary


            if (uiState.showItemChooser) {
                ModalBottomSheet(
                    modifier = Modifier.zIndex(5f),
                    onDismissRequest = { onUiEvent(ControlPadBuilderScreenEvent.OnItemChooserDismissRequest) },
                ) {
                    ItemSelectionBottomSheetContent(
                        onItemClick = { itemType ->

                            val properties = when(itemType){
                                ItemType.LABEL -> LabelProperties().toJson()
                                ItemType.BUTTON -> ButtonProperties(
                                    buttonColor = primary.value,
                                    textColor = onPrimary.value,
                                    iconColor = onPrimary.value,
                                ).toJson()
                                ItemType.SWITCH -> SwitchProperties(
                                    trackColor = primary.value,
                                    thumbColor = onPrimary.value,
                                ).toJson()
                                ItemType.SLIDER -> SliderProperties(
                                    trackColor = primary.value,
                                    thumbColor = primary.value,
                                ).toJson()
                                ItemType.STEP_SLIDER -> StepSliderProperties(
                                    trackColor = primary.value,
                                    thumbColor = primary.value,
                                ).toJson()
                                ItemType.DPAD -> DpadProperties(
                                    backgroundColor = primary.value,
                                    buttonColor = onPrimary.value,
                                ).toJson()
                                ItemType.JOYSTICK -> JoyStickProperties(
                                    backgroundColor = primary.value,
                                    handleColor = onPrimary.value,
                                ).toJson()
                                ItemType.STEERING_WHEEL -> SteeringWheelProperties(
                                    color = primary.value
                                ).toJson()
                            }

                            onUiEvent(
                                ControlPadBuilderScreenEvent.OnItemTypeSelected(
                                    itemType = itemType,
                                    controlPad = controlPad,
                                    properties = properties
                                    )
                            )



                        }
                    )

                }
            }

            if (uiState.showItemEditor && uiState.itemToBeEdited != null) {
                ModalBottomSheet(
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    onDismissRequest = { onUiEvent(ControlPadBuilderScreenEvent.OnItemEditorDismissRequest) },
                ) {
                    ItemPropertiesEditorSheet(
                        controlPadItem = uiState.itemToBeEdited,
                        onSaveSubmit = {
                            onUiEvent(ControlPadBuilderScreenEvent.OnItemEditSubmit(it, controlPad))

                        }
                    )
                }
            }

        }
    }

}


@Composable
private fun ItemSelectionBottomSheetContent(
    modifier: Modifier = Modifier,
    controlPadItemTypes: Array<ItemType> = ItemType.entries.toTypedArray(),
    onItemClick: ((ItemType) -> Unit)? = null
) {

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(controlPadItemTypes) { item ->
            Row(
                modifier = Modifier
                    .clickable {
                        onItemClick?.invoke(item)
                    }
                    .fillMaxWidth(0.5f)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    modifier = Modifier.size(25.dp),
                    painter = painterResource(
                        when (item) {
                            ItemType.SWITCH -> R.drawable.ic_switch
                            ItemType.JOYSTICK -> R.drawable.ic_joystick
                            ItemType.STEERING_WHEEL -> R.drawable.ic_steering_wheel
                            ItemType.DPAD -> R.drawable.ic_dpad
                            ItemType.SLIDER -> R.drawable.ic_slider
                            ItemType.STEP_SLIDER -> R.drawable.ic_slider
                            ItemType.LABEL -> R.drawable.ic_label
                            ItemType.BUTTON -> R.drawable.ic_button_circle
                        }
                    ),
                    contentDescription = item.name,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    modifier = Modifier.weight(0.3f),
                    text = item.name.replace("_", " ")
                )

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ItemSelectionBottomSheetContentPreview() {
    DroidPadTheme {
        Surface {
            ItemSelectionBottomSheetContent()
        }
    }
}


@Preview(showBackground = true,uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ControlPadBuilderScreenContentInteractiveXPreview() {
    val uiState = remember {
        mutableStateOf(
            ControlPadBuilderScreenState(
                controlPadItems = mutableStateListOf(),
                transformableStatesMap = SnapshotStateMap(),
                isModified = true
            )
        )
    }
    var itemId by remember { mutableStateOf(0L) }

    val controlPad = ControlPad(
        id = 100,
        name = "myController",
        orientation = Orientation.LANDSCAPE,
    )

    val controlPadItems = remember{
        mutableStateListOf(
            ControlPadItem(
                id = 1000,
                itemIdentifier = "label1",
                controlPadId = controlPad.id,
                itemType = ItemType.SWITCH,
            ),
            ControlPadItem(
                id = itemId++,
                offsetY = 100f,
                itemIdentifier = "slider1",
                controlPadId = controlPad.id,
                itemType = ItemType.SLIDER,
            )
        )
    }

    val minScale = 1f
    val maxScale = 6f

    DroidPadTheme {
        ControlPadBuilderScreenContent(
            tempOpen = true,
            controlPad = controlPad,
            uiState = uiState.value,
            onUiEvent = { event ->
                when (event) {
                    is ControlPadBuilderScreenEvent.OnAddItemClick -> {
                        uiState.value = uiState.value.copy(showItemChooser = true)
                    }

                    is ControlPadBuilderScreenEvent.OnDeleteItemClick -> {

                        uiState.value.controlPadItems.remove(event.controlPadItem)
                        uiState.value.transformableStatesMap.remove(event.controlPadItem.id)

                    }

                    is ControlPadBuilderScreenEvent.OnItemTypeSelected -> {
                        uiState.value = uiState.value.copy(showItemChooser = false)

                        itemId++
                        val newItem = ControlPadItem(
                            id = itemId,
                            itemIdentifier = "${event.itemType.name.lowercase()}$itemId",
                            controlPadId = event.controlPad.id,
                            itemType = event.itemType,
                        )
                        uiState.value.controlPadItems.add(newItem)

                        uiState.value.transformableStatesMap[newItem.id] =
                            TransformableState { zoomChange, offsetChange, rotationChange ->

                                val index = uiState.value.controlPadItems.indexOfFirst { it.id == newItem.id }

                                val controlPadItem = uiState.value.controlPadItems[index]

                                val newScale = controlPadItem.scale * zoomChange
                                val newRotation = controlPadItem.rotation + rotationChange
                                val newOffset = controlPadItem.offset + offsetChange.rotateBy(newRotation) * newScale

                                uiState.value.controlPadItems[index] =
                                    controlPadItem.copy(
                                        offsetX = newOffset.x,
                                        offsetY = newOffset.y,
                                        rotation = newRotation,
                                        scale = newScale.coerceIn(minScale,maxScale)
                                    )

                            }
                    }

                    is ControlPadBuilderScreenEvent.OnEditItemClick -> {
                        uiState.value = uiState.value.copy(
                            showItemEditor = true,
                            itemToBeEdited = event.controlPadItem
                        )
                    }

                    ControlPadBuilderScreenEvent.OnItemChooserDismissRequest -> {
                        uiState.value = uiState.value.copy(showItemChooser = false)
                    }

                    ControlPadBuilderScreenEvent.OnItemEditorDismissRequest -> {
                        uiState.value = uiState.value.copy(showItemEditor = false)
                    }

                    is ControlPadBuilderScreenEvent.OnItemEditSubmit -> {
                        uiState.value = uiState.value.copy(showItemEditor = false)

                        val index = uiState.value.controlPadItems.indexOfFirst { it.id == event.controlPadItem.id }
                        val controlPadItem = uiState.value.controlPadItems[index]
                        uiState.value.controlPadItems[index] = controlPadItem.copy(
                            itemIdentifier = event.controlPadItem.itemIdentifier,
                            properties = event.controlPadItem.properties
                        )


                        //database operation

                    }
                    else -> TODO("Not Yet Implemented")
                }
            }

        )
    }

}

