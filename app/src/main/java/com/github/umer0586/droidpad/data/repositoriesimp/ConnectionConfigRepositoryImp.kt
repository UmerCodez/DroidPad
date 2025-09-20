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

package com.github.umer0586.droidpad.data.repositoriesimp

import com.github.umer0586.droidpad.data.database.AppDatabase
import com.github.umer0586.droidpad.data.database.entities.ConnectionConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import com.github.umer0586.droidpad.data.repositories.ConnectionConfigRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ConnectionConfigRepositoryImp @Inject constructor(
    appDataBase: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ConnectionConfigRepository{
    private val connectionConfigDao = appDataBase.connectionConfigurationDao()

    override suspend fun getAll() = withContext(ioDispatcher){
        connectionConfigDao.getAll()
    }

    override suspend fun getById(id: Long) = withContext(ioDispatcher) {
        connectionConfigDao.getById(id)
    }

    override suspend fun getConfigForControlPad(controlPadId: Long) = withContext(ioDispatcher) {
        connectionConfigDao
            .getAll()
            .first()
            .find { it.controlPadId == controlPadId }

    }

    override suspend fun save(configuration: ConnectionConfig) = withContext(ioDispatcher) {
       connectionConfigDao.insert(configuration)
    }

    override suspend fun update(configuration: ConnectionConfig) {
        withContext(ioDispatcher) {
            connectionConfigDao.update(configuration)
        }
    }

    override suspend fun delete(configuration: ConnectionConfig) {
        withContext(ioDispatcher) {
            connectionConfigDao.delete(configuration)
        }
    }
    override suspend fun update(controlPadId: Long, connectionType: ConnectionType, configJson: String) {
        withContext(ioDispatcher) {
            getConfigForControlPad(controlPadId)
                ?.also { retrievedConfig ->
                    connectionConfigDao.update(
                        retrievedConfig.copy(
                            connectionType = connectionType,
                            configJson = configJson
                        )
                    )
                }
        }
    }


}