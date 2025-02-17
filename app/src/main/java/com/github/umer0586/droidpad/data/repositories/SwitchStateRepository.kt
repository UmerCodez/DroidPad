package com.github.umer0586.droidpad.data.repositories

import com.github.umer0586.droidpad.data.database.entities.SwitchState
import kotlinx.coroutines.flow.Flow

interface SwitchStateRepository {
    suspend fun getAllSwitchStatesAsFlow(): Flow<List<SwitchState>>
    suspend fun getAllSwitchStatesForControlPad(controlPadId: Long): List<SwitchState>
    suspend fun getAllSwitchStatesForControlPadAsFlow(controlPadId: Long): Flow<List<SwitchState>>
    suspend fun saveSwitchState(switchState: SwitchState): Long
    suspend fun getSwitchState(controlPadId: Long, controlPadItemId: Long): SwitchState?
    suspend fun updateSwitchState(switchState: SwitchState)
}