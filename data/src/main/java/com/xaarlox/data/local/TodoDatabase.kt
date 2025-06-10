package com.xaarlox.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xaarlox.data.local.entity.TodoEntity

@Database(
    entities = [TodoEntity::class],
    version = 1
)
abstract class TodoDatabase : RoomDatabase() {
    abstract val dao: TodoDao
}