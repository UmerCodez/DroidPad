package com.github.umer0586.droidpad.data.repositories

import com.github.umer0586.droidpad.data.database.entities.SliderValue
import kotlinx.coroutines.flow.Flow

interface SliderValueRepository {
    suspend fun getAllSliderValuesAsFlow(): Flow<List<SliderValue>>
    suspend fun getAllSliderValuesForControlPad(controlPadId: Long): List<SliderValue>
    suspend fun getAllSliderValuesForControlPadAsFlow(controlPadId: Long): Flow<List<SliderValue>>
    suspend fun saveSliderValue(sliderValue: SliderValue): Long
    suspend fun getSliderValue(controlPadId: Long, controlPadItemId: Long): SliderValue?
    suspend fun updateSliderValue(sliderValue: SliderValue)
}