package com.xaarlox.todo_list.ui.viewmodels.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xaarlox.data.remote.NetworkApiImpl
import com.xaarlox.domain.model.Todo
import com.xaarlox.domain.repository.TodoRepository
import com.xaarlox.todo_list.ui.util.Routes
import com.xaarlox.todo_list.ui.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodosViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val networkApi: NetworkApiImpl
) : ViewModel() {
    val todos = todoRepository.getTodos()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deletedTodo: Todo? = null

    private val _userIp = MutableStateFlow("Loading...")
    val userIp: StateFlow<String> = _userIp

    init {
        viewModelScope.launch {
            try {
                _userIp.value = networkApi.getUserIp()
            } catch (exception: Exception) {
                _userIp.value = "Error: ${exception.message}"
            }
        }
    }

    fun onEvent(event: TodosEvent) {
        when (event) {
            is TodosEvent.OnTodoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.EDIT_TODO + "?todoId=${event.todo.id}"))
            }

            is TodosEvent.OnAddTodoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.EDIT_TODO))
            }

            is TodosEvent.OnDeleteTodoClick -> {
                viewModelScope.launch {
                    deletedTodo = event.todo
                    todoRepository.deleteTodo(event.todo)
                }
            }

            is TodosEvent.OnDoneChange -> {
                viewModelScope.launch {
                    todoRepository.insertTodo(
                        event.todo.copy(
                            isDone = event.isDone
                        )
                    )
                    if (event.isDone) {
                        sendUiEvent(UiEvent.ShowSnackBar("Congrats! Todo completed!"))
                    } else {
                        sendUiEvent(UiEvent.ShowSnackBar("Oops... Todo marked as incomplete ;("))
                    }
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}