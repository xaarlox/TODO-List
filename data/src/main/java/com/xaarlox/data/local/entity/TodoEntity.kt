package com.xaarlox.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xaarlox.domain.model.Todo

@Entity(tableName = "todo")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val title: String,
    val description: String?,
    val isDone: Boolean
) {
    fun toDomainModel(): Todo {
        return Todo(
            id = id, title = title, description = description, isDone = isDone
        )
    }

    companion object {
        fun fromDomainModel(todo: Todo): TodoEntity {
            return TodoEntity(
                id = todo.id,
                title = todo.title,
                description = todo.description,
                isDone = todo.isDone
            )
        }
    }
}
