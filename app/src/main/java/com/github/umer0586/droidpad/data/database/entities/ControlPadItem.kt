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

import androidx.compose.ui.geometry.Offset
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class ItemType{
    SWITCH,
    SLIDER,
    LABEL,
    BUTTON,
    CLICK_BUTTON
}

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
data class ControlPadItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Unique identifier for database operations
    val itemIdentifier: String, // Unique identifier (set by user) which is sent over connection
    val controlPadId: Long,
    var offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val itemType: ItemType,

    // Properties in json format
    val properties: String = "{}" // Empty json object by default
)


val ControlPadItem.offset get() = Offset(offsetX,offsetY)