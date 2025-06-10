package com.xaarlox.todo_list.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xaarlox.todo_list.ui.screens.EditTodoScreen
import com.xaarlox.todo_list.ui.screens.LoadingScreen
import com.xaarlox.todo_list.ui.screens.TodosScreen

@Composable
fun TodoNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = TodoScreen.Loading.name
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = TodoScreen.Loading.name) {
            LoadingScreen(onLoadingFinished = {})
        }
        composable(route = TodoScreen.Todos.name) {
            TodosScreen()
        }
        composable(route = TodoScreen.Edit.name) {
            EditTodoScreen()
        }
    }
}

enum class TodoScreen {
    Loading, Todos, Edit
}