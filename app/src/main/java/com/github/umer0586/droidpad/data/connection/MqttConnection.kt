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

import com.github.umer0586.droidpad.data.connectionconfig.MqttConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.paho.mqttv5.client.IMqttToken
import org.eclipse.paho.mqttv5.client.MqttActionListener
import org.eclipse.paho.mqttv5.client.MqttAsyncClient
import org.eclipse.paho.mqttv5.client.MqttCallback
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence
import org.eclipse.paho.mqttv5.common.MqttException
import org.eclipse.paho.mqttv5.common.MqttMessage
import org.eclipse.paho.mqttv5.common.packet.MqttProperties
import java.net.SocketTimeoutException

class MqttConnection(
    val mqttConfig: MqttConfig,
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Connection(), MqttCallback {

    private var mqttAsyncClient: MqttAsyncClient? = null
    private var memoryPersistence: MemoryPersistence? = null
    private var connectionOptions: MqttConnectionOptions? = null

    override val connectionType: ConnectionType
        get() = ConnectionType.MQTT

    override suspend fun setup() = withContext<Unit>(ioDispatcher) {

        val broker = if (mqttConfig.useSSL) {
            if (mqttConfig.useWebsocket)
                "wss://${mqttConfig.brokerIp}:${mqttConfig.brokerPort}"
            else
                "ssl://${mqttConfig.brokerIp}:${mqttConfig.brokerPort}"
        } else {
            if (mqttConfig.useWebsocket)
                "ws://${mqttConfig.brokerIp}:${mqttConfig.brokerPort}"
            else
                "tcp://${mqttConfig.brokerIp}:${mqttConfig.brokerPort}"
        }
        memoryPersistence = MemoryPersistence()
        connectionOptions = MqttConnectionOptions()

        try {


            notifyConnectionState(ConnectionState.MQTT_CONNECTING)

            mqttAsyncClient = MqttAsyncClient(broker,mqttConfig.clientId, memoryPersistence)


            connectionOptions?.apply {
                isAutomaticReconnect = false
                isCleanStart = false
                connectionTimeout = mqttConfig.connectionTimeoutSecs

                if(mqttConfig.useCredentials){
                    userName = mqttConfig.userName
                    password = mqttConfig.password.toByteArray()
                }

            }

            mqttAsyncClient?.setCallback(this@MqttConnection)
            mqttAsyncClient?.connect(connectionOptions,null,object: MqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    println(asyncActionToken)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {

                    if(exception is SocketTimeoutException)
                        notifyConnectionState(ConnectionState.MQTT_CONNECTION_TIMEOUT)

                    notifyConnectionState(ConnectionState.MQTT_ERROR)
                    exception?.printStackTrace()

                }

            })

        } catch (e: MqttException) {
            e.printStackTrace()
            notifyConnectionState(ConnectionState.MQTT_ERROR)
        }



    }

    override suspend fun sendData(data: String) = withContext<Unit>(ioDispatcher){

        try {

            val message = MqttMessage(data.toByteArray()).apply {
                qos = mqttConfig.qos
            }
            mqttAsyncClient?.publish(mqttConfig.topic,message)

        } catch (e: MqttException) {
            e.printStackTrace()
            notifyConnectionState(ConnectionState.MQTT_ERROR)
        }


    }

    override suspend fun tearDown() = withContext<Unit>(ioDispatcher){
        try {

            mqttAsyncClient?.disconnect()
            mqttAsyncClient?.close()
            notifyConnectionState(ConnectionState.MQTT_DISCONNECTED)
        } catch (e: Exception) {
            e.printStackTrace()
            notifyConnectionState(ConnectionState.MQTT_ERROR)
        }

    }

    override fun disconnected(disconnectResponse: MqttDisconnectResponse?) {
        notifyConnectionState(ConnectionState.MQTT_DISCONNECTED)
        disconnectResponse?.reasonString?.also { println(it) }
    }

    override fun mqttErrorOccurred(exception: MqttException?) {
        notifyConnectionState(ConnectionState.MQTT_ERROR)
        exception?.printStackTrace()
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {

    }

    override fun deliveryComplete(token: IMqttToken?) {

    }

    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
        notifyConnectionState(ConnectionState.MQTT_CONNECTED)
    }

    override fun authPacketArrived(reasonCode: Int, properties: MqttProperties?) {

    }

}