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

package com.github.umer0586.droidpad.data.repositories

import com.github.umer0586.droidpad.data.database.entities.ConnectionConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import kotlinx.coroutines.flow.Flow


interface ConnectionConfigRepository {
    suspend fun getAll() : Flow<List<ConnectionConfig>>
    suspend fun getById(id : Long) : ConnectionConfig?
    suspend fun getConfigForControlPad(controlPadId : Long) : ConnectionConfig?
    suspend fun update(controlPadId: Long, connectionType: ConnectionType, configJson: String)
    suspend fun save(configuration: ConnectionConfig) : Long
    suspend fun update(configuration: ConnectionConfig)
    suspend fun delete(configuration: ConnectionConfig)
}