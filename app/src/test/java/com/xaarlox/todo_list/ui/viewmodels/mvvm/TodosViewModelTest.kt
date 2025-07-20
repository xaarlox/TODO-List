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
    fun `should emit initial todos when ViewModel is initialized`() = runTest {
        val fakeFlow = MutableStateFlow(emptyList<Todo>())
        every { repository.getTodos() } returns fakeFlow
        createViewModel()
        viewModel.todos.test {
            assertEquals(emptyList<Todo>(), awaitItem())
            cancel()
        }
    }

    @Test
    fun `should fetch user IP when ViewModel is initialized`() = runTest {
        coEvery { networkApi.getUserIp() } returns "14.02.20.25"
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.userIp.test {
            assertEquals("14.02.20.25", awaitItem())
        }
    }

    @Test
    fun `should set error message when fetching user IP fails`() = runTest {
        coEvery { networkApi.getUserIp() } throws RuntimeException("Network error")
        createViewModel()
        testDispatcher.scheduler.runCurrent()
        viewModel.userIp.test {
            assertEquals("Error: Network error", awaitItem())
        }
    }

    @Test
    fun `should emit Navigate UiEvent with todoId when OnTodoClick is triggered`() = runTest {
        createViewModel()
        expectSingleUiEvent(UiEvent.Navigate(Routes.EDIT_TODO + "?todoId=${testTodo.id}")) {
            viewModel.onEvent(TodosEvent.OnTodoClick(testTodo))
        }
    }

    @Test
    fun `should emit Navigate UiEvent without ID when OnAddTodoClick is triggered`() = runTest {
        createViewModel()
        expectSingleUiEvent(UiEvent.Navigate(Routes.EDIT_TODO)) {
            viewModel.onEvent(TodosEvent.OnAddTodoClick)
        }
    }

    @Test
    fun `should call deleteTodo on repository when OnDeleteTodoClick is triggered`() = runTest {
        createViewModel()
        coEvery { repository.deleteTodo(any()) } returns Unit
        viewModel.onEvent(TodosEvent.OnDeleteTodoClick(testTodo))
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.deleteTodo(testTodo) }
    }

    @Test
    fun `should insert completed todo and show congrats snackbar when OnDoneChange is true`() =
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
    fun `should insert incomplete todo and show oops snackbar when OnDoneChange is false`() =
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