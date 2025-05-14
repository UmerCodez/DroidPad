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

package com.github.umer0586.droidpad.ui.screens.sensorsscreen

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadSensor
import com.github.umer0586.droidpad.data.repositories.ControlPadSensorRepository
import com.github.umer0586.droidpad.data.sensor.DeviceSensor
import com.github.umer0586.droidpad.data.sensor.SensorManagerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SensorsScreenState(
    val availableSensors : SnapshotStateList<DeviceSensor> = SnapshotStateList(),
    val selectedSensors : SnapshotStateList<DeviceSensor> = SnapshotStateList(),
    val showSensorSelector : Boolean = false
)

sealed interface SensorsScreenEvent{
    data class LoadSelectedSensorsForControlPad(val controlPad: ControlPad): SensorsScreenEvent
    data object OnAddSensorClick : SensorsScreenEvent
    data class OnSensorSelected(val controlPad: ControlPad, val deviceSensor: DeviceSensor) : SensorsScreenEvent
    data object OnSensorSelectorDismissRequest : SensorsScreenEvent
    data object OnBackPress : SensorsScreenEvent
    data class OnSensorDeleteClick(val controlPad: ControlPad, val deviceSensor: DeviceSensor) : SensorsScreenEvent
}

@HiltViewModel
class SensorsScreenViewModel @Inject constructor(
    private val controlPadSensorRepository: ControlPadSensorRepository,
    private val sensorManagerUtil: SensorManagerUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SensorsScreenState()
    )

    val uiState = _uiState.asStateFlow()
    private val tag = javaClass.simpleName

    init {
        Log.d(tag, "init : ${hashCode()}")

        _uiState.value.availableSensors.clear()
        _uiState.value.availableSensors.addAll(
            sensorManagerUtil.getAvailableSensors().filter {
                it.type == sensorManagerUtil.accelerometerType || it.type == sensorManagerUtil.gyroscopeType
            }
        )

    }


    fun onEvent(event: SensorsScreenEvent) {
        when(event){
            // One time event launched by LaunchedEffect
            is SensorsScreenEvent.LoadSelectedSensorsForControlPad -> {
                viewModelScope.launch {
                    controlPadSensorRepository.getControlPadSensors().collect { controlPadSensors ->
                        val sensors = controlPadSensors
                            .filter { it.controlPadId == event.controlPad.id }
                            .map { it.toDeviceSensor() }
                            .toList()

                        _uiState.value.selectedSensors.clear()
                        _uiState.value.selectedSensors.addAll(sensors)
                    }
                }
            }
            SensorsScreenEvent.OnAddSensorClick -> {
                _uiState.update {
                    it.copy(showSensorSelector = true)
                }
            }
            SensorsScreenEvent.OnBackPress -> {}
            is SensorsScreenEvent.OnSensorDeleteClick -> {
                viewModelScope.launch {
                    controlPadSensorRepository.deleteControlPadSensor(event.controlPad.id, event.deviceSensor.type)
                }
            }
            is SensorsScreenEvent.OnSensorSelected -> {
                viewModelScope.launch {
                    controlPadSensorRepository.saveControlPadSensor(
                        ControlPadSensor(
                            controlPadId = event.controlPad.id,
                            sensorType = event.deviceSensor.type
                        )
                    )
                    _uiState.update {
                        it.copy(
                            showSensorSelector =  false
                        )
                    }
                }
            }
            SensorsScreenEvent.OnSensorSelectorDismissRequest -> {
                _uiState.update {
                    it.copy(showSensorSelector = false)
                }
            }
        }
    }

    private fun ControlPadSensor.toDeviceSensor() : DeviceSensor{
        return sensorManagerUtil.getSensor(this.sensorType)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(tag, "onCleared : ${hashCode()}")

    }

}