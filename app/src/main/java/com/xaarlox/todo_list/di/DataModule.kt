package com.xaarlox.todo_list.di

import android.content.Context
import androidx.room.Room
import com.xaarlox.data.local.TodoDatabase
import com.xaarlox.data.remote.NetworkApi
import com.xaarlox.data.repository.NetworkRepositoryImpl
import com.xaarlox.data.repository.TodoRepositoryImpl
import com.xaarlox.domain.repository.NetworkRepository
import com.xaarlox.domain.repository.TodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(database: TodoDatabase): TodoRepository {
        return TodoRepositoryImpl(database.dao)
    }

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    @Provides
    @Singleton
    fun provideNetworkApi(httpClient: HttpClient): NetworkApi {
        return NetworkApi(httpClient)
    }

    @Provides
    @Singleton
    fun provideNetworkRepository(httpClient: HttpClient): NetworkRepository {
        return NetworkRepositoryImpl(NetworkApi(httpClient))
    }
}