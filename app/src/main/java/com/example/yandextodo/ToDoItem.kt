package com.example.yandextodo
import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import java.util.UUID

data class ToDoItem(val text:String,
                    val uid:String = UUID.randomUUID().toString(),
                    val color:Color? = Color.White,
                    val deadline:LocalDateTime? = null,
                    val isDone:Boolean = false,
                    val importance:Importance = Importance.medium,
                    val createdAt: Long = System.currentTimeMillis(),
                    val changedAt: Long = System.currentTimeMillis(),
                    val lastUpdatedBy: String = "android-device"){
}