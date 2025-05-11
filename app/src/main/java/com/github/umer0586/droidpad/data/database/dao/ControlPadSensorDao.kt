package com.github.umer0586.droidpad.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.umer0586.droidpad.data.database.entities.ControlPadSensor
import kotlinx.coroutines.flow.Flow

@Dao
interface ControlPadSensorDao {

    @Query("SELECT * FROM ControlPadSensor")
    fun getAll() : Flow<List<ControlPadSensor>>

    @Query("SELECT * FROM ControlPadSensor WHERE controlPadId = :controlPadId")
    suspend fun getSensorsByControlPadId(controlPadId : Long) : List<ControlPadSensor>

    @Query("DELETE FROM ControlPadSensor WHERE controlPadId = :controlPadId AND sensorType = :sensorType")
    suspend fun delete(controlPadId: Long, sensorType: Int)

    @Insert
    suspend fun insert(controlPadSensor: ControlPadSensor): Long

    @Update
    suspend fun update(controlPadSensor: ControlPadSensor)

    @Delete
    suspend fun delete(controlPadSensor: ControlPadSensor)


}