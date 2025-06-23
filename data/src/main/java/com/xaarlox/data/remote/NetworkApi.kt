package com.xaarlox.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class NetworkApi(
    private val client: HttpClient
) {
    suspend fun getUserIp(): String {
        return client.get("https://api.ipify.org/").bodyAsText()
    }
}