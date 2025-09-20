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

import com.github.umer0586.droidpad.data.connectionconfig.WebsocketConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.TimeUnit


class WebsocketConnection(
    val webSocketConfig: WebsocketConfig,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
): Connection(){

    override val connectionType: ConnectionType
        get() = ConnectionType.WEBSOCKET

    private var webSocketClient: WebSocketClient? = null
    private var isConnected = false

    override suspend fun setup() = withContext<Unit>(ioDispatcher) {


        webSocketClient = WsClient(URI("ws://${webSocketConfig.host}:${webSocketConfig.port}"))

        notifyConnectionState(ConnectionState.WEBSOCKET_CONNECTING)

        try {

            webSocketClient?.connectBlocking(webSocketConfig.connectionTimeoutSecs.toLong(), TimeUnit.SECONDS)

        } catch (e: Exception) {
            e.printStackTrace()
            notifyConnectionState(ConnectionState.WEBSOCKET_ERROR)
        }

    }

    override suspend fun sendData(data: String) = withContext<Unit>(ioDispatcher) {
        try {
            webSocketClient?.send(data)
        } catch (e: Exception) {
            e.printStackTrace()
            notifyConnectionState(ConnectionState.WEBSOCKET_ERROR)
        }
    }

    override suspend fun tearDown() = withContext<Unit>(ioDispatcher) {
        try {
            webSocketClient?.close()
        } catch (e: Exception) {
            notifyConnectionState(ConnectionState.WEBSOCKET_ERROR)
        }
    }

    private inner class WsClient(uri: URI) : WebSocketClient(uri) {

        override fun onOpen(handshakedata: ServerHandshake?) {
            notifyConnectionState(ConnectionState.WEBSOCKET_CONNECTED)
            isConnected = true
        }

        override fun onMessage(message: String?) {
            message?.also { msg ->
                scope.launch {
                    notifyReceivedData(msg)
                }
            }
        }

        override fun onClose(code: Int, reason: String?, closeByServer: Boolean) {
            notifyConnectionState(ConnectionState.WEBSOCKET_DISCONNECTED)


            // If this client is not connected to the server and the onClose method is triggered,
            // it indicates that the client failed to establish a connection within the specified time frame.
            if(!closeByServer && !isConnected){
                notifyConnectionState(ConnectionState.WEBSOCKET_CONNECTION_TIMEOUT)
            }

            isConnected = false
        }

        override fun onError(ex: Exception?) {
            notifyConnectionState(ConnectionState.WEBSOCKET_ERROR)
            isConnected = false
        }
    }

}

