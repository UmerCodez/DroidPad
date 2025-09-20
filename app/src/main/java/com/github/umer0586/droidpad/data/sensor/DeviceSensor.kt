/*
 *     This file is a part of DroidPad (https://www.github.com/UmerCodez/DroidPad)
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

import android.content.Context
import android.hardware.Sensor

// Wrapper class for android.hardware.Sensor
// Directly using android.hardware.Sensor or other context-dependent APIs within a Composable function
// can prevent Android Studio from rendering previews. This is because the preview environment might not have access to these features (e.g., sensors, camera)
data class DeviceSensor(
    val name: String,
    val stringType: String,
    val type: Int,
    val maximumRange: Float,
    val reportingMode: Int,
    val maxDelay: Int,
    val minDelay: Int,
    val vendor: String,
    val power: Float,
    val resolution: Float,
    val isWakeUpSensor: Boolean,
){
    fun toSensor(context : Context) : Sensor {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as android.hardware.SensorManager
        return sensorManager.getDefaultSensor(this.type) ?: throw IllegalArgumentException("Sensor not found")
    }
}

fun Sensor.toDeviceSensor() : DeviceSensor {
    return DeviceSensor(
        name = this.name,
        stringType = this.stringType,
        type = this.type,
        maximumRange = this.maximumRange,
        reportingMode = this.reportingMode,
        maxDelay = this.maxDelay,
        minDelay = this.minDelay,
        vendor = this.vendor,
        power = this.power,
        resolution = this.resolution,
        isWakeUpSensor = this.isWakeUpSensor
    )
}


fun List<Sensor>.toDeviceSensors() = this.map { it.toDeviceSensor() }
fun List<DeviceSensor>.toSensors(context : Context) = this.map { it.toSensor(context) }


val fakeSensors = listOf(
    DeviceSensor(
        name = "Accelerometer",
        stringType = "android.sensor.accelerometer",
        type = 1,
        maximumRange = 19.6133f,
        reportingMode = 0,
        maxDelay = 200000,
        minDelay = 0,
        vendor = "Google",
        power = 0.13f,
        resolution = 0.0029f,
        isWakeUpSensor = false
    ),

    DeviceSensor(
        name = "Gyroscope",
        stringType = "android.sensor.gyroscope",
        type = 4,
        maximumRange = 34.9066f,
        reportingMode = 0,
        maxDelay = 200000,
        minDelay = 0,
        vendor = "Google",
        power = 0.13f,
        resolution = 0.0011f,
        isWakeUpSensor = false
    ),

    DeviceSensor(
        name = "Light",
        stringType = "android.sensor.light",
        type = 5,
        maximumRange = 40000f,
        reportingMode = 0,
        maxDelay = 200000,
        minDelay = 0,
        vendor = "Google",
        power = 0.13f,
        resolution = 1.0f,
        isWakeUpSensor = false
    ),

    DeviceSensor(
        name = "Proximity",
        stringType = "android.sensor.proximity",
        type = 8,
        maximumRange = 5.0f,
        reportingMode = 1, // On-change
        maxDelay = 0,
        minDelay = 0,
        vendor = "Google",
        power = 0.5f,
        resolution = 1.0f,
        isWakeUpSensor = true
    ),

    DeviceSensor(
        name = "Magnetic Field",
        stringType = "android.sensor.magnetic_field",
        type = 2,
        maximumRange = 2000.0f,
        reportingMode = 0,
        maxDelay = 200000,
        minDelay = 0,
        vendor = "Google",
        power = 0.35f,
        resolution = 0.1f,
        isWakeUpSensor = false
    )
)

val accelerometer = fakeSensors[0]
val gyroscope = fakeSensors[1]
val light = fakeSensors[2]
val proximity = fakeSensors[3]
val magneticField = fakeSensors[4]