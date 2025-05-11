package com.github.umer0586.droidpad.data.repositories

import com.github.umer0586.droidpad.data.database.entities.ControlPadSensor
import kotlinx.coroutines.flow.Flow


interface ControlPadSensorRepository {

    suspend fun getControlPadSensors(): Flow<List<ControlPadSensor>>
    suspend fun getControlPadSensorsByControlPadId(controlPadId: Long): List<ControlPadSensor>
    suspend fun saveControlPadSensor(controlPadSensor: ControlPadSensor): Long
    suspend fun updateControlPadSensor(controlPadSensor: ControlPadSensor)
    suspend fun deleteControlPadSensor(controlPadSensor: ControlPadSensor)
    suspend fun deleteControlPadSensor(controlPadId: Long, sensorType: Int)

}