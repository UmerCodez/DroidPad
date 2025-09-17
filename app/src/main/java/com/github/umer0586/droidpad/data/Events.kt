package com.github.umer0586.droidpad.data

import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.ui.components.DPAD_BUTTON
import com.github.umer0586.droidpad.ui.components.LEDSTATE
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json


private val JsonCon = Json {
    encodeDefaults = true
}


@Serializable
data class SliderEvent(
    val id: String,
    val type: ItemType = ItemType.SLIDER,
    val value: Float
){
    fun toJson(): String {
        return JsonCon.encodeToString(this)
    }
    fun toCSV() = "$id,SLIDER,$value"

    companion object {
        fun fromJson(json: String): SliderEvent {
            return JsonCon.decodeFromString(json)
        }
    }
}

@Serializable
data class SwitchEvent(
    val id: String,
    val type: ItemType = ItemType.SWITCH,
    val state: Boolean
){
    fun toJson(): String {
        return JsonCon.encodeToString(this)
    }
    fun toCSV() = "$id,SWITCH,$state"

    companion object {
        fun fromJson(json: String): SwitchEvent {
            return JsonCon.decodeFromString(json)
        }
    }
}

@Serializable
data class ButtonEvent(
    val id: String,
    val type: ItemType = ItemType.BUTTON,
    val state: String
){
    fun toJson(): String {
        return JsonCon.encodeToString(this)
    }
    fun toCSV() = "$id,BUTTON,$state"
}

@Serializable
data class DPadEvent(
    val id: String,
    val type: ItemType = ItemType.DPAD,
    val button: DPAD_BUTTON,
    val state: String
){
    fun toJson(): String {
        return JsonCon.encodeToString(this)
    }
    fun toCSV() = "$id,DPAD,$button,$state"
}

@Serializable
data class JoyStickEvent(
    val id: String,
    val type: ItemType = ItemType.JOYSTICK,
    val x: Float,
    val y: Float
){
    fun toJson(): String {
        return JsonCon.encodeToString(this)
    }
    fun toCSV() = "$id,JOYSTICK,$x,$y"
}

@Serializable
data class SteeringWheelEvent(
    val id: String,
    val type: ItemType = ItemType.STEERING_WHEEL,
    val angle: Float
){
    fun toJson(): String {
        return JsonCon.encodeToString(this)
    }
    fun toCSV() = "$id,STEERING_WHEEL,$angle"
}

@Serializable
data class LedEvent(
    val id: String,
    val type: ItemType = ItemType.LED,
    val state: LEDSTATE
){
    companion object {
        fun fromJson(json: String): LedEvent {
            return JsonCon.decodeFromString(json)
        }
    }
}

@Serializable
data class LogEvent(
    @Transient val timestamp: String = "",
    val type: String = "LOG",
    val message: String
){
    companion object {
        fun fromJson(json: String): LogEvent {
            return JsonCon.decodeFromString(json)
        }
    }
}
