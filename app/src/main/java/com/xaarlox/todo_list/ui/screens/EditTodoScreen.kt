package com.xaarlox.todo_list.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xaarlox.todo_list.ui.components.EditTodoItem
import com.xaarlox.todo_list.ui.util.UiEvent
import com.xaarlox.todo_list.ui.viewmodels.mvi.EditTodoIntent
import com.xaarlox.todo_list.ui.viewmodels.mvi.EditTodoViewModel

@Composable
fun EditTodoScreen(
    onPopBackStack: () -> Unit, viewModel: EditTodoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message, actionLabel = effect.action
                    )
                }

                else -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onIntent(EditTodoIntent.OnSaveTodoClick) },
                shape = CircleShape,
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add, contentDescription = "Save"
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            EditTodoItem(
                state = state,
                onTitleChange = { viewModel.onIntent(EditTodoIntent.OnTitleChanged(it)) },
                onDescriptionChanged = { viewModel.onIntent(EditTodoIntent.OnDescriptionChanged(it)) },
                onIsDoneChange = { viewModel.onIntent(EditTodoIntent.OnIsDoneChanged(it)) }
            )
        }
    }
}