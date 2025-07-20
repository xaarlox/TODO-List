package com.xaarlox.data.local.entity

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.xaarlox.data.local.TodoDao
import com.xaarlox.data.local.TodoDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TodoDaoTest {
    private lateinit var database: TodoDatabase
    private lateinit var dao: TodoDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), TodoDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.todoDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTodo_shouldReturnInsertedTodoById() = runTest {
        val id = 1
        val todoItem = TodoEntity(
            id = id,
            title = "Morning stretch",
            description = "Do a 5-minute full-body stretch to start the day with energy",
            isDone = true
        )
        dao.insertToDo(todoItem)
        val insertedTodo = dao.getTodoById(id)
        assertEquals(todoItem, insertedTodo)
    }

    @Test
    fun deleteTodo_shouldRemoveTodoFromDatabase() = runTest {
        val id = 2
        val todoItem = TodoEntity(
            id = id,
            title = "Try a new recipe",
            description = "Find a simple dish online and cook smth new 4 dinner",
            isDone = false
        )
        dao.insertToDo(todoItem)
        dao.deleteTodo(todoItem)
        val deletedTodo = dao.getTodoById(id)
        assertNull(deletedTodo)
    }

    @Test
    fun getTodos_shouldReturnAllInsertedTodos() = runTest {
        val todoItem1 = TodoEntity(
            id = 3,
            title = "Go for a walk",
            description = "Take a 20-minute walk outside to refresh your mind",
            isDone = false
        )
        val todoItem2 = TodoEntity(
            id = 4,
            title = "Read 10 pages",
            description = "Pick up any book and read at least 10 pages today",
            isDone = true
        )
        val expectedTodos = listOf(todoItem1, todoItem2)
        dao.insertToDo(todoItem1)
        dao.insertToDo(todoItem2)
        val allTodos = dao.getTodos().first()
        assertEquals(expectedTodos.size, allTodos.size)
        assertTrue(allTodos.containsAll(expectedTodos))
    }
}