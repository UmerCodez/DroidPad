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


package com.github.umer0586.droidpad.data.repositoriesimp


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.umer0586.droidpad.data.Preference
import com.github.umer0586.droidpad.data.Resolution
import com.github.umer0586.droidpad.data.repositories.PreferenceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

//The delegate will ensure that we have a single instance of DataStore with that name in our application.
private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore("user_pref")


class PreferenceRepositoryImp(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PreferenceRepository {

    private object Key {
        val builderScreenPortraitResolution = stringPreferencesKey("BUILDER_SCREEN_PORTRAIT_RESOLUTION")
        val builderScreenLandscapeResolution = stringPreferencesKey("BUILDER_SCREEN_LANDSCAPE_RESOLUTION")
        val jsonTypeForBluetooth = booleanPreferencesKey("JSON_TYPE_FOR_BLUETOOTH")
    }

    private object Defaults {
        val builderScreenPortraitResolution = Resolution(width = 0, height = 0).toJson()
        val builderScreenLandscapeResolution = Resolution(width = 0, height = 0).toJson()
        val jsonTypeForBluetooth = false
    }


    override suspend fun savePreference(preference: Preference) = withContext<Unit>(ioDispatcher) {
        context.userPreferencesDataStore.edit { pref ->
            pref[Key.builderScreenPortraitResolution] = preference.builderScreenPortraitResolution.toJson()
            pref[Key.builderScreenLandscapeResolution] = preference.builderScreenLandscapeResolution.toJson()
            pref[Key.jsonTypeForBluetooth] = preference.sendJsonOverBluetooth
        }
    }

    override val preference: Flow<Preference>
        get() = context.userPreferencesDataStore.data.map { pref ->
            Preference(
                builderScreenPortraitResolution = Resolution.fromJson(pref[Key.builderScreenPortraitResolution] ?: Defaults.builderScreenPortraitResolution),
                builderScreenLandscapeResolution = Resolution.fromJson(pref[Key.builderScreenLandscapeResolution] ?: Defaults.builderScreenLandscapeResolution),
                sendJsonOverBluetooth = pref[Key.jsonTypeForBluetooth] ?: Defaults.jsonTypeForBluetooth
            )
        }.flowOn(ioDispatcher)

}

