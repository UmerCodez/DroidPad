package com.github.umer0586.droidpad.data.repositories

interface JsonRepository {
    suspend fun fetchRemoteJson(url: String): String
}