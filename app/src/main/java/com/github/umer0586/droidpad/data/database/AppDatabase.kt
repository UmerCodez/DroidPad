/*
 *     This file is a part of DroidPad (https://www.github.com/umer0586/DroidPad)
 *     Copyright (C) 2024 Umer Farooq (umerfarooq2383@gmail.com)
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

package com.github.umer0586.droidpad.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.umer0586.droidpad.data.database.dao.ConnectionConfigurationDao
import com.github.umer0586.droidpad.data.database.dao.ControlPadDao
import com.github.umer0586.droidpad.data.database.dao.ControlPadItemDao
import com.github.umer0586.droidpad.data.database.dao.SwitchStateDao
import com.github.umer0586.droidpad.data.database.entities.ConnectionConfig
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.SwitchState

@Database(
    entities = [ControlPadItem::class, ControlPad::class, ConnectionConfig::class, SwitchState::class],
    version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun controlPadItemDao(): ControlPadItemDao
    abstract fun controlPadDao(): ControlPadDao
    abstract fun connectionConfigurationDao(): ConnectionConfigurationDao
    abstract fun switchStateDao(): SwitchStateDao
}

