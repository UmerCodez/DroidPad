package com.github.umer0586.droidpad.data.connectionconfig

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class WebsocketServerConfig(
    val port: Int = 8080,
    val listenOnAllInterfaces: Boolean = false
){
    fun toJson() = Json.encodeToString(this)

    companion object {
        fun fromJson(json: String) = Json.decodeFromString<WebsocketServerConfig>(json)
    }

    val host get() = if (listenOnAllInterfaces) "0.0.0.0" else "Unknown"
    val address get() = "ws://${host}:${port}"
}
