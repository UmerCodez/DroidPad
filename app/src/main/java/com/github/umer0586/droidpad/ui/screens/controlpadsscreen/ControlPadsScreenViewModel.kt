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

package com.github.umer0586.droidpad.ui.screens.controlpadsscreen

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.repositories.ConnectionConfigRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadItemRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ControlPadsScreenState(
    val controlPads: SnapshotStateList<ControlPad> = SnapshotStateList(),
    val controlPadConnectionTypeMap: SnapshotStateMap<Long, ConnectionType> = SnapshotStateMap(),
    val itemToBeEdited: ControlPad? = null
)

sealed interface ControlPadsScreenEvent {
    data class OnDeleteClick(val controlPad: ControlPad) : ControlPadsScreenEvent
    data class OnPlayClick(val controlPad: ControlPad) : ControlPadsScreenEvent
    data class OnNameUpdate(val controlPad: ControlPad) : ControlPadsScreenEvent
    data class OnEditClick(val controlPad: ControlPad) : ControlPadsScreenEvent
    data class OnBuildClick(val controlPad: ControlPad) : ControlPadsScreenEvent
    data class OnSettingClick(val controlPad: ControlPad) : ControlPadsScreenEvent
    data class OnDuplicateClick(val controlPad: ControlPad) : ControlPadsScreenEvent
    data class OnQrCodeClick(val controlPad: ControlPad) : ControlPadsScreenEvent
    // Indicates that user clicked the "+" floating action button
    data object OnCreateClick : ControlPadsScreenEvent
    data object OnExitClick : ControlPadsScreenEvent
    data object OnShareClick : ControlPadsScreenEvent
    data object OnAboutClick : ControlPadsScreenEvent
    data object OnQRScannerClick : ControlPadsScreenEvent
}



@HiltViewModel
class ControlPadsScreenViewModel @Inject constructor(
    private val controlPadsRepository: ControlPadRepository,
    private val connectionConfigRepository: ConnectionConfigRepository,
    private val controlPadItemRepository: ControlPadItemRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(ControlPadsScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {

            controlPadsRepository.getControlPads().collect { controlPadList ->
                _uiState.value.controlPads.clear()
                _uiState.value.controlPads.addAll(controlPadList)
            }

        }

    }

    // This method should be invoked from the UI (e.g., via LaunchedEffect)
    // to ensure the "ConnectionType" of a control pad is updated correctly
    // when navigating back to ControlPadsScreen from ConnectionConfigScreen.
    // Avoid calling this method in the ViewModel's init block,
    // as the "ConnectionType" label will not reflect changes made in ConnectionConfigScreen
    // upon returning to ControlPadsScreen.
    fun loadConnectionTypes(){
        viewModelScope.launch {
            controlPadsRepository.getControlPads().first().forEach { controlPad ->
                connectionConfigRepository.getConfigForControlPad(controlPad.id)?.also { config ->
                    _uiState.value.controlPadConnectionTypeMap[controlPad.id] = config.connectionType
                }
            }
        }

    }


    fun onEvent(event: ControlPadsScreenEvent){
        when(event){
            is ControlPadsScreenEvent.OnDeleteClick -> {
                viewModelScope.launch {
                    controlPadsRepository.deleteControlPad(event.controlPad)
                }
            }
            is ControlPadsScreenEvent.OnNameUpdate -> {
                viewModelScope.launch {
                    controlPadsRepository.updateControlPad(event.controlPad)
                }
            }
            is ControlPadsScreenEvent.OnEditClick -> {
                _uiState.update {
                    it.copy(itemToBeEdited = event.controlPad)
                }
            }
            is ControlPadsScreenEvent.OnDuplicateClick -> {
                viewModelScope.launch {
                    createAndSaveDuplicate(event.controlPad)
                }
            }
            else -> {

            }
        }
    }

    private suspend fun createAndSaveDuplicate(controlPad: ControlPad){

        val duplicateControlPad = ControlPad(
            name = controlPad.name,
            orientation = controlPad.orientation,
        )

        controlPadsRepository.saveControlPad(duplicateControlPad)
            .also { duplicateControlPadId ->

                controlPadsRepository.getControlPadItemsOf(controlPad)
                    .forEach { controlPadItem ->
                        val duplicateControlPadItem = controlPadItem.copy(
                            id = 0, // // its not zero, it will be auto-generated (unique) when save in DB
                            controlPadId = duplicateControlPadId
                        )
                        controlPadItemRepository.save(duplicateControlPadItem)
                    }

                connectionConfigRepository.getConfigForControlPad(controlPad.id)
                    ?.also { connectionConfig ->
                        connectionConfigRepository.save(
                            connectionConfig.copy(
                                id = 0, // its not zero, it will be auto-generated (unique) when save in DB
                                controlPadId = duplicateControlPadId
                            )
                        )
                        _uiState.value.controlPadConnectionTypeMap[duplicateControlPadId] = connectionConfig.connectionType
                    }


            }
    }
}