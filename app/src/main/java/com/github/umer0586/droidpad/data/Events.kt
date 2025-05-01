package com.github.umer0586.droidpad.data

import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.ui.components.DPAD_BUTTON
import kotlinx.serialization.Serializable
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
    fun toCSV() = "$id,$value"
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
    fun toCSV() = "$id,$state"
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
    fun toCSV() = "$id,$state"
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
    fun toCSV() = "$id,$button,$state"
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
    fun toCSV() = "$id,$x,$y"
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
    fun toCSV() = "$id,$angle"
}
