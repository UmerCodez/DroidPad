package com.github.umer0586.droidpad.data.repositoriesimp

import com.github.umer0586.droidpad.data.database.AppDatabase
import com.github.umer0586.droidpad.data.database.entities.ControlPadSensor
import com.github.umer0586.droidpad.data.repositories.ControlPadSensorRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ControlPadSensorRepositoryImp @Inject constructor(
    appDataBase: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ControlPadSensorRepository {
    private val controlPadSensorDao = appDataBase.controlPadSensorDao()

    override suspend fun getControlPadSensors(): Flow<List<ControlPadSensor>> {

        val list = withContext(ioDispatcher) {
            controlPadSensorDao.getAll()
        }
        return list
    }

    override suspend fun getControlPadSensorsByControlPadId(controlPadId: Long): List<ControlPadSensor> {

        val list = withContext(ioDispatcher) {
            controlPadSensorDao.getSensorsByControlPadId(controlPadId)
        }
        return list
    }

    override suspend fun saveControlPadSensor(controlPadSensor: ControlPadSensor): Long {

        val id = withContext(ioDispatcher) {
            controlPadSensorDao.insert(controlPadSensor)
        }
        return id
    }

    override suspend fun updateControlPadSensor(controlPadSensor: ControlPadSensor) {

        withContext(ioDispatcher) {
            controlPadSensorDao.update(controlPadSensor)
        }
    }

    override suspend fun deleteControlPadSensor(controlPadSensor: ControlPadSensor) {

        withContext(ioDispatcher) {
            controlPadSensorDao.delete(controlPadSensor)
        }
    }

    override suspend fun deleteControlPadSensor(controlPadId: Long, sensorType: Int) {
        withContext(ioDispatcher) {
            controlPadSensorDao.delete(controlPadId, sensorType)
        }
    }

}
