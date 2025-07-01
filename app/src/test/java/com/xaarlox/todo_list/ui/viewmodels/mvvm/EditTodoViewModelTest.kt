package com.xaarlox.todo_list.ui.viewmodels.mvvm

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.xaarlox.domain.model.Todo
import com.xaarlox.domain.repository.TodoRepository
import com.xaarlox.todo_list.ui.util.UiEvent
import com.xaarlox.todo_list.ui.viewmodels.mvi.EditTodoIntent
import com.xaarlox.todo_list.ui.viewmodels.mvi.EditTodoViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditTodoViewModelTest {
    @MockK
    private lateinit var repository: TodoRepository

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var editTodoViewModel: EditTodoViewModel
    private var testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        editTodoViewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onTitleChanged updates state`() = runTest {
        editTodoViewModel.onIntent(EditTodoIntent.OnTitleChanged("Test title"))
        assertEquals("Test title", editTodoViewModel.state.value.title)
    }

    @Test
    fun `onDescriptionChanged updates state`() = runTest {
        editTodoViewModel.onIntent(EditTodoIntent.OnDescriptionChanged("Test description"))
        assertEquals("Test description", editTodoViewModel.state.value.description)
    }

    @Test
    fun `onIsDoneChanged updates state`() = runTest {
        editTodoViewModel.onIntent(EditTodoIntent.OnIsDoneChanged(true))
        assertTrue(editTodoViewModel.state.value.isDone)
    }

    @Test
    fun `onSaveTodoClick with blank title emits snackbar`() = runTest {
        editTodoViewModel.onIntent(EditTodoIntent.OnTitleChanged(""))
        editTodoViewModel.uiEffect.test {
            editTodoViewModel.onIntent(EditTodoIntent.OnSaveTodoClick)
            val event = awaitItem()
            assertTrue(event is UiEvent.ShowSnackBar)
            assertEquals("The title can't be empty", (event as UiEvent.ShowSnackBar).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSaveTodoClick with valid data saves todo and emits events`() = runTest {
        coEvery { repository.insertTodo(any()) } returns Unit

        editTodoViewModel.onIntent(EditTodoIntent.OnTitleChanged("Title"))
        editTodoViewModel.onIntent(EditTodoIntent.OnDescriptionChanged("Description"))
        editTodoViewModel.onIntent(EditTodoIntent.OnIsDoneChanged(true))
        editTodoViewModel.onIntent(EditTodoIntent.OnSaveTodoClick)

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            repository.insertTodo(
                Todo(
                    id = null,
                    title = "Title",
                    description = "Description",
                    isDone = true
                )
            )
        }

        editTodoViewModel.uiEffect.test {
            assertEquals("Todo saved successfully", (awaitItem() as UiEvent.ShowSnackBar).message)
            assertTrue(awaitItem() is UiEvent.PopBackStack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init with todoId loads todo from repository`() = runTest {
        val todo = Todo(1, "Title", "Description", false)
        coEvery { repository.getTodoById(1) } returns todo

        val viewModel = createViewModel(todoId = 1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.state.value.id)
        assertEquals("Title", viewModel.state.value.title)
        assertEquals("Description", viewModel.state.value.description)
        assertFalse(viewModel.state.value.isDone)
    }

    private fun createViewModel(todoId: Int? = null): EditTodoViewModel {
        val handle =
            if (todoId != null) SavedStateHandle(mapOf("todoId" to todoId)) else SavedStateHandle()
        savedStateHandle = handle
        return EditTodoViewModel(repository, handle)
    }
}