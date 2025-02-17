package com.github.umer0586.droidpad.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.umer0586.droidpad.data.database.entities.SliderValue
import kotlinx.coroutines.flow.Flow

@Dao
interface SliderValueDao {

    @Query("SELECT * FROM SliderValue")
    fun getAll(): Flow<List<SliderValue>>

    @Query("SELECT * FROM SliderValue WHERE controlPadId = :controlPadId")
    fun getAllForControlPadAsFlow(controlPadId: Long): Flow<List<SliderValue>>

    @Query("SELECT * FROM SliderValue WHERE id = :id")
    suspend fun getById(id: Long): SliderValue?

    @Query("SELECT * FROM SliderValue WHERE controlPadId = :controlPadId AND controlPadItemId = :controlPadItemId")
    suspend fun getBy(controlPadId: Long, controlPadItemId: Long): SliderValue?

    @Insert
    suspend fun insertSliderValue(sliderValue: SliderValue): Long

    @Update
    suspend fun updateSliderValue(sliderValue: SliderValue)
}