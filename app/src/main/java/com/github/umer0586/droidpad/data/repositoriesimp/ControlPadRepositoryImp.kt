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
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.repositories.ControlPadRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ControlPadRepositoryImp @Inject constructor(
    appDataBase: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ControlPadRepository {
    private val controlPadDao = appDataBase.controlPadDao()
    private val controlPadItemDao = appDataBase.controlPadItemDao()

    override suspend fun getControlPads(): Flow<List<ControlPad>> {

        val list = withContext(ioDispatcher) {
            controlPadDao.getAll()
        }
        return list
    }

    override suspend fun getControlPadById(id: Long): ControlPad? {

        val controlPad = withContext(ioDispatcher) {
            controlPadDao.getById(id)
        }

        return controlPad
    }

    override suspend fun saveControlPad(controlPad: ControlPad): Long {

      val id =  withContext(ioDispatcher) {
            controlPadDao.insert(controlPad)
        }
        return id
    }

    override suspend fun updateControlPad(controlPad: ControlPad) {
        withContext(ioDispatcher) {
            controlPadDao.update(controlPad)
        }
    }

    override suspend fun deleteControlPad(controlPad: ControlPad) {
        withContext(ioDispatcher) {
            controlPadDao.delete(controlPad)
        }
    }

    override suspend fun getControlPadItemsOf(controlPad: ControlPad): List<ControlPadItem> {
        val list = withContext(ioDispatcher) {
            controlPadItemDao.getAll().first().filter { it.controlPadId == controlPad.id }
        }
        return list
    }
}