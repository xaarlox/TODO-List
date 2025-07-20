package com.xaarlox.todo_list.ui.screens

import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.xaarlox.todo_list.ui.util.UiEvent
import com.xaarlox.todo_list.ui.viewmodels.mvi.EditTodoIntent
import com.xaarlox.todo_list.ui.viewmodels.mvi.EditTodoState
import com.xaarlox.todo_list.ui.viewmodels.mvi.EditTodoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditTodoScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: EditTodoViewModel
    private lateinit var stateFlow: MutableStateFlow<EditTodoState>
    private lateinit var effectFlow: MutableSharedFlow<UiEvent>
    private lateinit var onPopBackStack: () -> Unit

    @Before
    fun setUp() {
        val initialState = EditTodoState(
            id = 1,
            title = "Test title",
            description = "Test description",
            isDone = false,
            isLoading = true
        )
        stateFlow = MutableStateFlow(initialState)
        effectFlow = MutableSharedFlow()
        viewModel = mockk(relaxed = true)
        every { viewModel.state } returns stateFlow
        every { viewModel.uiEffect } returns effectFlow
        onPopBackStack = mockk(relaxed = true)
        composeTestRule.setContent {
            EditTodoScreen(
                viewModel = viewModel,
                onPopBackStack = onPopBackStack
            )
        }
    }

    @Test
    fun setEditTodoScreenContent_shouldDisplayAllFields() {
        composeTestRule.onNodeWithText("Test title").assertExists()
        composeTestRule.onNodeWithText("Test description").assertExists()
        composeTestRule.onNodeWithText("Mark as complete").assertExists()
        composeTestRule.onNodeWithContentDescription("Save").assertExists()
    }

    @Test
    fun typingTitle_shouldTriggerOnTitleChangedIntent() {
        composeTestRule.onNodeWithText("Test title").performTextInput(" new")
        verify { viewModel.onIntent(match { it is EditTodoIntent.OnTitleChanged }) }
    }

    @Test
    fun typingDescription_shouldTriggersOnDescriptionChangedIntent() {
        composeTestRule.onNodeWithText("Test description").performTextInput(" more")
        verify { viewModel.onIntent(match { it is EditTodoIntent.OnDescriptionChanged }) }
    }

    @Test
    fun clickingCheckbox_shouldTriggerOnIsDoneChangedIntent() {
        composeTestRule.onNode(isToggleable()).performClick()
        verify { viewModel.onIntent(match { it is EditTodoIntent.OnIsDoneChanged }) }
    }

    @Test
    fun clickingSaveFab_shouldTriggerOnSaveIntentAndShowSnackbar() {
        composeTestRule.onNodeWithContentDescription("Save").performClick()
        verify { viewModel.onIntent(EditTodoIntent.OnSaveTodoClick) }
        runBlocking {
            effectFlow.emit(UiEvent.ShowSnackBar("Todo saved!"))
        }
        composeTestRule.onNodeWithText("Todo saved!").assertExists()
    }

    @Test
    fun emitPopBackStackEvent_shouldTriggerNavigationBack() {
        runBlocking {
            effectFlow.emit(UiEvent.PopBackStack)
        }
        verify { onPopBackStack.invoke() }
    }
}