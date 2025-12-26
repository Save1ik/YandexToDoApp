package com.example.yandextodo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.yandextodo.screens.ItemScreen
import com.example.yandextodo.ui.theme.YandexToDoTheme
import java.io.File
import java.time.LocalDateTime


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val file = File(this.filesDir, "items.json")
        val fileStorage = FileStorage(file)
        val item1 = ToDoItem(text = "Дело 1",
            color = Color.Red,
            importance = Importance.hight,
            deadline = LocalDateTime.now())
        val item2 = ToDoItem(text = "Дело 2",
            importance = Importance.medium)
        Log.d("My logs", fileStorage.itemList.toString())
        fileStorage.addItem(item1)
        fileStorage.addItem(item2)
        Log.d("My logs", fileStorage.itemList.toString())
        fileStorage.saveItems()
        fileStorage.removeItem(item1.uid)
        Log.d("My logs", fileStorage.itemList.toString())
        fileStorage.loadFromFile()
        Log.d("My logs", fileStorage.itemList.toString())

        setContent {
            YandexToDoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ItemScreen()
                }
            }
        }
    }
}