package com.example.yandextodo.screens


import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yandextodo.FileStorage
import com.example.yandextodo.ToDoItem
import com.example.yandextodo.components.ToDoItemUI


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    todoItems: List<ToDoItem>,
    onItemClick: (String) -> Unit,
    onAddClick: () -> Unit,
    onDeleteItem: (String) -> Unit,

    ) {


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Мои дела") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(
                items = todoItems ,
                key = { it.uid }
            ) { item ->
                ToDoItemUI(
                    item = item,
                    onClick = { onItemClick(item.uid) },
                    onDelete = {
                        onDeleteItem(item.uid)
                    },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

