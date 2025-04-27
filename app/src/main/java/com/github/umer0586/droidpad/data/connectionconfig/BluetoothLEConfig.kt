package com.github.umer0586.droidpad.data.connectionconfig

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class BluetoothLEConfig(
    val serviceUUID: String = "4fbfc1d7-f509-44ab-afe1-62ea40a4b111",
    val characteristicUUID: String = "dc3f5274-33ba-48de-8246-43bf8985b323",
    val characteristicDescriptorUUID: String = "00002902-0000-1000-8000-00805f9b34fb"
) {
    fun toJson() = Json.encodeToString(this)
    companion object {
        fun fromJson(json: String) = Json.decodeFromString<BluetoothLEConfig>(json)
    }
}