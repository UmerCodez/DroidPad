package com.github.umer0586.droidpad.data.util

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.github.umer0586.droidpad.data.connectionconfig.RemoteBluetoothDevice

interface BluetoothUtil {
    fun isBluetoothEnabled() : Boolean
    fun getPairedDevices() : List<RemoteBluetoothDevice>
}

class BluetoothUtilImp(private val applicationContext: Context): BluetoothUtil {

    private val bluetoothManager =
        applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    override fun isBluetoothEnabled() = bluetoothManager.adapter.isEnabled

    override fun getPairedDevices(): List<RemoteBluetoothDevice> {

        // On Android 12+ BLUETOOTH_CONNECT runtime permission is required to get paired/bounded devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return bluetoothManager.adapter.bondedDevices?.map {
                RemoteBluetoothDevice(
                    it.name,
                    it.address
                )
            }?.toList() ?: emptyList()

        }

        return emptyList()
    }

}