package com.xaarlox.domain.repository

import com.xaarlox.domain.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertToDo(todo: Todo)
    suspend fun deleteTodo(todo: Todo)
    suspend fun updateTodo(todo: Todo)
    suspend fun getTodoById(id: Int): Todo?
    fun getTodos(): Flow<List<Todo>>
}