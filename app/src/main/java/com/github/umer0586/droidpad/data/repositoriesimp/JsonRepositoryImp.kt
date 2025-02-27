package com.github.umer0586.droidpad.data.repositoriesimp

import com.github.umer0586.droidpad.data.repositories.JsonRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class JsonRepositoryImp(
    private val httpClient: HttpClient = HttpClient(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : JsonRepository {

    override suspend fun fetchRemoteJson(url: String): String {
        return withContext(ioDispatcher){
            httpClient.get(url).bodyAsText()
        }
    }

}