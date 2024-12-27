/*
 *     This file is a part of DroidPad (https://www.github.com/umer0586/DroidPad)
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
import androidx.room.Query
import androidx.room.Update
import com.github.umer0586.droidpad.data.database.entities.ConnectionConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectionConfigurationDao {

    @Query("SELECT * FROM ConnectionConfig")
    fun getAll() : Flow<List<ConnectionConfig>>

    @Query("SELECT * FROM ConnectionConfig WHERE id = :id")
    suspend fun getById(id : Long) : ConnectionConfig?

    @Query("DELETE FROM ConnectionConfig WHERE id = :id")
    suspend fun deleteById(id : Long)

    @Insert
    suspend fun insert(configuration: ConnectionConfig) : Long

    @Update
    suspend fun update(configuration: ConnectionConfig)

    @Delete
    suspend fun delete(configuration: ConnectionConfig)

}