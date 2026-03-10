package com.example.yandextodo.database

import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.yandextodo.Importance
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "todo_items")
@TypeConverters(TodoItemConverters::class)
data class ToDoItemEntity(
    @PrimaryKey
    val uid: String,
    val text: String,
    val importance: String,
    val color: Int,
    val deadline: String? = null,
    val isDone: Boolean = false,
    val createdAt: Long,
    val changedAt: Long,
    val lastUpdatedBy: String = "android-device"
)

class TodoItemConverters {
    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun toLocalDateTime(dateString: String?): LocalDateTime? {
        return dateString?.let {
            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }

    @TypeConverter
    fun fromImportance(importance: Importance): String {
        return importance.name
    }

    @TypeConverter
    fun toImportance(importanceString: String): Importance {
        return Importance.valueOf(importanceString)
    }

    @TypeConverter
    fun fromColor(color: androidx.compose.ui.graphics.Color): Int {
        return color.toArgb()
    }

    @TypeConverter
    fun toColor(argb: Int): androidx.compose.ui.graphics.Color {
        return androidx.compose.ui.graphics.Color(argb)
    }
}