package com.github.umer0586.droidpad.data.connectionconfig


import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

//standard service uuid for serial port profile
const val UUID_SSP = "00001101-0000-1000-8000-00805F9B34FB"

@Serializable
data class BluetoothConfig(
    val serviceUUID : String = UUID_SSP,
    val remoteDevice: RemoteBluetoothDevice?
){

    fun toJson() = Json.encodeToString(this)
    companion object {
        fun fromJson(json: String) = Json.decodeFromString<BluetoothConfig>(json)
    }
}

@Serializable
data class RemoteBluetoothDevice(
    val name: String,
    val address: String,
)