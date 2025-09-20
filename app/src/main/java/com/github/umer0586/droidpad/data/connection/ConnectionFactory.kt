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

package com.github.umer0586.droidpad.data.connection

import android.content.Context
import com.github.umer0586.droidpad.data.connectionconfig.BluetoothConfig
import com.github.umer0586.droidpad.data.connectionconfig.BluetoothLEConfig
import com.github.umer0586.droidpad.data.connectionconfig.MqttConfig
import com.github.umer0586.droidpad.data.connectionconfig.TCPConfig
import com.github.umer0586.droidpad.data.connectionconfig.UDPConfig
import com.github.umer0586.droidpad.data.connectionconfig.WebsocketConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import kotlinx.coroutines.CoroutineScope


interface ConnectionFactory {
    fun getConnection(connectionConfig: ConnectionConfig, scope : CoroutineScope) : Connection
}

class ConnectionFactoryImpl(private val appContext: Context) : ConnectionFactory {

    override fun getConnection(connectionConfig: ConnectionConfig, scope : CoroutineScope) =
        when(connectionConfig.connectionType) {
            ConnectionType.TCP -> TCPConnection(TCPConfig.fromJson(connectionConfig.configJson), scope = scope)
            ConnectionType.UDP -> UDPConnection(UDPConfig.fromJson(connectionConfig.configJson))
            ConnectionType.WEBSOCKET -> WebsocketConnection(WebsocketConfig.fromJson(connectionConfig.configJson), scope = scope)
            ConnectionType.MQTT_V5 -> Mqttv5Connection(MqttConfig.fromJson(connectionConfig.configJson))
            ConnectionType.MQTT_V3 -> Mqttv3Connection(MqttConfig.fromJson(connectionConfig.configJson))
            ConnectionType.BLUETOOTH_LE -> BluetoothLEConnection(context = appContext , config = BluetoothLEConfig.fromJson(connectionConfig.configJson))
            ConnectionType.BLUETOOTH -> BluetoothConnection(context = appContext , bluetoothConfig = BluetoothConfig.fromJson(connectionConfig.configJson))
        }

}

