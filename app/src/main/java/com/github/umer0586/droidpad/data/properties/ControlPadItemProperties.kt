/*
 *     This file is a part of DroidPad (https://www.github.com/umer0586/DroidPad)
 *     Copyright (C) 2025 Umer Farooq (umerfarooq2383@gmail.com)
 *
 *     DroidPad is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     DroidPad is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with DroidPad. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.github.umer0586.droidpad.data.properties


import androidx.compose.ui.graphics.Color
import com.github.umer0586.droidpad.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// TODO : Add color property for label
@Serializable
data class LabelProperties(
    val text: String = "label"
){

    fun toJson() = Json.encodeToString(this)
    companion object {
        fun fromJson(json: String) = Json.decodeFromString<LabelProperties>(json)
    }
}

@Serializable
data class SwitchProperties(
    val trackColor: ULong = Color(0xFFDECA86).value,
    val thumbColor: ULong = Color(0xFFB99D22).value,
){
    fun toJson() = Json.encodeToString(this)
    companion object {
        fun fromJson(json: String) = Json.decodeFromString<SwitchProperties>(json)
    }
}

@Serializable
data class ButtonProperties(
    val text: String = "Btn",
    val textColor: ULong = Color(0xFFFFFCFC).value,
    val buttonColor: ULong = Color(0xFF7D5260).value,
    val useIcon: Boolean = false,
    val useClickAction: Boolean = false,
    val iconId: Int = 0,
    val iconColor: ULong = Color(0xFFFFFCFC).value
){
    fun toJson() = Json.encodeToString(this)
    companion object{
        fun fromJson(json: String) = Json.decodeFromString<ButtonProperties>(json)
        fun getIconById(id: Int) = idToIconMap[id] ?: R.drawable.ic_power
        val iconIds = idToIconMap.keys.toList()
    }
}

@Serializable
data class DpadProperties(
    val backgroundColor: ULong = Color(0xFFFDD835).value,
    val buttonColor: ULong = Color(0xFF2A2929).value,
    val useClickAction: Boolean = false
){
    fun toJson() = Json.encodeToString(this)
    companion object {
        fun fromJson(json: String) = Json.decodeFromString<DpadProperties>(json)
    }
}


@Serializable
data class SliderProperties(
    val minValue: Float = 0f,
    val maxValue: Float = 10f,
    val thumbColor: ULong = Color(0xFF7D5260).value,
    val trackColor: ULong = Color(0xFF39456B).value,
){
    fun toJson() = Json.encodeToString(this)

    companion object {
        fun fromJson(json: String) = Json.decodeFromString<SliderProperties>(json)
    }
}


private val idToIconMap = mapOf(
    0 to R.drawable.ic_power,
    1 to R.drawable.ic_up_arrow,
    2 to R.drawable.ic_right_arrow,
    3 to R.drawable.ic_down_arrow,
    4 to R.drawable.ic_left_arrow,
    5 to R.drawable.ic_flash,
    6 to R.drawable.ic_add,
    7 to R.drawable.ic_minus,
    8 to R.drawable.ic_light,
)
