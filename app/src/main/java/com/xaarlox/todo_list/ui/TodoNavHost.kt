package com.xaarlox.todo_list.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xaarlox.todo_list.ui.screens.EditTodoScreen
import com.xaarlox.todo_list.ui.screens.LoadingScreen
import com.xaarlox.todo_list.ui.screens.TodosScreen
import com.xaarlox.todo_list.ui.util.Routes

@Composable
fun TodoNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOADING_TODOS
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Routes.LOADING_TODOS) {
            LoadingScreen(onLoadingFinished = {
                navController.navigate(Routes.TODO_LIST) {
                    popUpTo(Routes.LOADING_TODOS) { inclusive = true }
                }
            })
        }
        composable(route = Routes.TODO_LIST) {
            TodosScreen(
                onNavigate = { event ->
                    navController.navigate(event.route)
                }
            )
        }
        composable(route = Routes.ADD_EDIT_TODO) {
            EditTodoScreen()
        }
    }
}