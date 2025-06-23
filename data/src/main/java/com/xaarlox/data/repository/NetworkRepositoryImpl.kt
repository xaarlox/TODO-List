package com.xaarlox.data.repository

import com.xaarlox.data.remote.NetworkApi
import com.xaarlox.domain.repository.NetworkRepository

class NetworkRepositoryImpl(
    private val networkApi: NetworkApi
) : NetworkRepository {
    override suspend fun getUserIp(): String {
        return try {
            networkApi.getUserIp()
        } catch (exception: Exception) {
            "Oops. Problem with fetching public API ;("
        }
    }
}