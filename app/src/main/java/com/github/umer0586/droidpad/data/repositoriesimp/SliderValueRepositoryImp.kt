package com.github.umer0586.droidpad.data.repositoriesimp

import com.github.umer0586.droidpad.data.database.AppDatabase
import com.github.umer0586.droidpad.data.database.entities.SliderValue
import com.github.umer0586.droidpad.data.repositories.SliderValueRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SliderValueRepositoryImp @Inject constructor(
    appDataBase: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): SliderValueRepository {

    private val sliderValueDao = appDataBase.sliderValueDao()

    override suspend fun getAllSliderValuesAsFlow(): Flow<List<SliderValue>> {
        val sliderValues = withContext(ioDispatcher) {
            sliderValueDao.getAll()
        }
        return sliderValues
    }

    override suspend fun getAllSliderValuesForControlPad(controlPadId: Long): List<SliderValue> {
        val sliderValues = withContext(ioDispatcher) {
            sliderValueDao.getAll().first()
        }
        return sliderValues.filter { it.controlPadId == controlPadId }
    }

    override suspend fun getAllSliderValuesForControlPadAsFlow(controlPadId: Long): Flow<List<SliderValue>> {

        val controlPadSliderValuesFlow = withContext(ioDispatcher) {
            sliderValueDao.getAllForControlPadAsFlow(controlPadId)
        }
        return  controlPadSliderValuesFlow
    }

    override suspend fun saveSliderValue(sliderValue: SliderValue): Long {
        val newId = withContext(ioDispatcher) {
            sliderValueDao.insertSliderValue(sliderValue)
        }
        return  newId
    }

    override suspend fun getSliderValue(controlPadId: Long, controlPadItemId: Long): SliderValue? {
        val sliderValue = withContext(ioDispatcher) {
            sliderValueDao.getBy(controlPadId = controlPadId, controlPadItemId = controlPadItemId)
        }
        return sliderValue
    }

    override suspend fun updateSliderValue(sliderValue: SliderValue) {
        withContext(ioDispatcher){
            sliderValueDao.updateSliderValue(sliderValue)
        }
    }
}