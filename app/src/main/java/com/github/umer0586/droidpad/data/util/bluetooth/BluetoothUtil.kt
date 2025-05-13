package com.github.umer0586.droidpad.data.util.bluetooth

import com.github.umer0586.droidpad.data.connectionconfig.RemoteBluetoothDevice
import kotlinx.coroutines.flow.Flow

data class BluetoothState(
    val isEnable: Boolean,
    val pairedDevices: List<RemoteBluetoothDevice>
)

interface BluetoothUtil {
    val bluetoothState: Flow<BluetoothState>
    fun isBluetoothEnabled() : Boolean
    fun getPairedDevices() : List<RemoteBluetoothDevice>
    fun hasBluetoothPermission() : Boolean
    fun cleanUp(): Unit
}
