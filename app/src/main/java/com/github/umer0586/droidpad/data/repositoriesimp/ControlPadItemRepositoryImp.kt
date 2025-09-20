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
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.repositories.ControlPadItemRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ControlPadItemRepositoryImp @Inject constructor(
    appDataBase: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ControlPadItemRepository {

    private val controlPadItemDao = appDataBase.controlPadItemDao()

    override suspend fun getAll() = withContext(ioDispatcher) {
        controlPadItemDao.getAll()
    }

    override suspend fun getById(id: Long) = withContext(ioDispatcher) {
        controlPadItemDao.getById(id)
    }

    override suspend fun save(controlPadItem: ControlPadItem) = withContext(ioDispatcher) {
        controlPadItemDao.insert(controlPadItem)
    }


    override suspend fun update(controlPadItem: ControlPadItem) {
        withContext(ioDispatcher) {
            controlPadItemDao.update(controlPadItem)
        }
    }

    override suspend fun delete(controlPadItem: ControlPadItem) {
        withContext(ioDispatcher) {
            controlPadItemDao.delete(controlPadItem)
        }
    }

}