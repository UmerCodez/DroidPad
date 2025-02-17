package com.github.umer0586.droidpad.data.repositoriesimp

import com.github.umer0586.droidpad.data.database.AppDatabase
import com.github.umer0586.droidpad.data.database.entities.SwitchState
import com.github.umer0586.droidpad.data.repositories.SwitchStateRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SwitchStateRepositoryImp @Inject constructor(
    appDataBase: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): SwitchStateRepository {

    private val switchStateDao = appDataBase.switchStateDao()
    override suspend fun getAllSwitchStatesAsFlow(): Flow<List<SwitchState>> {
        val switchStates = withContext(ioDispatcher) {
            switchStateDao.getAll()
        }
        return switchStates
    }

    override suspend fun getAllSwitchStatesForControlPad(controlPadId: Long): List<SwitchState> {
        val switchStates = withContext(ioDispatcher) {
            switchStateDao.getAll().first()
        }
        return switchStates.filter { it.controlPadId == controlPadId }
    }

    override suspend fun getAllSwitchStatesForControlPadAsFlow(controlPadId: Long): Flow<List<SwitchState>> {

        val controlPadSwitchStatesFlow = withContext(ioDispatcher) {
            switchStateDao.getAllForControlPadAsFlow(controlPadId)
        }
        return  controlPadSwitchStatesFlow
    }

    override suspend fun saveSwitchState(switchState: SwitchState): Long {
         val newId = withContext(ioDispatcher) {
             switchStateDao.insertSwitchState(switchState)
         }
        return  newId
    }

    override suspend fun getSwitchState(controlPadId: Long, controlPadItemId: Long): SwitchState? {
        val switchState = withContext(ioDispatcher) {
            switchStateDao.getBy(controlPadId = controlPadId, controlPadItemId = controlPadItemId)
        }
        return switchState
    }

    override suspend fun updateSwitchState(switchState: SwitchState) {
        withContext(ioDispatcher){
            switchStateDao.updateSwitchState(switchState)
        }
    }
}