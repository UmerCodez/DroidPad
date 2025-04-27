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


import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.github.umer0586.droidpad.data.connectionconfig.BluetoothConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.util.UUID

class BluetoothConnection(
    private val context: Context,
    val bluetoothConfig: BluetoothConfig,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Connection() {

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    override val connectionType: ConnectionType
        get() = ConnectionType.BLUETOOTH

    override suspend fun setup() {

        if(bluetoothConfig.remoteDevice == null) {
            notifyConnectionState(ConnectionState.BLUETOOTH_NO_DEVICE_SPECIFIED)
            return
        }


        val device = bluetoothAdapter.getRemoteDevice(bluetoothConfig.remoteDevice.address)
        if (device == null) {
            notifyConnectionState(ConnectionState.BLUETOOTH_INVALID_DEVICE)
            return
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notifyConnectionState(ConnectionState.BLUETOOTH_PERMISSION_REQUIRED)
            return
        }

        notifyConnectionState(ConnectionState.BLUETOOTH_CONNECTING)

        withContext(ioDispatcher) {

            try {
                socket =
                    device.createInsecureRfcommSocketToServiceRecord(
                        UUID.fromString(
                            bluetoothConfig.serviceUUID
                        )
                    )
                socket?.connect()
                outputStream = socket?.outputStream

                notifyConnectionState(ConnectionState.BLUETOOTH_CONNECTED)


            } catch (exception: Exception) {
                exception.printStackTrace()
                notifyConnectionState(ConnectionState.BLUETOOTH_CONNECTION_FAILED)

            }
        }
    }

    override suspend fun sendData(data: String) {
        withContext(ioDispatcher) {
            try {
                outputStream?.write(data.toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
                notifyConnectionState(ConnectionState.BLUETOOTH_DATA_SENT_ERROR)
            }
        }
    }

    override suspend fun tearDown() {
        withContext(ioDispatcher){

            try {
                outputStream?.close()
                socket?.close()
                outputStream = null
                socket = null
                notifyConnectionState(ConnectionState.BLUETOOTH_DISCONNECTED)
            }catch (e: Exception){
                e.printStackTrace()
                notifyConnectionState(ConnectionState.BLUETOOTH_DISCONNECTED)
            }
        }
    }
}