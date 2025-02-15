package com.github.umer0586.droidpad.data.repositories

import com.github.umer0586.droidpad.data.Preference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface PreferenceRepository {
    suspend fun savePreference(preference: Preference)
    val preference: Flow<Preference>
}

suspend fun PreferenceRepository.updatePreference(preference: (pref: Preference) -> Preference) {
    val oldPreference = this.preference.first()
    val newPreference = preference.invoke(oldPreference)
    savePreference(newPreference)
}

suspend fun PreferenceRepository.getPreference() = preference.first()