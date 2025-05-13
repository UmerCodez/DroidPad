package com.github.umer0586.droidpad.data.util.bluetooth

import com.github.umer0586.droidpad.data.connectionconfig.RemoteBluetoothDevice

interface BluetoothUtil {
    fun isBluetoothEnabled() : Boolean
    fun getPairedDevices() : List<RemoteBluetoothDevice>
    fun hasBluetoothPermission() : Boolean
}
