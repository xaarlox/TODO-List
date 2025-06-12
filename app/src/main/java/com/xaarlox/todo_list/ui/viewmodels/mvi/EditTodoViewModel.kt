package com.xaarlox.todo_list.ui.viewmodels.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xaarlox.domain.model.Todo
import com.xaarlox.domain.repository.TodoRepository
import com.xaarlox.todo_list.ui.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(EditTodoState())
    val state: StateFlow<EditTodoState> = _state.asStateFlow()

    private val _uiEffect = Channel<UiEvent>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        val todoId = savedStateHandle.get<Int>("todoId") ?: -1
        if (todoId != -1) {
            viewModelScope.launch {
                val todo = todoRepository.getTodoById(todoId)
                todo?.let {
                    _state.update { current ->
                        current.copy(
                            id = it.id,
                            title = it.title,
                            description = it.description.orEmpty(),
                            isDone = it.isDone
                        )
                    }
                }
            }
        }
    }

    fun onIntent(intent: EditTodoIntent) {
        when (intent) {
            is EditTodoIntent.OnTitleChanged -> {
                _state.update { it.copy(title = intent.value) }
            }

            is EditTodoIntent.OnDescriptionChanged -> {
                _state.update { it.copy(description = intent.value) }
            }

            is EditTodoIntent.OnIsDoneChanged -> {
                _state.update { it.copy(isDone = intent.value) }
            }

            is EditTodoIntent.OnSaveTodoClick -> {
                viewModelScope.launch {
                    if (state.value.title.isBlank()) {
                        sendUiEffect(
                            UiEvent.ShowSnackBar(
                                message = "The title can't be empty"
                            )
                        )
                        return@launch
                    }

                    val todo = Todo(
                        id = state.value.id,
                        title = state.value.title,
                        description = state.value.description,
                        isDone = state.value.isDone
                    )
                    todoRepository.insertTodo(todo)
                    sendUiEffect(UiEvent.ShowSnackBar("Todo saved successfully"))
                    sendUiEffect(UiEvent.PopBackStack)
                }
            }
        }
    }

    private fun sendUiEffect(uiEffect: UiEvent) {
        viewModelScope.launch {
            _uiEffect.send(uiEffect)
        }
    }
}