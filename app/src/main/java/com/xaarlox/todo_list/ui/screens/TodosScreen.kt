package com.xaarlox.todo_list.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xaarlox.todo_list.ui.components.TodoItem
import com.xaarlox.todo_list.ui.util.UiEvent
import com.xaarlox.todo_list.ui.viewmodels.mvvm.TodosEvent
import com.xaarlox.todo_list.ui.viewmodels.mvvm.TodosViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TodosScreen(
    onNavigate: (UiEvent.Navigate) -> Unit, viewModel: TodosViewModel = hiltViewModel()
) {
    val todos = viewModel.todos.collectAsState(initial = emptyList())
    val userIp = viewModel.userIp.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message, actionLabel = event.action
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(TodosEvent.OnUndoDeleteClick)
                    }
                }

                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, floatingActionButton = {
        FloatingActionButton(onClick = {
            viewModel.onEvent(TodosEvent.OnAddTodoClick)
        }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Your IP: ${userIp.value}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(todos.value) { todo ->
                TodoItem(todo = todo,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onEvent(TodosEvent.OnTodoClick(todo))
                        }
                        .padding(16.dp))
            }
        }
    }
}