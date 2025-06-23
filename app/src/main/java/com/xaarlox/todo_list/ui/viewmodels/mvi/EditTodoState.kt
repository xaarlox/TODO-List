package com.xaarlox.todo_list.ui.viewmodels.mvi

data class EditTodoState(
    val id: Int? = null,
    val title: String = "",
    val description: String = "",
    val isDone: Boolean = false,
    val isLoading: Boolean = false
)
