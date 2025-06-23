package com.xaarlox.data.repository

import com.xaarlox.data.local.TodoDao
import com.xaarlox.domain.model.Todo
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class TodoRepositoryImplTest {
    @MockK
    private lateinit var todoDao: TodoDao
    private lateinit var todoRepositoryImpl: TodoRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        todoRepositoryImpl = TodoRepositoryImpl(todoDao)
    }

    @Test
    fun insertTodo_calls_dao_with_correct_entity() = runBlocking {
        val todo = Todo(
            id = 1,
            title = "Start Challenge",
            description = "Start GET TONED challenge (Chloe Ting)",
            isDone = true
        )
        coEvery { todoDao.insertToDo(any()) } just runs
        todoRepositoryImpl.insertTodo(todo)
        coVerify {
            todoDao.insertToDo(match {
                it.id == todo.id && it.title == todo.title && it.description == todo.description && it.isDone == todo.isDone
            })
        }
    }
}