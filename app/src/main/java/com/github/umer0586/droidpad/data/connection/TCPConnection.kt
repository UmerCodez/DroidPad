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

import android.util.Log
import com.github.umer0586.droidpad.data.connectionconfig.TCPConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.io.IOException

class TCPConnection(
    val tcpConfig: TCPConfig,
    private val scope: CoroutineScope,
    // Dispatchers should be injected for making testing easier
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : Connection() {

    private var selectorManager: SelectorManager? = null
    private val TAG: String = "TCPConnection"
    private var socket : Socket? = null
    private var writeChannel: ByteWriteChannel ? = null
    private var dataReceivingJob: Job? = null

    // sendData Coroutine write lock
    // Fixes a NullPointerExcept that occurs when using multiple joysticks at once
    private val writeMutex = Mutex()

    override val connectionType: ConnectionType
        get() = ConnectionType.TCP

    override suspend fun setup() = withContext<Unit>(ioDispatcher) {

        notifyConnectionState(ConnectionState.TCP_CONNECTING)
        try {

            selectorManager = SelectorManager(ioDispatcher)

            selectorManager?.also{ selectorManager ->

                // Throws TimeoutCancellationException if timeout
                withTimeout(tcpConfig.timeoutSecs*1000L) {
                    socket = aSocket(selectorManager)
                        .tcp()
                        .connect(tcpConfig.host, tcpConfig.port)
                }

                notifyConnectionState(ConnectionState.TCP_CONNECTED)

                writeChannel = socket?.openWriteChannel(autoFlush = true)

                dataReceivingJob = scope.launch(ioDispatcher) {

                    // TCP sockets are byte streams with no built-in message boundaries.
                    // When the remote peer closes (or half-closes) the connection, the local side detects EOF during a read operation.
                    // A read function (like Ktor's readUTF8Line()) returns null or throws an exception when it hits EOF — this means the server has shut down the connection gracefully, and no more data will arrive.

                    try {
                        val readChannel = socket?.openReadChannel()
                        while (isActive) {
                            try {
                                val data = readChannel?.readUTF8Line() ?: break // EOF
                                notifyReceivedData(data)
                            } catch (e: Throwable) {
                                if (isActive) {
                                    Log.e(TAG, "Error reading from socket", e)
                                    e.printStackTrace()
                                    notifyConnectionState(ConnectionState.TCP_ERROR)
                                }
                                break
                            }
                        }
                    } catch (e: Exception) {
                        if (isActive) {
                            Log.e(TAG, "Receiving job exception", e)
                            e.printStackTrace()
                            notifyConnectionState(ConnectionState.TCP_ERROR)
                        }
                    }
                }
            }


        }catch (e : TimeoutCancellationException){
            e.printStackTrace()
            notifyConnectionState(ConnectionState.TCP_CONNECTION_TIMEOUT)
            selectorManager?.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
            notifyConnectionState(ConnectionState.TCP_CONNECTION_FAILED)
            selectorManager?.close()
        }



    }

    override suspend fun sendData(data: String) = withContext<Unit>(ioDispatcher){

        try {
            writeMutex.withLock {
                val channel = writeChannel
                if (channel == null || channel.isClosedForWrite) {
                    // Channel not available → connection broken
                    notifyConnectionState(ConnectionState.TCP_ERROR)
                    return@withLock
                }
                channel.writeStringUtf8(data)
            }
        } catch (e: IOException) {
            // Most common: actual network/socket failure
            Log.e(TAG, "Send failed due to IO error - connection likely dead", e)
            notifyConnectionState(ConnectionState.TCP_ERROR)
        }
    }

    override suspend fun tearDown() = withContext<Unit>(ioDispatcher) {

        notifyConnectionState(ConnectionState.TCP_DISCONNECTING)

        // Cancel the receiving job first
        dataReceivingJob?.cancel()
        dataReceivingJob = null

        try {
            socket?.close()
            writeChannel?.flushAndClose()
            selectorManager?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing resources", e)
        } finally {
            socket = null
            writeChannel = null
            selectorManager = null
            notifyConnectionState(ConnectionState.TCP_DISCONNECTED)
        }
    }

}
