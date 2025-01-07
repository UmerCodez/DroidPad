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
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import com.github.umer0586.droidpad.data.connectionconfig.BluetoothLEConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID


class BluetoothLEConnection(
    private val context: Context,
    config: BluetoothLEConfig,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Connection() {

    private
    val serviceUUID: UUID = UUID.fromString(config.serviceUUID) // Custom service UUID

    private
    val characteristicUUID: UUID =
        UUID.fromString(config.characteristicUUID) // Custom characteristic UUID

    private
    val descriptorUUID: UUID = UUID.fromString(config.characteristicDescriptorUUID)

    private val characteristic = BluetoothGattCharacteristic(
        characteristicUUID,
        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
        BluetoothGattCharacteristic.PERMISSION_READ
    )


    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private var advertiser: BluetoothLeAdvertiser? = null
    private var isAdvertising = false
    private var gattServer: BluetoothGattServer? = null

    private var connectedDevice: BluetoothDevice? = null


    val localName: String
        @SuppressLint("MissingPermission")
        get() = bluetoothAdapter.name ?: "Unknown"

    override val connectionType: ConnectionType
        get() = ConnectionType.BLUETOOTH_LE

    private val advertiseCallBack = object : AdvertiseCallback() {

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            isAdvertising = true
            notifyConnectionState(ConnectionState.BLUETOOTH_ADVERTISEMENT_SUCCESS)
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            println("errorCode : $errorCode")
            isAdvertising = false
            notifyConnectionState(ConnectionState.BLUETOOTH_ADVERTISEMENT_FAILED)
        }


    }

    @SuppressLint("MissingPermission")
    override suspend fun setup() = withContext(ioDispatcher) {

        //For Android 12 (API level 31) and higher:
        //
        //BLUETOOTH_CONNECT and BLUETOOTH_ADVERTISE are considered dangerous permissions and must be requested at runtime
        //These are part of the new granular Bluetooth permissions introduced in Android 12
        //
        //For Android 11 (API level 30) and lower:
        //
        //BLUETOOTH and BLUETOOTH_ADMIN are normal permissions and only need to be declared in the manifest
        //No runtime requests needed for these

        // If Android 12 or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bluetoothConnectGranted =
                context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            val bluetoothAdvertiseGranted =
                context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED

            if (!(bluetoothConnectGranted && bluetoothAdvertiseGranted)) {
                notifyConnectionState(ConnectionState.BLUETOOTH_PERMISSION_REQUIRED)
                return@withContext
            }
        }

        setupGattServer()
        startAdvertising()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    override suspend fun sendData(data: String) = withContext<Unit>(ioDispatcher) {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                connectedDevice?.also { device ->

                    gattServer?.notifyCharacteristicChanged(
                        device,
                        characteristic,
                        false,
                        data.toByteArray()
                    )
                }

            } else {
                characteristic.value = data.toByteArray()
                gattServer?.notifyCharacteristicChanged(connectedDevice, characteristic, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            notifyConnectionState(ConnectionState.BLUETOOTH_DATA_SENT_ERROR)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun tearDown() = withContext(ioDispatcher) {

        gattServer?.close()
        advertiser?.stopAdvertising(advertiseCallBack)
        isAdvertising = false
        notifyConnectionState(ConnectionState.BLUETOOTH_GATT_SERVER_CLOSED)
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT)
    private fun setupGattServer() {
        gattServer =
            bluetoothManager.openGattServer(context, object : BluetoothGattServerCallback() {

                // For now we will only manage a single connected client
                override fun onConnectionStateChange(
                    device: BluetoothDevice,
                    status: Int,
                    newState: Int
                ) {
                    super.onConnectionStateChange(device, status, newState)
                    if (newState == BluetoothProfile.STATE_CONNECTED) {

                        notifyConnectionState(ConnectionState.BLUETOOTH_CLIENT_CONNECTED)
                        connectedDevice = device

                        // Stop advertising to ensure no other client can connect.
                        // Note: A device becomes connectable only when it starts advertising,
                        // not merely by opening a GATT server.
                        advertiser?.stopAdvertising(advertiseCallBack)
                        isAdvertising = false

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                        notifyConnectionState(ConnectionState.BLUETOOTH_CLIENT_DISCONNECTED)
                        connectedDevice = null

                        // When a client disconnects, stop advertising services.
                        // This ensures that the client cannot automatically reconnect without explicit user action
                        // (e.g., tapping the "Play/Connect" button again).
                        // This approach avoids repeated or unintended advertisements of the same services
                        // when the user taps the "Play/Connect" button again.
                        advertiser?.stopAdvertising(advertiseCallBack)
                        isAdvertising = false
                    }


                }

                override fun onDescriptorWriteRequest(
                    device: BluetoothDevice?, requestId: Int,
                    descriptor: BluetoothGattDescriptor?, preparedWrite: Boolean,
                    responseNeeded: Boolean, offset: Int, value: ByteArray?
                ) {
                    println("onDescriptorWriteRequest")
                    if (responseNeeded) {
                        gattServer?.sendResponse(
                            device, requestId,
                            BluetoothGatt.GATT_SUCCESS, 0, null
                        )
                    }
                }
            })

        notifyConnectionState(ConnectionState.BLUETOOTH_GATT_SERVER_OPENED)

        val service = BluetoothGattService(serviceUUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)


        // Add descriptor for enabling notifications
        val descriptor = BluetoothGattDescriptor(
            descriptorUUID,
            BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE
        )

        characteristic.addDescriptor(descriptor)

        service.addCharacteristic(characteristic)
        gattServer?.addService(service)

    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(value = Manifest.permission.BLUETOOTH_ADVERTISE)
    private fun startAdvertising() {

        if (isAdvertising)
            return

        // TODO: prompt user to enable bluetooth
        // Will return null if Bluetooth is turned off or if Bluetooth LE Advertising is not supported on this device.
        advertiser = bluetoothAdapter.bluetoothLeAdvertiser

        if (advertiser == null) {
            notifyConnectionState(ConnectionState.BLUETOOTH_ADVERTISER_NOT_FOUND)
            return
        }

        if (!bluetoothAdapter.isMultipleAdvertisementSupported()) {
            notifyConnectionState(ConnectionState.BLUETOOTH_ADVERTISEMENT_NOT_SUPPORTED)
            return
        }

        val advertiseSettings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(true)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build()

        val advertiseData = AdvertiseData.Builder()
            // A long device name can cause advertisement failures.
            // TODO: Prompt the user to set a shorter name in Bluetooth settings
            // or exclude the device name from the advertisement.
            .setIncludeDeviceName(true)
            .addServiceUuid(ParcelUuid(serviceUUID))
            .build()


        notifyConnectionState(ConnectionState.BLUETOOTH_ADVERTISING)

        advertiser?.startAdvertising(advertiseSettings, advertiseData, advertiseCallBack)
    }


}