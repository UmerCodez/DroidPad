package com.github.umer0586.droidpad.ui.screens

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.github.umer0586.droidpad.data.ExternalData
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//https://www.youtube.com/watch?v=qBxaZ071N0c
// Compose type safe navigation doesn't support custom types we have to use this workaround
object CustomNavType {

    val ControlPadType = object : NavType<ControlPad>(isNullableAllowed = true) {
        override fun get(bundle: Bundle, key: String): ControlPad? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): ControlPad {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: ControlPad): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: ControlPad) {
            bundle.putString(key, Json.encodeToString(value))
        }

    }

    val ExternalDataType = object : NavType<ExternalData?>(isNullableAllowed = true) {
        override fun get(bundle: Bundle, key: String): ExternalData? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): ExternalData? {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: ExternalData?): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: ExternalData?) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
}