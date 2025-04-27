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

package com.github.umer0586.droidpad.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

enum class ConnectionType{
    TCP,UDP,MQTT_V5,MQTT_V3,WEBSOCKET,BLUETOOTH_LE,BLUETOOTH
}

@Serializable
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ControlPad::class,
            parentColumns = ["id"],
            childColumns = ["controlPadId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ConnectionConfig(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val controlPadId: Long,
    val connectionType: ConnectionType,
    // A json string containing the connection configuration for the selected connection type
    val configJson : String
)
