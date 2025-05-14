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

package com.github.umer0586.droidpad.ui.screens.newcontrolpadscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.droidpad.data.connectionconfig.WebsocketConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.data.repositories.ConnectionConfigRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewControlPadScreenState(
    val controlPadName: String = "MyControlPad",
    val inputError: Boolean = false,
    val controlPadOrientation: Orientation = Orientation.PORTRAIT,
    val creatingControlPad: Boolean = false,
    val controlPadCreated: Boolean = false,
)

sealed interface NewControlPadScreenEvent {
    data class OnControlPadNameChanged(val controlPadName: String) :
        NewControlPadScreenEvent

    data class OnControlPadOrientationChanged(val controlPadOrientation: Orientation) :
        NewControlPadScreenEvent

    data object OnCreateClick : NewControlPadScreenEvent
    data object OnBackPress : NewControlPadScreenEvent

}

@HiltViewModel
class NewControlPadScreenViewModel @Inject constructor(
    private val controlPadRepository: ControlPadRepository,
    private val connectionConfigRepository: ConnectionConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewControlPadScreenState())
    val uiState = _uiState.asStateFlow()

    private val tag = javaClass.simpleName

    init {
        Log.d(tag, "init : ${hashCode()}")
    }

    private var _onControlPadCreated: ((controlPad: ControlPad) -> Unit)? = null
    fun onControlPadCreated(callBack: ((controlPad: ControlPad) -> Unit)? = null) {
        _onControlPadCreated = callBack
    }


    fun onEvent(event: NewControlPadScreenEvent) {
        when (event) {

            is NewControlPadScreenEvent.OnControlPadNameChanged -> {
                _uiState.update {
                    it.copy(
                        controlPadName = event.controlPadName,
                        inputError = event.controlPadName.isEmpty()
                    )
                }
            }

            is NewControlPadScreenEvent.OnControlPadOrientationChanged -> {
                _uiState.update {
                    it.copy(controlPadOrientation = event.controlPadOrientation)
                }
            }

            is NewControlPadScreenEvent.OnCreateClick -> {
                viewModelScope.launch {
                    val id = controlPadRepository.saveControlPad(
                        ControlPad(
                            name = uiState.value.controlPadName,
                            orientation = uiState.value.controlPadOrientation,
                        )
                    )

                    controlPadRepository.getControlPadById(id)?.also { controlPad ->
                        _onControlPadCreated?.invoke(controlPad)

                        // ConnectionType selection is not provided to user while creating new ControlPad
                        // Every newly created Control Pad will have ConnectionType.WEBSOCKET as default
                        // This connectionConfig will be fetched in ConnectionConfigScreen
                        connectionConfigRepository.save(
                            ConnectionConfig(
                                controlPadId = controlPad.id,
                                connectionType = ConnectionType.WEBSOCKET,
                                configJson = WebsocketConfig().toJson()
                            )
                        )

                    }


                }
            }

            NewControlPadScreenEvent.OnBackPress -> {}
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(tag, "onCleared : ${hashCode()}")

    }


}