package com.xaarlox.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

interface NetworkApi {
    suspend fun getUserIp(): String
}

class NetworkApiImpl(
    private val client: HttpClient
) : NetworkApi {
    override suspend fun getUserIp(): String {
        return client.get("https://api.ipify.org/").bodyAsText()
    }
}