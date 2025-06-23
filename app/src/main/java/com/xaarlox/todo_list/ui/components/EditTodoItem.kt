package com.xaarlox.todo_list.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xaarlox.todo_list.ui.theme.LightPurple
import com.xaarlox.todo_list.ui.theme.MyPurple
import com.xaarlox.todo_list.ui.viewmodels.mvi.EditTodoState

@Composable
fun EditTodoItem(
    state: EditTodoState,
    onTitleChange: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onIsDoneChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightPurple, RoundedCornerShape(18.dp))
            .border(2.dp, MyPurple, RoundedCornerShape(18.dp))
            .padding(20.dp)
    ) {
        BasicTextField(
            value = state.title,
            onValueChange = onTitleChange,
            textStyle = TextStyle(
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            modifier = Modifier.fillMaxWidth()
        ) { innerTextField ->
            if (state.title.isEmpty()) {
                Text(
                    text = "Title",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color.Black.copy(alpha = 0.5f)
                )
            }
            innerTextField()
        }
        Spacer(modifier = Modifier.height(16.dp))
        BasicTextField(
            value = state.description,
            onValueChange = onDescriptionChanged,
            textStyle = TextStyle(fontSize = 20.sp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
        ) { innerTextField ->
            if (state.description.isEmpty()) {
                Text(
                    text = "Description",
                    fontSize = 20.sp,
                    color = Color.Black.copy(alpha = 0.5f)
                )
            }
            innerTextField()
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = state.isDone,
            onCheckedChange = onIsDoneChange,
            colors = CheckboxDefaults.colors(
                checkedColor = LightPurple,
                uncheckedColor = MyPurple
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Mark as complete",
            fontSize = 20.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium
        )
    }
}