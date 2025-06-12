package com.xaarlox.todo_list.ui.viewmodels.mvvm

import com.xaarlox.domain.model.Todo

sealed class TodosEvent {
    data class OnDeleteTodoClick(val todo: Todo) : TodosEvent()
    data class OnDoneChange(val todo: Todo, val isDone: Boolean) : TodosEvent()
    data class OnTodoClick(val todo: Todo) : TodosEvent()
    object OnAddTodoClick : TodosEvent()
}