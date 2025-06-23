package com.xaarlox.todo_list.ui.viewmodels.mvi

sealed class EditTodoIntent {
    data class OnTitleChanged(val value: String) : EditTodoIntent()
    data class OnDescriptionChanged(val value: String) : EditTodoIntent()
    data class OnIsDoneChanged(val value: Boolean) : EditTodoIntent()
    object OnSaveTodoClick : EditTodoIntent()
}