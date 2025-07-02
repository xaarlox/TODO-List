package com.xaarlox.todo_list.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xaarlox.domain.model.Todo
import com.xaarlox.todo_list.ui.theme.LightPurple
import com.xaarlox.todo_list.ui.theme.MyPurple
import com.xaarlox.todo_list.ui.viewmodels.mvvm.TodosEvent

@Composable
fun TodoItem(
    todo: Todo, onEvent: (TodosEvent) -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .border(2.dp, MyPurple, shape = RoundedCornerShape(8.dp))
            .background(LightPurple, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = todo.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            textDecoration = if (todo.isDone) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = todo.isDone,
            onCheckedChange = { isChecked ->
                onEvent(TodosEvent.OnDoneChange(todo, isChecked))
            },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Black,
                uncheckedColor = Color.Black
            ),
            modifier = Modifier.padding(2.dp)
        )
        IconButton(
            onClick = {
                onEvent(TodosEvent.OnTodoClick(todo))
            }
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit ${todo.title}")
        }
    }
}