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

package com.github.umer0586.droidpad.ui.screens.connectionconfigscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.droidpad.data.connectionconfig.BluetoothConfig
import com.github.umer0586.droidpad.data.connectionconfig.BluetoothLEConfig
import com.github.umer0586.droidpad.data.connectionconfig.MqttConfig
import com.github.umer0586.droidpad.data.connectionconfig.RemoteBluetoothDevice
import com.github.umer0586.droidpad.data.connectionconfig.TCPConfig
import com.github.umer0586.droidpad.data.connectionconfig.UDPConfig
import com.github.umer0586.droidpad.data.connectionconfig.UUID_SSP
import com.github.umer0586.droidpad.data.connectionconfig.WebsocketConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import com.github.umer0586.droidpad.data.repositories.ConnectionConfigRepository
import com.github.umer0586.droidpad.data.util.BluetoothUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ConnectionConfigScreenState(
    val connectionType: ConnectionType = ConnectionType.TCP,
    val isHostNameValid: Boolean = true,
    val isPortNoValid: Boolean = true,
    val host: String = "127.0.0.1",
    val port: Int = 8080,
    val clientId: String = "DroidPad",
    val topic: String = "DroidPad/Events",
    val hasInputError: Boolean = false,
    val useCredentials: Boolean = false,
    val username: String = "",
    val password: String = "",
    val connectionTimeout: Int = 5,
    val useSSL: Boolean = false,
    val useWebsocket: Boolean = false,
    val qos: Int = 0,
    val isBluetoothEnable: Boolean = false,
    val bluetoothServiceUUID: String = UUID_SSP,
    val selectedBluetoothDevice: RemoteBluetoothDevice? = null,
    val pairedBluetoothDevices: List<RemoteBluetoothDevice> = emptyList()
)

sealed interface ConnectionConfigScreenEvent {
    data class OnConnectionTypeChange(val connectionType: ConnectionType) :
        ConnectionConfigScreenEvent

    data class OnHostChange(val host: String) : ConnectionConfigScreenEvent
    data class OnPortChange(val portNo: String) : ConnectionConfigScreenEvent
    data class OnClientIdChange(val clientId: String) : ConnectionConfigScreenEvent
    data class OnTopicChange(val topic: String) : ConnectionConfigScreenEvent
    data class OnUsernameChange(val username: String) : ConnectionConfigScreenEvent
    data class OnPasswordChange(val password: String) : ConnectionConfigScreenEvent
    data class OnConnectionTimeoutChange(val connectionTimeout: Int) : ConnectionConfigScreenEvent
    data class OnSaveClick(val controlPadId: Long) : ConnectionConfigScreenEvent
    data class OnQosChange(val qos: Int) : ConnectionConfigScreenEvent
    data class OnUseSSLChange(val sslEnabled: Boolean) : ConnectionConfigScreenEvent
    data class OnUseWebsocketChange(val websocketEnabled: Boolean) : ConnectionConfigScreenEvent
    data class OnUseCredentialChange(val useCredentials: Boolean) : ConnectionConfigScreenEvent
    data class OnBluetoothUUIDChange(val uuid: String) : ConnectionConfigScreenEvent
    data class OnBluetoothDeviceSelected(val remoteBluetoothDevice: RemoteBluetoothDevice) : ConnectionConfigScreenEvent
    data object OnSelectDeviceClick : ConnectionConfigScreenEvent
    data object OnBackPress : ConnectionConfigScreenEvent

}

@HiltViewModel
class ConnectionConfigScreenViewModel @Inject constructor(
    private val connectionConfigRepository: ConnectionConfigRepository,
    private val bluetoothUtil: BluetoothUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectionConfigScreenState())
    val uiState = _uiState.asStateFlow()

    init {

        viewModelScope.launch {

            _uiState.collect { uiState ->

                // Host and portNo are common in all connection types
                // TODO: update the login when BLE connection is introduced
                _uiState.update {
                    it.copy(hasInputError = !uiState.isPortNoValid || uiState.host.isEmpty())
                }

                if (uiState.connectionType == ConnectionType.MQTT_V5 || uiState.connectionType == ConnectionType.MQTT_V3) {
                    _uiState.update {
                        it.copy(
                            hasInputError = uiState.clientId.isEmpty()
                                    || uiState.topic.isEmpty() || uiState.topic.contains(Regex("\\s+"))
                                    || (uiState.username.isEmpty() && uiState.useCredentials) || (uiState.password.isEmpty() && uiState.useCredentials)
                        )
                    }

                } else if (uiState.connectionType == ConnectionType.BLUETOOTH) {
                    _uiState.update {
                        it.copy(
                            hasInputError = uiState.selectedBluetoothDevice == null || uiState.bluetoothServiceUUID.isEmpty()
                        )
                    }
                }
            }
        }
    }


    private var _onConfigSaved: (() -> Unit)? = null
    fun onConfigSaved(callback: () -> Unit) {
        _onConfigSaved = callback
    }

    fun loadConnectionConfigFor(controlPadId: Long) {
        viewModelScope.launch {
            connectionConfigRepository.getConfigForControlPad(controlPadId)
                ?.also { config ->
                    when (config.connectionType) {
                        ConnectionType.TCP -> {
                            val tcpConfig = TCPConfig.fromJson(config.configJson)
                            _uiState.update {
                                it.copy(
                                    connectionType = config.connectionType,
                                    host = tcpConfig.host,
                                    port = tcpConfig.port,
                                    connectionTimeout = tcpConfig.timeoutSecs
                                )
                            }

                        }

                        ConnectionType.UDP -> {
                            val udpConfig = UDPConfig.fromJson(config.configJson)
                            _uiState.update {
                                it.copy(
                                    connectionType = config.connectionType,
                                    host = udpConfig.host,
                                    port = udpConfig.port
                                )
                            }
                        }

                        ConnectionType.WEBSOCKET -> {
                            val websocketConfig = WebsocketConfig.fromJson(config.configJson)
                            _uiState.update {
                                it.copy(
                                    connectionType = config.connectionType,
                                    host = websocketConfig.host,
                                    port = websocketConfig.port,
                                    connectionTimeout = websocketConfig.connectionTimeoutSecs
                                )
                            }
                        }

                        ConnectionType.MQTT_V5 -> {
                            val mqttConfig = MqttConfig.fromJson(config.configJson)
                            _uiState.update {
                                it.copy(
                                    connectionType = config.connectionType,
                                    host = mqttConfig.brokerIp,
                                    port = mqttConfig.brokerPort,
                                    clientId = mqttConfig.clientId,
                                    topic = mqttConfig.topic,
                                    useCredentials = mqttConfig.useCredentials,
                                    useSSL = mqttConfig.useSSL,
                                    username = mqttConfig.userName,
                                    password = mqttConfig.password,
                                    connectionTimeout = mqttConfig.connectionTimeoutSecs,
                                    qos = mqttConfig.qos,
                                    useWebsocket = mqttConfig.useWebsocket
                                )
                            }

                        }

                        ConnectionType.MQTT_V3 -> {
                            val mqttConfig = MqttConfig.fromJson(config.configJson)
                            _uiState.update {
                                it.copy(
                                    connectionType = config.connectionType,
                                    host = mqttConfig.brokerIp,
                                    port = mqttConfig.brokerPort,
                                    clientId = mqttConfig.clientId,
                                    topic = mqttConfig.topic,
                                    useCredentials = mqttConfig.useCredentials,
                                    useSSL = mqttConfig.useSSL,
                                    username = mqttConfig.userName,
                                    password = mqttConfig.password,
                                    connectionTimeout = mqttConfig.connectionTimeoutSecs,
                                    qos = mqttConfig.qos,
                                    useWebsocket = mqttConfig.useWebsocket
                                )
                            }

                        }

                        ConnectionType.BLUETOOTH_LE -> {
                            _uiState.update {
                                it.copy(
                                    connectionType = config.connectionType
                                )
                            }
                        }

                        ConnectionType.BLUETOOTH -> {
                            val bluetoothConfig = BluetoothConfig.fromJson(config.configJson)
                            _uiState.update {
                                it.copy(
                                    connectionType = config.connectionType,
                                    bluetoothServiceUUID = bluetoothConfig.serviceUUID,
                                    selectedBluetoothDevice = bluetoothConfig.remoteDevice,
                                    pairedBluetoothDevices = bluetoothUtil.getPairedDevices()
                                )
                            }
                        }
                    }


                }
        }

    }

    fun onEvent(event: ConnectionConfigScreenEvent) {
        when (event) {
            is ConnectionConfigScreenEvent.OnConnectionTypeChange -> {
                _uiState.update { it.copy(connectionType = event.connectionType) }
            }

            is ConnectionConfigScreenEvent.OnHostChange -> {
                _uiState.update { it.copy(host = event.host) }
            }

            is ConnectionConfigScreenEvent.OnPortChange -> {

                event.portNo.toIntOrNull()?.let { portNo ->

                    _uiState.update {
                        it.copy(port = portNo)
                    }

                    if (portNo !in 0..65534)
                        _uiState.update { it.copy(isPortNoValid = false) }
                    else {
                        _uiState.update { it.copy(isPortNoValid = true) }
                    }
                } ?: _uiState.update { it.copy(isPortNoValid = false) }

            }

            is ConnectionConfigScreenEvent.OnQosChange -> {
                if (event.qos in 0..2) {
                    _uiState.update { it.copy(qos = event.qos) }
                }
            }

            is ConnectionConfigScreenEvent.OnSaveClick -> {
                saveConfig(event.controlPadId)
            }

            ConnectionConfigScreenEvent.OnBackPress -> {}
            is ConnectionConfigScreenEvent.OnClientIdChange -> {
                _uiState.update { it.copy(clientId = event.clientId) }
            }

            is ConnectionConfigScreenEvent.OnConnectionTimeoutChange -> {
                _uiState.update { it.copy(connectionTimeout = event.connectionTimeout) }
            }

            is ConnectionConfigScreenEvent.OnPasswordChange -> {
                _uiState.update { it.copy(password = event.password) }
            }

            is ConnectionConfigScreenEvent.OnTopicChange -> {
                _uiState.update { it.copy(topic = event.topic) }
            }

            is ConnectionConfigScreenEvent.OnUsernameChange -> {
                _uiState.update { it.copy(username = event.username) }
            }

            is ConnectionConfigScreenEvent.OnUseCredentialChange -> {
                _uiState.update { it.copy(useCredentials = event.useCredentials) }
            }

            is ConnectionConfigScreenEvent.OnUseSSLChange -> {
                _uiState.update { it.copy(useSSL = event.sslEnabled) }
            }

            is ConnectionConfigScreenEvent.OnUseWebsocketChange -> {
                _uiState.update { it.copy(useWebsocket = event.websocketEnabled) }
            }

            is ConnectionConfigScreenEvent.OnBluetoothUUIDChange -> {
                _uiState.update { it.copy(bluetoothServiceUUID = event.uuid) }
            }

            is ConnectionConfigScreenEvent.OnBluetoothDeviceSelected -> {
                _uiState.update { it.copy(selectedBluetoothDevice = event.remoteBluetoothDevice) }
            }

            is ConnectionConfigScreenEvent.OnSelectDeviceClick -> {
                _uiState.update {
                    it.copy(
                        pairedBluetoothDevices = bluetoothUtil.getPairedDevices(),
                        isBluetoothEnable = bluetoothUtil.isBluetoothEnabled()
                    )
                }
            }
        }
    }


    private fun saveConfig(controlPadId: Long) {

        val configJson = when (uiState.value.connectionType) {
            ConnectionType.TCP -> TCPConfig(
                host = uiState.value.host,
                port = uiState.value.port,
                timeoutSecs = uiState.value.connectionTimeout
            ).toJson()

            ConnectionType.UDP -> {
                UDPConfig(
                    host = uiState.value.host,
                    port = uiState.value.port
                ).toJson()
            }

            ConnectionType.MQTT_V5 -> {
                MqttConfig(
                    brokerIp = uiState.value.host,
                    brokerPort = uiState.value.port,
                    clientId = uiState.value.clientId,
                    topic = uiState.value.topic,
                    useCredentials = uiState.value.useCredentials,
                    userName = uiState.value.username,
                    password = uiState.value.password,
                    connectionTimeoutSecs = uiState.value.connectionTimeout,
                    qos = uiState.value.qos,
                    useSSL = uiState.value.useSSL,
                    useWebsocket = uiState.value.useWebsocket
                ).toJson()
            }

            ConnectionType.MQTT_V3 -> {
                MqttConfig(
                    brokerIp = uiState.value.host,
                    brokerPort = uiState.value.port,
                    clientId = uiState.value.clientId,
                    topic = uiState.value.topic,
                    useCredentials = uiState.value.useCredentials,
                    userName = uiState.value.username,
                    password = uiState.value.password,
                    connectionTimeoutSecs = uiState.value.connectionTimeout,
                    qos = uiState.value.qos,
                    useSSL = uiState.value.useSSL,
                    useWebsocket = uiState.value.useWebsocket
                ).toJson()
            }

            ConnectionType.WEBSOCKET -> {
                WebsocketConfig(
                    host = uiState.value.host,
                    port = uiState.value.port,
                    connectionTimeoutSecs = uiState.value.connectionTimeout
                ).toJson()
            }

            ConnectionType.BLUETOOTH_LE -> {
                BluetoothLEConfig(
                    serviceUUID = "4fbfc1d7-f509-44ab-afe1-62ea40a4b111",
                    characteristicUUID = "dc3f5274-33ba-48de-8246-43bf8985b323"
                ).toJson()
            }

            ConnectionType.BLUETOOTH -> {
                BluetoothConfig(
                    serviceUUID = uiState.value.bluetoothServiceUUID,
                    remoteDevice = uiState.value.selectedBluetoothDevice
                ).toJson()
            }
        }

        viewModelScope.launch {

            connectionConfigRepository.update(
                controlPadId = controlPadId,
                connectionType = _uiState.value.connectionType,
                configJson = configJson
            )
            _onConfigSaved?.invoke()

        }
    }


}