package com.xaarlox.todo_list.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xaarlox.todo_list.ui.util.UiEvent
import com.xaarlox.todo_list.ui.viewmodels.mvi.EditTodoIntent
import com.xaarlox.todo_list.ui.viewmodels.mvi.EditTodoViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onIntent(EditTodoIntent.OnSaveTodoClick)
            }) {
                Icon(
                    imageVector = Icons.Default.Check, contentDescription = "Save"
                )
            }
        }) {
        Column(modifier = Modifier.fillMaxSize()) {
            TextField(value = state.title, onValueChange = {
                viewModel.onIntent(EditTodoIntent.OnTitleChanged(it))
            }, placeholder = {
                Text(text = "Title")
            }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = state.description, onValueChange = {
                viewModel.onIntent(EditTodoIntent.OnDescriptionChanged(it))
            }, placeholder = {
                Text(text = "Description")
            }, modifier = Modifier.fillMaxWidth(), singleLine = false, maxLines = 5
            )
        }
    }
}