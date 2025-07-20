package com.xaarlox.data.repository

import com.xaarlox.data.remote.NetworkApi
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class NetworkRepositoryImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var mockNetworkApi: NetworkApi
    private lateinit var networkRepository: NetworkRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        networkRepository = NetworkRepositoryImpl(mockNetworkApi)
    }

    @Test
    fun `should return IP address when NetworkApi returns successfully`() = runTest {
        coEvery { mockNetworkApi.getUserIp() } returns "22.04.20.24"

        val result = networkRepository.getUserIp()
        assertEquals("22.04.20.24", result)
    }

    @Test
    fun `should return error message when NetworkApi throws exception`() = runTest {
        coEvery { mockNetworkApi.getUserIp() } throws Exception("Network error")

        val result = networkRepository.getUserIp()
        assertTrue(result.contains("Oops"))
    }
}