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

package com.github.umer0586.droidpad.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ControlPadDao {

    @Query("SELECT * FROM ControlPad")
    fun getAll() : Flow<List<ControlPad>>

    @Query("SELECT * FROM ControlPad WHERE id = :id")
    suspend fun getById(id : Long) : ControlPad?

    @Query("DELETE FROM ControlPad WHERE id = :id")
    suspend fun deleteById(id : Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(controlPad: ControlPad) : Long

    @Delete
    suspend fun delete(controlPad: ControlPad)

    @Update
    suspend fun update(controlPad: ControlPad)

    @Query("""
        SELECT * FROM ControlPad 
        JOIN ControlPadItem ON ControlPad.id = ControlPadItem.controlPadId
        WHERE ControlPad.id = :controlPadId 
            """)
    suspend fun getControlPadWithControlPadItems(controlPadId : Long) : Map<ControlPad, List<ControlPadItem>>

}