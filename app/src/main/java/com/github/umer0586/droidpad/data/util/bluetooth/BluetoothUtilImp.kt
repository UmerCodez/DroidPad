package com.github.umer0586.droidpad.data.util.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.umer0586.droidpad.data.connectionconfig.RemoteBluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BluetoothUtilImp(private val applicationContext: Context): BluetoothUtil {


    private val bluetoothManager =
        applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private val broadcastMessageReceiver = BroadcastMessageReceiver(applicationContext)

    private val _bluetoothState = MutableStateFlow(
        BluetoothState(
            isEnable = isBluetoothEnabled(),
            pairedDevices = getPairedDevices()
        )
    )

    override val bluetoothState: Flow<BluetoothState>
        get() = _bluetoothState.asStateFlow()


    init {


        broadcastMessageReceiver.registerEvents(IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        broadcastMessageReceiver.setOnMessageReceived { intent ->
            if (BluetoothAdapter.ACTION_STATE_CHANGED == intent.action) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        Log.d("Bluetooth", "Bluetooth is OFF")
                        _bluetoothState.value = BluetoothState(
                            isEnable = false,
                            pairedDevices = getPairedDevices()
                        )
                    }
                    BluetoothAdapter.STATE_ON -> {
                        Log.d("Bluetooth", "Bluetooth is ON")
                        _bluetoothState.value = BluetoothState(
                            isEnable = true,
                            pairedDevices = getPairedDevices()
                        )
                    }

                }
            }
        }
    }

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

    override fun cleanUp() {
        broadcastMessageReceiver.unregisterEvents()
    }

}

private class BroadcastMessageReceiver(private val context: Context) : BroadcastReceiver()
{
    private var messageReceiveCallBack : ((Intent) -> Unit)? = null
    private var isRegistered = false

    companion object
    {
        private val TAG: String = BroadcastMessageReceiver::class.java.simpleName
    }

    fun setOnMessageReceived(callBack : ((Intent) -> Unit)?)
    {
        messageReceiveCallBack = callBack
    }

    override fun onReceive(context: Context, intent: Intent?)
    {
        Log.d(TAG, "onReceive() intent = [$intent]")
        if (intent != null) {
            messageReceiveCallBack?.invoke(intent)
        }

    }

    fun registerEvents(intentFilter: IntentFilter)
    {
        Log.d(TAG, "registerEvents() called")

        try
        {
            if (!isRegistered)
                ContextCompat.registerReceiver(context,this, intentFilter,ContextCompat.RECEIVER_NOT_EXPORTED)


            isRegistered = true
        }
        catch (e: IllegalArgumentException)
        {
            isRegistered = false
            e.printStackTrace()
        }
    }

    fun unregisterEvents()
    {
        Log.d(TAG, "unregister() called")

        try
        {
            if (isRegistered)
                context.unregisterReceiver(this)

            isRegistered = false
        }
        catch (e: IllegalArgumentException)
        {
            isRegistered = false
            e.printStackTrace()
        }
    }


}