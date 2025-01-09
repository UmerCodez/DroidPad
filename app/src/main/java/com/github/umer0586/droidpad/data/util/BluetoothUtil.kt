package com.github.umer0586.droidpad.data.util

import android.bluetooth.BluetoothManager
import android.content.Context

interface BluetoothUtil {
    fun isBluetoothEnabled() : Boolean
}

class BluetoothUtilImp(applicationContext: Context): BluetoothUtil {

    private val bluetoothManager =
        applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    override fun isBluetoothEnabled() = bluetoothManager.adapter.isEnabled

}