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

import com.github.umer0586.droidpad.data.connectionconfig.UDPConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UDPConnection(
    val udpConfig: UDPConfig,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Connection(){


    private var datagramSocket: DatagramSocket? = null

    override val connectionType: ConnectionType
        get() = ConnectionType.UDP

    override suspend fun setup() = withContext(ioDispatcher){
        datagramSocket = DatagramSocket()
    }

    override suspend fun sendData(data: String) = withContext<Unit>(ioDispatcher) {

        val packet = DatagramPacket(
            data.toByteArray(),
            data.length,
            InetAddress.getByName(udpConfig.host),
            udpConfig.port
        )

        try {
            datagramSocket?.send(packet)
        } catch (e: Exception) {
            notifyConnectionState(ConnectionState.UDP_ERROR)
        }
    }

    override suspend fun tearDown() = withContext(ioDispatcher){
        datagramSocket?.close()
        datagramSocket = null

    }

}