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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.net.SocketTimeoutException

class Mqttv3Connection(
    val mqttConfig: MqttConfig,
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
) : Connection(), MqttCallback {

    private var mqttAsyncClient: MqttAsyncClient? = null
    private var memoryPersistence: MemoryPersistence? = null
    private var connectionOptions: MqttConnectOptions? = null

    override val connectionType: ConnectionType
        get() = ConnectionType.MQTT_V3

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
        connectionOptions = MqttConnectOptions()

        try {


            notifyConnectionState(ConnectionState.MQTT_CONNECTING)

            mqttAsyncClient = MqttAsyncClient(broker,mqttConfig.clientId, memoryPersistence)


            connectionOptions?.apply {
                isAutomaticReconnect = false
                isCleanSession = false
                connectionTimeout = mqttConfig.connectionTimeoutSecs

                if(mqttConfig.useCredentials){
                    userName = mqttConfig.userName
                    password = mqttConfig.password.toCharArray()
                }

            }

            mqttAsyncClient?.setCallback(this@Mqttv3Connection)
            mqttAsyncClient?.connect(connectionOptions,null,object: IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    println(asyncActionToken)
                    notifyConnectionState(ConnectionState.MQTT_CONNECTED)
                    mqttAsyncClient?.subscribe("DroidPad/feed",0)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    when {
                        exception is SocketTimeoutException -> {
                            notifyConnectionState(ConnectionState.MQTT_CONNECTION_TIMEOUT)
                        }
                        exception is MqttException && exception.reasonCode.toShort() == MqttException.REASON_CODE_FAILED_AUTHENTICATION -> {
                            notifyConnectionState(ConnectionState.MQTT_AUTH_FAILED)
                        }
                        else -> {
                            notifyConnectionState(ConnectionState.MQTT_ERROR)
                        }
                    }
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


    override fun connectionLost(cause: Throwable?) {
        notifyConnectionState(ConnectionState.MQTT_DISCONNECTED)
        cause?.printStackTrace()
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        scope.launch {
            topic?.also { topic ->
                if (topic == "DroidPad/feed") {
                    message?.also {
                        notifyReceivedData(it.payload.decodeToString())
                    }
                }
            }
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {

    }


}