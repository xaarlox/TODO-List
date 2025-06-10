package com.xaarlox.domain.repository

interface NetworkRepository {
    suspend fun getUserIp(): String
}