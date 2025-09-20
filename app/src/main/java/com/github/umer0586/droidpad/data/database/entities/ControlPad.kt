/*
 *     This file is a part of DroidPad (https://www.github.com/UmerCodez/DroidPad)
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

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

enum class Orientation{
    LANDSCAPE,
    PORTRAIT
}

@Serializable // To make it serializable for JSON in custom Nav types
@Entity
data class ControlPad(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val orientation : Orientation,
    val backgroundColor : Long = Color(0xFF3C3C3E).value.toLong(),
    val width: Int = 0,
    val height: Int = 0,
    val logging: Boolean = false
)

fun ControlPad.toJson() = Json.encodeToString(this)
fun ControlPad.fromJson(json: String) = Json.decodeFromString<ControlPad>(json)