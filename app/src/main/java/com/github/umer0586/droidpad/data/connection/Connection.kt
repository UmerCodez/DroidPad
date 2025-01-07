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

package com.github.umer0586.droidpad.data.connection

import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class ConnectionState{
    NONE,
    TCP_CONNECTING,TCP_CONNECTED,TCP_DISCONNECTING,TCP_DISCONNECTED,TCP_CONNECTION_FAILED,TCP_ERROR,TCP_CONNECTION_TIMEOUT,
    UDP_ERROR,
    WEBSOCKET_CONNECTING,WEBSOCKET_CONNECTED, WEBSOCKET_DISCONNECTED,WEBSOCKET_CONNECTION_FAILED,
    WEBSOCKET_ERROR, WEBSOCKET_CONNECTION_TIMEOUT,

    MQTT_CONNECTING,MQTT_CONNECTED, MQTT_DISCONNECTED,MQTT_CONNECTION_FAILED,
    MQTT_SEND_FAILED,MQTT_CONNECTION_TIMEOUT,MQTT_ERROR,
    BLUETOOTH_ADVERTISING,BLUETOOTH_CLIENT_CONNECTED,BLUETOOTH_CLIENT_DISCONNECTED,
    BLUETOOTH_ADVERTISEMENT_FAILED, BLUETOOTH_ADVERTISEMENT_SUCCESS,BLUETOOTH_ADVERTISEMENT_NOT_SUPPORTED,
    BLUETOOTH_ADVERTISER_NOT_FOUND, BLUETOOTH_GATT_SERVER_CLOSED, BLUETOOTH_DATA_SENT_ERROR, BLUETOOTH_GATT_SERVER_OPENED,
    BLUETOOTH_PERMISSION_REQUIRED

}

abstract class Connection {
    private val _connectionState = MutableStateFlow(ConnectionState.NONE)
    val connectionState = _connectionState.asSharedFlow()

    protected fun notifyConnectionState(newState: ConnectionState) {
        _connectionState.value = newState
    }

    abstract val connectionType: ConnectionType
    abstract suspend fun setup()
    abstract suspend fun sendData(data: String)
    abstract suspend fun tearDown()

}