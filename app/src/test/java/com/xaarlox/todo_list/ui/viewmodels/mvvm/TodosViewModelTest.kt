package com.xaarlox.todo_list.ui.viewmodels.mvvm

import app.cash.turbine.test
import com.xaarlox.data.remote.NetworkApi
import com.xaarlox.domain.model.Todo
import com.xaarlox.domain.repository.TodoRepository
import com.xaarlox.todo_list.ui.util.Routes
import com.xaarlox.todo_list.ui.util.UiEvent
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodosViewModelTest {
    @MockK
    private lateinit var repository: TodoRepository

    @MockK
    private lateinit var networkApi: NetworkApi

    private lateinit var viewModel: TodosViewModel
    private var testDispatcher = StandardTestDispatcher()

    private val testTodo = Todo(1, "Test title", "Test description", false)
    private val completedTodo = testTodo.copy(isDone = true)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        every { repository.getTodos() } returns MutableStateFlow(emptyList())
        coEvery { networkApi.getUserIp() } returns "14.02.20.25"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when ViewModel is initialized then todos emits initial value`() = runTest {
        val fakeFlow = MutableStateFlow(emptyList<Todo>())
        every { repository.getTodos() } returns fakeFlow
        createViewModel()
        viewModel.todos.test {
            assertEquals(emptyList<Todo>(), awaitItem())
            cancel()
        }
    }

    @Test
    fun `when viewModel is initialized then user IP is fetched`() = runTest {
        coEvery { networkApi.getUserIp() } returns "14.02.20.25"
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.userIp.test {
            assertEquals("14.02.20.25", awaitItem())
        }
    }

    @Test
    fun `when fetching user IP fails then error is set`() = runTest {
        coEvery { networkApi.getUserIp() } throws RuntimeException("Network error")
        createViewModel()
        testDispatcher.scheduler.runCurrent()
        viewModel.userIp.test {
            assertEquals("Error: Network error", awaitItem())
        }
    }

    @Test
    fun `when OnTodoClick event is sent then Navigate UiEvent is emitted`() = runTest {
        createViewModel()
        expectSingleUiEvent(UiEvent.Navigate(Routes.EDIT_TODO + "?todoId=${testTodo.id}")) {
            viewModel.onEvent(TodosEvent.OnTodoClick(testTodo))
        }
    }

    @Test
    fun `when OnAddTodoClick event is sent then Navigate UiEvent is emitted`() = runTest {
        createViewModel()
        expectSingleUiEvent(UiEvent.Navigate(Routes.EDIT_TODO)) {
            viewModel.onEvent(TodosEvent.OnAddTodoClick)
        }
    }

    @Test
    fun `when OnDeleteTodoClick event is sent then deleteTodo is called`() = runTest {
        createViewModel()
        coEvery { repository.deleteTodo(any()) } returns Unit
        viewModel.onEvent(TodosEvent.OnDeleteTodoClick(testTodo))
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.deleteTodo(testTodo) }
    }

    @Test
    fun `when OnDoneChange is true then insertTodo is called and congrats snackbar is shown`() =
        runTest {
            createViewModel()
            coEvery { repository.insertTodo(any()) } returns Unit
            expectSingleUiEvent(UiEvent.ShowSnackBar("Congrats! Todo completed!")) {
                viewModel.onEvent(TodosEvent.OnDoneChange(testTodo, true))
                testDispatcher.scheduler.advanceUntilIdle()
                coVerify { repository.insertTodo(completedTodo) }
            }
        }

    @Test
    fun `when OnDoneChange is false then insertTodo is called and incomplete snackbar is shown`() =
        runTest {
            createViewModel()
            coEvery { repository.insertTodo(any()) } returns Unit
            expectSingleUiEvent(UiEvent.ShowSnackBar("Oops... Todo marked as incomplete ;(")) {
                viewModel.onEvent(TodosEvent.OnDoneChange(completedTodo, false))
                testDispatcher.scheduler.advanceUntilIdle()
                coVerify { repository.insertTodo(testTodo) }
            }
        }

    private fun createViewModel() {
        viewModel = TodosViewModel(repository, networkApi)
    }

    private suspend fun expectSingleUiEvent(expected: UiEvent, action: suspend () -> Unit) {
        viewModel.uiEvent.test {
            action()
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}