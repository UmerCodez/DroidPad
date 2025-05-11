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

package com.github.umer0586.droidpad.data.sensor

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val jsonCon = Json {
    encodeDefaults = true
}

interface SensorEvent {

    fun toJson() : String
    fun toCsv(): String
}

@Serializable
data class AccelerometerEvent(
    val type: String = "ACCELEROMETER",
    val x: Float,
    val y: Float,
    val z: Float
): SensorEvent {

    override fun toJson(): String {
        return jsonCon.encodeToString(this)
    }

    override fun toCsv(): String {
        return "$type,$x,$y,$z"
    }

}

@Serializable
data class GyroscopeEvent(
    val type: String = "GYROSCOPE",
    val x: Float,
    val y: Float,
    val z: Float
): SensorEvent {

    override fun toJson(): String {
        return jsonCon.encodeToString(this)
    }

    override fun toCsv(): String {
        return "$type,$x,$y,$z"
    }

}