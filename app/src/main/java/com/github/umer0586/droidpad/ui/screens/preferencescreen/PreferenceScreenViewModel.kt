package com.github.umer0586.droidpad.ui.screens.preferencescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.droidpad.data.repositories.PreferenceRepository
import com.github.umer0586.droidpad.data.repositories.updatePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PreferenceScreenState(
    val jsonForBluetooth: Boolean = false,
    val sensorSamplingRate: Int = 200000,
    val vibrate: Boolean = false
)

sealed interface PreferenceScreenEvent{
    data class OnJsonForBluetoothChange(val jsonForBluetooth: Boolean) : PreferenceScreenEvent
    data class OnSensorSamplingRateChange(val sensorSamplingRate: Int) : PreferenceScreenEvent
    data class OnVibrateChange(val vibrate: Boolean) : PreferenceScreenEvent
    data object OnSensorSamplingRateChangeFinished : PreferenceScreenEvent
    data object OnBackClick : PreferenceScreenEvent
}

@HiltViewModel
class PreferenceScreenViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel(){

    private var _uiState = MutableStateFlow(
        PreferenceScreenState()
    )

    val uiState = _uiState.asStateFlow()

    private val tag = javaClass.simpleName

    init {

        Log.d(tag, "init : ${hashCode()}")

        viewModelScope.launch {
            preferenceRepository.preference.collect{ pref ->

                _uiState.update {
                    it.copy(
                        jsonForBluetooth = pref.sendJsonOverBluetooth,
                        sensorSamplingRate = pref.sensorSamplingRate,
                        vibrate = pref.vibrate
                    )
                }
            }
        }
    }

    fun onEvent(event: PreferenceScreenEvent) {
        when (event) {
            is PreferenceScreenEvent.OnJsonForBluetoothChange -> {
                _uiState.update {
                    it.copy(jsonForBluetooth = event.jsonForBluetooth)
                }
                viewModelScope.launch {
                    preferenceRepository.updatePreference {
                        it.copy(sendJsonOverBluetooth = event.jsonForBluetooth)
                    }
                }
            }
            is PreferenceScreenEvent.OnSensorSamplingRateChange -> {
                _uiState.update {
                    it.copy(sensorSamplingRate = event.sensorSamplingRate)
                }
            }
            is PreferenceScreenEvent.OnSensorSamplingRateChangeFinished -> {
                viewModelScope.launch {
                    preferenceRepository.updatePreference {
                        it.copy(sensorSamplingRate = uiState.value.sensorSamplingRate)
                    }
                }
            }
            is PreferenceScreenEvent.OnVibrateChange -> {
                _uiState.update {
                    it.copy(vibrate = event.vibrate)
                }
                viewModelScope.launch {
                    preferenceRepository.updatePreference {
                        it.copy(vibrate = event.vibrate)
                    }
                }
            }

            PreferenceScreenEvent.OnBackClick -> {}
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(tag, "onCleared : ${hashCode()}")
    }
}