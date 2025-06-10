package com.xaarlox.data.repository

import com.xaarlox.data.local.TodoDao
import com.xaarlox.data.local.entity.TodoEntity
import com.xaarlox.domain.model.Todo
import com.xaarlox.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val dao: TodoDao
) : TodoRepository {
    override suspend fun insertToDo(todo: Todo) {
        dao.insertToDo(TodoEntity.fromDomainModel(todo))
    }

    override suspend fun deleteTodo(todo: Todo) {
        dao.deleteTodo(TodoEntity.fromDomainModel(todo))
    }

    override suspend fun getTodoById(id: Int): Todo? {
        return dao.getTodoById(id)?.toDomainModel()
    }

    override fun getTodos(): Flow<List<Todo>> {
        return dao.getTodos().map { entities -> entities.map { it.toDomainModel() } }
    }
}