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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.data.database.entities.offset
import com.github.umer0586.droidpad.data.properties.ButtonProperties
import com.github.umer0586.droidpad.data.properties.LabelProperties
import com.github.umer0586.droidpad.data.properties.SliderProperties
import com.github.umer0586.droidpad.data.properties.SwitchProperties
import com.github.umer0586.droidpad.ui.components.ControlPadButton
import com.github.umer0586.droidpad.ui.components.ControlPadClickButton
import com.github.umer0586.droidpad.ui.components.ControlPadLabel
import com.github.umer0586.droidpad.ui.components.ControlPadSlider
import com.github.umer0586.droidpad.ui.components.ControlPadSwitch
import com.github.umer0586.droidpad.ui.components.propertieseditor.ItemPropertiesEditorSheet
import com.github.umer0586.droidpad.ui.components.rotateBy
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import com.github.umer0586.droidpad.ui.utils.LockScreenOrientation


// TODO: Add color picker for choosing background color of ControlPad
@Composable
fun ControlPadBuilderScreen(
    controlPad: ControlPad,
    viewModel: ControlPadBuilderScreenViewModel = hiltViewModel(),
    onSaveClick: (() -> Unit)? = null,
    onBackPress: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit){
        Log.d("ControlPadBuilderScreen","LaunchedEffect(Unit) : ${controlPad.id}")
        viewModel.loadControlPadItemsFor(controlPad)
    }

    LockScreenOrientation(
        orientation = when(controlPad.orientation){
            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Orientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    )

    ControlPadBuilderScreenContent(
        controlPad = controlPad,
        uiState = uiState,
        onUiEvent = { event ->
            viewModel.onEvent(event)

            if(event is ControlPadBuilderScreenEvent.OnSaveClick)
                onSaveClick?.invoke()
            else if(event is ControlPadBuilderScreenEvent.OnBackPress)
                onBackPress?.invoke()

        }
    )

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
            TextButton(
                onClick = { onItemClick?.invoke(item) },
                content = {
                    Text(text = item.name)
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlPadBuilderScreenContent(
    controlPad: ControlPad,
    uiState: ControlPadBuilderScreenState,
    onUiEvent: (ControlPadBuilderScreenEvent) -> Unit

) {
    Scaffold(
        bottomBar = {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = {
                            onUiEvent(ControlPadBuilderScreenEvent.OnAddItemClick)
                        },
                        content = {
                            Icon(
                                modifier = Modifier.clickable {
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
                            .padding(10.dp)
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
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(controlPad.backgroundColor.toULong()))
                .padding(innerPadding)
                .clipToBounds()
        ) {


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
                else if(controlPadItem.itemType == ItemType.CLICK_BUTTON && uiState.transformableStatesMap[controlPadItem.id] != null){

                    ControlPadClickButton(
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
                                ItemType.CLICK_BUTTON -> ButtonProperties(
                                    buttonColor = primary.value,
                                    textColor = onPrimary.value,
                                    iconColor = onPrimary.value,
                                ).toJson()
                                else -> TODO("Not Yet Implemented")
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


@Preview(showBackground = true,uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ControlPadBuilderScreenContentInteractiveXPreview() {
    val uiState = remember {
        mutableStateOf(
            ControlPadBuilderScreenState(
                controlPadItems = mutableStateListOf(),
                transformableStatesMap = SnapshotStateMap()
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

