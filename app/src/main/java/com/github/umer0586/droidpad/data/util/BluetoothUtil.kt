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
    fun hasBluetoothPermission() : Boolean
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

    override fun hasBluetoothPermission(): Boolean {

        //| Android Version | Runtime Permission Needed? | Permission |
        //|------------------|--------------------------|------------|
        //| Android 12+      | Yes                      | `BLUETOOTH_CONNECT` |
        //| Android ≤ 11     | No                       | `BLUETOOTH`, `BLUETOOTH_ADMIN` | (Normal Permissions)

        // On Android 12+ BLUETOOTH_CONNECT runtime permission is required to get paired/bounded devices
        // When requested at run time user will be asked to allow near by device permission not "Allow to connect"

        //The BLUETOOTH_CONNECT permission is required for any interaction with already connected or bonded Bluetooth devices,
        // including GATT server operations like accepting connections or notifying data.

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required below Android 12
        }

    }

}