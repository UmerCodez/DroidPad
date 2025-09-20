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

package com.github.umer0586.droidpad.data.connectionconfig

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class TCPConfig(
    val host: String = "127.0.0.1",
    val port: Int = 8080,
    val timeoutSecs: Int = 3
){

    fun toJson() = Json.encodeToString(this)

    companion object {
        fun fromJson(json: String) = Json.decodeFromString<TCPConfig>(json)
    }

    val address get() = "$host:$port"
}
