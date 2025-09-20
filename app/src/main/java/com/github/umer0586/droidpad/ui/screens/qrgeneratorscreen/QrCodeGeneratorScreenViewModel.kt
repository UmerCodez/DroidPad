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



package com.github.umer0586.droidpad.ui.screens.qrgeneratorscreen

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.droidpad.data.ExternalData
import com.github.umer0586.droidpad.data.connectionconfig.BluetoothConfig
import com.github.umer0586.droidpad.data.connectionconfig.MqttConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.repositories.ConnectionConfigRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadRepository
import com.github.umer0586.droidpad.data.util.QRCodeGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QRCodeScreenState(
    val creatingQrCode: Boolean = false,
    val qrCodeReady: Boolean = false,
    val qrCodeImage: Bitmap? = null,
    val errorOccurred: Boolean = false
)

sealed interface QRCodeScreenEvent {
    data object OnBackPress : QRCodeScreenEvent
    data class OnGenerateQRCode(val controlPad: ControlPad) : QRCodeScreenEvent
}

@HiltViewModel
class QrCodeScreenViewModel @Inject constructor(
    private val controlPadsRepository: ControlPadRepository,
    private val connectionConfigRepository: ConnectionConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QRCodeScreenState())
    val uiState = _uiState.asStateFlow()

    private val tag = javaClass.simpleName

    init {
        Log.d(tag, "init : ${hashCode()}")
    }

    fun onEvent(event: QRCodeScreenEvent) {
        when (event) {

            is QRCodeScreenEvent.OnGenerateQRCode -> {
                viewModelScope.launch {
                    createQRCodeFor(event.controlPad)
                }
            }

            else -> {}
        }
    }


    private suspend fun createQRCodeFor(controlPad: ControlPad) {

        _uiState.update {
            it.copy(creatingQrCode = true)
        }

        connectionConfigRepository.getConfigForControlPad(controlPad.id)?.also { connectionConfig ->

            val dataToBeExported = ExternalData(
                controlPad = controlPad.copy(id = 0),
                controlPadItems = controlPadsRepository.getControlPadItemsOf(controlPad)
                    .map { it.copy(id = 0) },
                connectionConfig = connectionConfig.copy(id = 0).let {

                    // Don't export password
                    if (it.connectionType == ConnectionType.MQTT_V5 || it.connectionType == ConnectionType.MQTT_V3) {
                        val mqttConfig = MqttConfig.fromJson(it.configJson)
                        val updatedConfigJson = mqttConfig.copy(password = "****").toJson()
                        return@let it.copy(configJson = updatedConfigJson)

                    }
                    else if(it.connectionType == ConnectionType.BLUETOOTH){
                        val bluetoothConfig = BluetoothConfig.fromJson(it.configJson)
                        val updatedConfigJson = bluetoothConfig.copy(remoteDevice = null).toJson()
                        return@let it.copy(configJson = updatedConfigJson)
                    }
                    return@let it
                }
            )

            try {

                val qrCodeImage = QRCodeGenerator.createQrCode(dataToBeExported)

                _uiState.update {
                    it.copy(
                        creatingQrCode = false,
                        qrCodeReady = true,
                        qrCodeImage = qrCodeImage
                    )
                }


            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorOccurred = true,
                        qrCodeReady = false,
                        qrCodeImage = null
                    )
                }
            }

        }

    }

    override fun onCleared() {
        super.onCleared()
        Log.d(tag, "onCleared : ${hashCode()}")
    }


}