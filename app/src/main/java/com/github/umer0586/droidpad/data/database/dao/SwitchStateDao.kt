package com.github.umer0586.droidpad.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.umer0586.droidpad.data.database.entities.SwitchState
import kotlinx.coroutines.flow.Flow

// Room doesn't allow suspend modifier for a function returning a flow

@Dao
interface SwitchStateDao {

    @Query("SELECT * FROM SwitchState")
    fun getAll(): Flow<List<SwitchState>>

    @Query("SELECT * FROM SwitchState WHERE controlPadId = :controlPadId")
    fun getAllForControlPadAsFlow(controlPadId: Long): Flow<List<SwitchState>>

    @Query("SELECT * FROM SwitchState WHERE id = :id")
    suspend fun getById(id: Long): SwitchState?

    @Query("SELECT * FROM SwitchState WHERE controlPadId = :controlPadId AND controlPadItemId = :controlPadItemId")
    suspend fun getBy(controlPadId: Long, controlPadItemId: Long): SwitchState?

    @Insert
    suspend fun insertSwitchState(switchState: SwitchState): Long

    @Update
    suspend fun updateSwitchState(switchState: SwitchState)


}