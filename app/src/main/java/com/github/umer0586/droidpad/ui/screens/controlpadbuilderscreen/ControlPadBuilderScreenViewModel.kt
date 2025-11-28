/*
 *     This file is a part of DroidPad (https://www.github.com/UmerCodez/DroidPad)
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

import android.util.Log
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.droidpad.data.Resolution
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.data.database.entities.offset
import com.github.umer0586.droidpad.data.repositories.ControlPadItemRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadRepository
import com.github.umer0586.droidpad.data.repositories.PreferenceRepository
import com.github.umer0586.droidpad.data.repositories.updatePreference
import com.github.umer0586.droidpad.ui.components.rotateBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt


data class ControlPadBuilderScreenState(
    val controlPadItems: SnapshotStateList<ControlPadItem> = SnapshotStateList(),
    val showItemChooser: Boolean = false,
    val showItemEditor: Boolean = false,
    val itemToBeEdited: ControlPadItem? = null,
    val transformableStatesMap: SnapshotStateMap<Long, TransformableState> = SnapshotStateMap(),
    val isModified: Boolean = false,
    val showEditorAids: Boolean = false,
    val useAngleSnap: Boolean = false,
    val angleSnapDivision:Int = 36,
    )

sealed interface ControlPadBuilderScreenEvent {
    data object OnAddItemClick : ControlPadBuilderScreenEvent
    data class OnDeleteItemClick(val controlPadItem: ControlPadItem, val controlPad: ControlPad) :  ControlPadBuilderScreenEvent
    data class OnItemTypeSelected(val itemType: ItemType, val controlPad: ControlPad, val properties: String) :  ControlPadBuilderScreenEvent
    data class OnEditItemClick(val controlPadItem: ControlPadItem, val controlPad: ControlPad) :  ControlPadBuilderScreenEvent
    data class OnItemEditSubmit(val controlPadItem: ControlPadItem, val controlPad: ControlPad) : ControlPadBuilderScreenEvent
    data object OnItemChooserDismissRequest : ControlPadBuilderScreenEvent
    data object OnItemEditorDismissRequest : ControlPadBuilderScreenEvent
    data object OnSaveClick: ControlPadBuilderScreenEvent
    data object OnBackPress: ControlPadBuilderScreenEvent
    data class OnResolutionReported(val controlPad: ControlPad, val builderScreenResolution: Resolution, val tempOpen : Boolean = false) : ControlPadBuilderScreenEvent
    data object OnTempOpenCompleted : ControlPadBuilderScreenEvent
    data object OnEditorAidsClick: ControlPadBuilderScreenEvent
    data object OnEditorAidsDismissRequest: ControlPadBuilderScreenEvent
    data object OnUseAngleSnapChange: ControlPadBuilderScreenEvent
    data class OnAngleSnapChange(val newValue:Float) : ControlPadBuilderScreenEvent

}

@HiltViewModel
class ControlPadBuilderScreenViewModel @Inject constructor(
    private val controlPadRepository: ControlPadRepository,
    private val controlPadItemRepository: ControlPadItemRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val tag = ControlPadBuilderScreenViewModel::class.simpleName
    private val _uiState = MutableStateFlow(ControlPadBuilderScreenState())
    val uiState = _uiState.asStateFlow()

    private val minScale = 0.5f
    private val maxScale = 6f

    init {
        Log.d(tag,"init ${hashCode()}")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(tag,"onCleared() ${hashCode()}")
    }

    fun snappedRotation(input: Float):Float{
        return input.div(360).times(_uiState.value.angleSnapDivision)// Scale the input into [-angleSnapDivision,angleSnapDivision]
            .roundToInt()// Snap!
            .times(360).toFloat().div(_uiState.value.angleSnapDivision)// Scale back to normal and convert
    }
    fun loadControlPadItemsFor(controlPad: ControlPad){
        _uiState.update {
            it.copy(isModified = false)
        }
        var temporalRotation: Float
        viewModelScope.launch {
            Log.d(tag, "loadControlPadItemsFor: ")
            controlPadRepository.getControlPadItemsOf(controlPad).also{ items ->
                Log.d(tag, items.toString())
                _uiState.value.controlPadItems.clear()
                items.forEach { item ->
                    var temporalRotation: Float =item.rotation// I have little understanding on kotlin, thanks closure isolation

                    uiState.value.controlPadItems.add(item)
                    uiState.value.transformableStatesMap[item.id] =
                        TransformableState { zoomChange, offsetChange, rotationChange ->

                            val index = uiState.value.controlPadItems.indexOfFirst { it.id == item.id }

                            val controlPadItem = uiState.value.controlPadItems[index]

                            val newScale = controlPadItem.scale * zoomChange

                            val newRotation = if(controlPadItem.itemType == ItemType.JOYSTICK || controlPadItem.itemType == ItemType.STEERING_WHEEL) 0f
                                else if (_uiState.value.useAngleSnap){ temporalRotation + rotationChange }
                                else{ controlPadItem.rotation + rotationChange }

                            temporalRotation = newRotation
                            
                            val snappedNewRotation = snappedRotation(newRotation)

                            val newOffset = controlPadItem.offset + offsetChange.rotateBy(
                                if (rotationChange == 0f) controlPadItem.rotation
                                else if (_uiState.value.useAngleSnap) snappedNewRotation
                                else newRotation
                            ) * newScale

                            uiState.value.controlPadItems[index] =
                                controlPadItem.copy(
                                    offsetX = newOffset.x,
                                    offsetY = newOffset.y,
                                    // Joystick and steering wheel should not be rotatable
                                    rotation = if (rotationChange == 0f) controlPadItem.rotation
                                            else if (_uiState.value.useAngleSnap) snappedNewRotation
                                            else newRotation,
                                    scale = newScale.coerceIn(minScale,maxScale)
                                )

                            // The if statement prevents the state from being updated if it is already modified.
                            // This can help avoid triggering unnecessary recompositions.
                            if (_uiState.value.isModified != true) {
                                _uiState.update {
                                    it.copy(isModified = true)
                                }
                            }

                        }
                }

            }
        }
    }

    fun onEvent(event: ControlPadBuilderScreenEvent){
        when(event){
            ControlPadBuilderScreenEvent.OnAddItemClick -> {
                _uiState.update {
                    it.copy(showItemChooser = true)
                }
            }
            is ControlPadBuilderScreenEvent.OnDeleteItemClick -> {
                viewModelScope.launch {
                    controlPadItemRepository.delete(event.controlPadItem)
                    _uiState.value.controlPadItems.remove(event.controlPadItem)
                }
            }
            // When user click "Edit" icon on the item
            is ControlPadBuilderScreenEvent.OnEditItemClick -> {
                _uiState.update {
                    it.copy(
                        showItemEditor = true,
                        itemToBeEdited = event.controlPadItem
                    )
                }
            }
            ControlPadBuilderScreenEvent.OnItemChooserDismissRequest -> {
                _uiState.update {
                    it.copy(showItemChooser = false)
                }
            }
            is ControlPadBuilderScreenEvent.OnItemEditSubmit -> {

                _uiState.update { it.copy(showItemEditor = false) }

                viewModelScope.launch {
                    // save changes to database
                    controlPadItemRepository.update(event.controlPadItem)

                    // reflect changes in state
                    val index = uiState.value.controlPadItems.indexOfFirst { it.id == event.controlPadItem.id }
                    val controlPadItem = uiState.value.controlPadItems[index]
                    uiState.value.controlPadItems[index] = controlPadItem.copy(
                        itemIdentifier = event.controlPadItem.itemIdentifier,
                        properties = event.controlPadItem.properties
                    )


                }
            }
            ControlPadBuilderScreenEvent.OnItemEditorDismissRequest -> {
                _uiState.update {
                    it.copy(showItemEditor = false)
                }
            }
            is ControlPadBuilderScreenEvent.OnItemTypeSelected -> {
                _uiState.update { it.copy(showItemChooser = false)  }

                val newItem = ControlPadItem(
                    itemIdentifier = event.itemType.name.lowercase(),
                    controlPadId = event.controlPad.id,
                    itemType = event.itemType,
                    properties = event.properties
                )
                viewModelScope.launch {
                    var temporalRotation: Float =newItem.rotation
                    controlPadItemRepository.save(newItem).also { newId ->
                        controlPadItemRepository.getById(newId).also { newItem ->

                            uiState.value.controlPadItems.add(newItem!!)

                            uiState.value.transformableStatesMap[newItem.id] =
                                TransformableState { zoomChange, offsetChange, rotationChange ->
                                    val index = uiState.value.controlPadItems.indexOfFirst { it.id == newItem.id }
                                    val controlPadItem = uiState.value.controlPadItems[index]

                                    val newScale = controlPadItem.scale * zoomChange

                                    val newRotation = if(controlPadItem.itemType == ItemType.JOYSTICK || controlPadItem.itemType == ItemType.STEERING_WHEEL) 0f
                                    else if (_uiState.value.useAngleSnap){ temporalRotation + rotationChange }
                                    else{ controlPadItem.rotation + rotationChange }

                                    temporalRotation = newRotation

                                    val snappedNewRotation = snappedRotation(newRotation)

                                    val newOffset = controlPadItem.offset + offsetChange.rotateBy(
                                        if (rotationChange == 0f) controlPadItem.rotation
                                        else if (_uiState.value.useAngleSnap) snappedNewRotation
                                        else newRotation
                                    ) * newScale

                                    uiState.value.controlPadItems[index] =
                                        controlPadItem.copy(
                                            offsetX = newOffset.x,
                                            offsetY = newOffset.y,
                                            // Joystick and steering wheel should not be rotatable
                                            rotation = if (rotationChange == 0f) controlPadItem.rotation
                                            else if (_uiState.value.useAngleSnap) snappedNewRotation
                                            else newRotation,
                                            scale = newScale.coerceIn(minScale,maxScale)
                                        )

                                    // The if statement prevents the state from being updated if it is already modified.
                                    // This can help avoid triggering unnecessary recompositions.
                                    if (_uiState.value.isModified != true) {
                                        _uiState.update {
                                            it.copy(isModified = true)
                                        }
                                    }

                                }
                        }
                    }

                }


            }

            is ControlPadBuilderScreenEvent.OnSaveClick -> {
                viewModelScope.launch {
                    _uiState.value.controlPadItems.forEach {
                        controlPadItemRepository.update(it)
                    }
                }

            }

            is ControlPadBuilderScreenEvent.OnResolutionReported -> {

                saveResolution(
                    controlPad = event.controlPad,
                    builderScreenResolution = event.builderScreenResolution,
                    tempOpen = event.tempOpen
                )

            }

            is ControlPadBuilderScreenEvent.OnEditorAidsClick -> {
                _uiState.update {
                    it.copy(showEditorAids = true)
                }
            }
            is ControlPadBuilderScreenEvent.OnEditorAidsDismissRequest -> {
                _uiState.update {
                    it.copy(showEditorAids = false)
                }
            }
            is ControlPadBuilderScreenEvent.OnUseAngleSnapChange ->{
                _uiState.update {
                    it.copy(useAngleSnap = !it.useAngleSnap)
                }
            }
            is ControlPadBuilderScreenEvent.OnAngleSnapChange -> {
                _uiState.update {
                    it.copy(angleSnapDivision = event.newValue.toInt())
                }
            }


            ControlPadBuilderScreenEvent.OnBackPress -> {}
            ControlPadBuilderScreenEvent.OnTempOpenCompleted -> {}
        }
    }

    private fun saveResolution(controlPad: ControlPad, builderScreenResolution: Resolution, tempOpen: Boolean){

        if(!tempOpen) {
            viewModelScope.launch {
                controlPadRepository.updateControlPad(
                    controlPad.copy(
                        width = builderScreenResolution.width,
                        height = builderScreenResolution.height
                    )
                )
            }
        }

        viewModelScope.launch {

            if (controlPad.orientation == Orientation.PORTRAIT) {
                preferenceRepository.updatePreference { pref ->
                    pref.copy(
                        builderScreenPortraitResolution = builderScreenResolution,
                    )
                }
            }

            if(controlPad.orientation == Orientation.LANDSCAPE) {
                preferenceRepository.updatePreference { pref ->
                    pref.copy(
                        builderScreenLandscapeResolution = builderScreenResolution,
                    )
                }
            }
        }
    }

}