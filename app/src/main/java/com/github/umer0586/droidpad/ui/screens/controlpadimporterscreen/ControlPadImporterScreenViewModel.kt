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


package com.github.umer0586.droidpad.ui.screens.controlpadimporterscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.droidpad.data.ExternalData
import com.github.umer0586.droidpad.data.Resolution
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.data.repositories.ConnectionConfigRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadItemRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadRepository
import com.github.umer0586.droidpad.data.repositories.PreferenceRepository
import com.github.umer0586.droidpad.data.repositories.getPreference
import com.github.umer0586.droidpad.data.util.calculateCenteredOffset
import com.github.umer0586.droidpad.data.util.getNewScaleForBuilderScreenOnThisDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ImporterScreenState(
    val importing: Boolean = false,
    val controlPadReady: Boolean = false,
    val importFailed: Boolean = false,
    val controlPad: ControlPad? = null,
    val differentResolutionsDetected: Boolean = false,
)

sealed interface ImporterScreenEvent {
    data class OnExternalDataProvided(val externalData: ExternalData) : ImporterScreenEvent
    data object OnBackPress : ImporterScreenEvent
    data class OnOptionSelected(val externalData: ExternalData, val importOptions: ImportOptions) : ImporterScreenEvent
}

enum class ImportOptions{
    IMPORT_UN_CHANGED,
    IMPORT_ADJUST_POSITION_CENTER,
    IMPORT_ADJUST_POSITION,
    IMPORT_SCALE,
    IMPORT_SCALE_CENTER
}

@HiltViewModel
class ControlPadImporterScreenViewModel @Inject constructor(
    private val controlPadsRepository: ControlPadRepository,
    private val connectionConfigRepository: ConnectionConfigRepository,
    private val controlPadItemsRepository: ControlPadItemRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImporterScreenState())
    val uiState = _uiState.asStateFlow()
    private val tag = javaClass.simpleName

    init {
        Log.d(tag, "init : ${hashCode()}")
    }

    private var _onControlPadReadyCallBack: ((ControlPad) -> Unit)? = null
    fun onControlPadReady(callback:(ControlPad) -> Unit) {
        _onControlPadReadyCallBack = callback
    }

    private var _onBuilderScreenResRequired: ((ExternalData) -> Unit)? = null
    fun onBuilderScreenResRequired(callback:(ExternalData) -> Unit) {
        _onBuilderScreenResRequired = callback
    }

    fun onEvent(event: ImporterScreenEvent) {
        when (event) {
            is ImporterScreenEvent.OnExternalDataProvided -> {
                viewModelScope.launch {
                    analyze(event.externalData)
                }
            }
            is ImporterScreenEvent.OnOptionSelected ->{
                viewModelScope.launch {
                    import(event.externalData, event.importOptions)
                }
            }
            else -> {}
        }
    }

    private suspend fun analyze(externalData: ExternalData) {

        // Resolution of Builder Screen from the device that exported control pad
        val otherBuilderScreenRes = Resolution(
            width = externalData.controlPad.width,
            height = externalData.controlPad.height
        )

        // The Resolution of Builder Screen on this device based on the orientation.
        // This value is zero if the user has never opened the Builder Screen on this device.
        // The resolution is recorded the first time the Builder Screen is opened.
        val thisDeviceBuilderScreenRes = getBuilderScreenResOnThisDevice(externalData.controlPad.orientation)


        if(externalData.controlPad.orientation == Orientation.PORTRAIT) {
            // A zero Builder Screen resolution indicates the user has never opened the Builder Screen.
            if (thisDeviceBuilderScreenRes.isZero()){
                // Trigger the event to temporarily opens the Builder Screen to record
                // its resolution on this device
                _onBuilderScreenResRequired?.invoke(externalData)
                return
            }

            if(otherBuilderScreenRes != thisDeviceBuilderScreenRes){
                _uiState.update {
                    it.copy(differentResolutionsDetected = true)
                }
            } else {
                import(externalData = externalData, ImportOptions.IMPORT_UN_CHANGED)
            }
        }

        else if(externalData.controlPad.orientation == Orientation.LANDSCAPE) {
            if (thisDeviceBuilderScreenRes.isZero()){
                _onBuilderScreenResRequired?.invoke(externalData)
                return
            }
            if(otherBuilderScreenRes != thisDeviceBuilderScreenRes){
                _uiState.update {
                    it.copy(differentResolutionsDetected = true)
                }
            }else {
                import(externalData = externalData, ImportOptions.IMPORT_UN_CHANGED)
            }
        }

    }

    private suspend fun import(externalData: ExternalData, importOptions: ImportOptions) {
        try {

            _uiState.update {
                it.copy(importing = true)
            }

            controlPadsRepository.saveControlPad(externalData.controlPad).also { newControlPadId ->
                externalData.controlPadItems.map{

                    val thisDeviceBuilderScreenRes = getBuilderScreenResOnThisDevice(externalData.controlPad.orientation)
                    val otherDeviceBuilderScreenRes = Resolution(externalData.controlPad.width, externalData.controlPad.height)

                    val newScale = getNewScaleForBuilderScreenOnThisDevice(thisDeviceBuilderScreenRes = thisDeviceBuilderScreenRes, otherDeviceBuilderScreenRes = otherDeviceBuilderScreenRes)

                    when(importOptions){
                        ImportOptions.IMPORT_UN_CHANGED -> it
                        ImportOptions.IMPORT_ADJUST_POSITION -> it.copy( offsetX = it.offsetX*newScale, offsetY = it.offsetY*newScale)
                        ImportOptions.IMPORT_SCALE -> it.copy(scale = it.scale*newScale, offsetX = it.offsetX*newScale, offsetY = it.offsetY*newScale)
                        ImportOptions.IMPORT_ADJUST_POSITION_CENTER-> {
                            val offsetX = calculateCenteredOffset(thisDeviceBuilderScreenRes, otherDeviceBuilderScreenRes).x
                            val offsetY = calculateCenteredOffset(thisDeviceBuilderScreenRes, otherDeviceBuilderScreenRes).y

                            it.copy(offsetX = (it.offsetX+offsetX)*newScale, offsetY = (it.offsetY+offsetY)*newScale)
                        }
                        ImportOptions.IMPORT_SCALE_CENTER -> {

                            val offsetX = calculateCenteredOffset(thisDeviceBuilderScreenRes, otherDeviceBuilderScreenRes).x
                            val offsetY = calculateCenteredOffset(thisDeviceBuilderScreenRes, otherDeviceBuilderScreenRes).y

                            it.copy(scale = it.scale*newScale, offsetX = (it.offsetX+offsetX)*newScale, offsetY = (it.offsetY+offsetY)*newScale)

                        }
                    }


                }.forEach { controlPadItem ->
                    controlPadItemsRepository.save(controlPadItem.copy(controlPadId = newControlPadId))
                }
                connectionConfigRepository.save(externalData.connectionConfig.copy(controlPadId = newControlPadId))

                controlPadsRepository.getControlPadById(newControlPadId)?.also {
                    _onControlPadReadyCallBack?.invoke(it)
                }

                _uiState.update {
                    it.copy(
                        importing = false,
                        controlPadReady = true,
                    )
                }
            }



        } catch (e: Exception) {

            _uiState.update {
                it.copy(
                    importFailed = true,
                    importing = false,
                    controlPadReady = false
                )
            }
        }
    }

    private suspend fun getBuilderScreenResOnThisDevice(orientation: Orientation) : Resolution {
        val settings = preferenceRepository.getPreference()

        return when(orientation){
            Orientation.LANDSCAPE -> settings.builderScreenLandscapeResolution
            Orientation.PORTRAIT -> settings.builderScreenPortraitResolution
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(tag, "onCleared : ${hashCode()}")
    }
}