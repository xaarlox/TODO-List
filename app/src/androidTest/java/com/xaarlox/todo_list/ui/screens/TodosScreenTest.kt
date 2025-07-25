package com.xaarlox.todo_list.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.xaarlox.domain.model.Todo
import com.xaarlox.todo_list.ui.util.UiEvent
import com.xaarlox.todo_list.ui.viewmodels.mvvm.TodosEvent
import com.xaarlox.todo_list.ui.viewmodels.mvvm.TodosViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TodosScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: TodosViewModel

    private val testTodos = listOf(
        Todo(id = 1, title = "Title1", description = "Description1", isDone = false),
        Todo(id = 2, title = "Title2", description = "Description2", isDone = true)
    )

    private lateinit var uiEventFlow: MutableSharedFlow<UiEvent>

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)

        val todosFlow = MutableStateFlow(testTodos)
        val userIpFlow = MutableStateFlow("15.03.20.24")
        uiEventFlow = MutableSharedFlow(replay = 1)

        every { viewModel.todos } returns todosFlow
        every { viewModel.userIp } returns userIpFlow
        every { viewModel.uiEvent } returns uiEventFlow
    }

    @Test
    fun setTodosScreenContent_shouldDisplayTodosAndIp() {
        setTodosScreenContent()
        composeTestRule.onNodeWithText("Title1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Title2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your IP: 15.03.20.24").assertIsDisplayed()
    }

    @Test
    fun swipeLeftOnTodo_shouldTriggerDeleteEvent() {
        setTodosScreenContent()
        composeTestRule.onNodeWithTag("swipe-Title1").performTouchInput { swipeLeft() }
        composeTestRule.waitForIdle()
        verify { viewModel.onEvent(match { it is TodosEvent.OnDeleteTodoClick && it.todo.id == 1 }) }
    }

    @Test
    fun emitShowSnackBarEvent_shouldDisplaySnackbar() {
        setTodosScreenContent()
        composeTestRule.runOnIdle {
            uiEventFlow.tryEmit(UiEvent.ShowSnackBar("Test Snackbar"))
        }
        composeTestRule.onNodeWithText("Test Snackbar").assertIsDisplayed()
    }

    @Test
    fun clickAddButton_shouldTriggerAddTodoEvent() {
        setTodosScreenContent()
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        verify { viewModel.onEvent(TodosEvent.OnAddTodoClick) }
    }

    @Test
    fun clickEditButton_shouldTriggerNavigationToEditScreen() {
        var navigatedEvent: UiEvent.Navigate? = null
        setTodosScreenContent(onNavigate = { event -> navigatedEvent = event })

        composeTestRule.onNodeWithContentDescription("Edit Title1").performClick()
        verify { viewModel.onEvent(match { it is TodosEvent.OnTodoClick && it.todo.id == 1 }) }

        composeTestRule.runOnIdle {
            uiEventFlow.tryEmit(UiEvent.Navigate("edit/1"))
        }

        composeTestRule.runOnIdle {
            assert(navigatedEvent is UiEvent.Navigate)
            assert((navigatedEvent as UiEvent.Navigate).route == "edit/1")
        }
    }

    private fun setTodosScreenContent(
        onNavigate: (UiEvent.Navigate) -> Unit = {},
        viewModel: TodosViewModel = this.viewModel
    ) {
        composeTestRule.setContent {
            TodosScreen(
                onNavigate = onNavigate,
                viewModel = viewModel
            )
        }
    }
}