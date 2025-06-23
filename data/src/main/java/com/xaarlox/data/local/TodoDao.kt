package com.xaarlox.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xaarlox.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDo(todo: TodoEntity)

    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    @Query("SELECT * FROM todo WHERE id = :id")
    suspend fun getTodoById(id: Int): TodoEntity?

    @Query("SELECT * FROM todo")
    fun getTodos(): Flow<List<TodoEntity>>
}