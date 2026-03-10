package com.example.yandextodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.yandextodo.api.ToDoApiService
import com.example.yandextodo.database.RoomRepository
import com.example.yandextodo.database.ToDoDatabase
import com.example.yandextodo.screens.ItemScreen
import com.example.yandextodo.screens.ListScreen
import com.example.yandextodo.ui.theme.YandexToDoTheme
import kotlinx.coroutines.launch


sealed class Screen(val route: String) {
    object TodoList : Screen("todo_list")
    object TodoItem : Screen("todo_item/{itemId}") {
        fun createRoute(itemId: String?) = "todo_item/${itemId ?: "new"}"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val BASE_URL = "https://hive.mrdekk.ru/todo/"
        val BEARER_TOKEN = "c6c66fbf-397e-4a2f-9804-4d2c0974eb8a"

        val database = ToDoDatabase.getDatabase(this)
        val toDoItemDao = database.toDoItemDao()
        val roomRepository = RoomRepository(toDoItemDao)
        val apiService = ToDoApiService(BASE_URL, BEARER_TOKEN)
        val backend = BackendService(apiService)
        val repository = Repository(backend, roomRepository)



        lifecycleScope.launch {
            try {
                repository.syncLocalToServer()
            } catch (e: Exception) {
                println("Initial sync failed: ${e.message}")
            }
        }

        setContent {
            YandexToDoTheme {
                val todoItems by repository.getItemsStateFlow().collectAsState()

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.TodoList.route
                ) {
                    composable(Screen.TodoList.route) {
                        ListScreen(
                            todoItems = todoItems,
                            onItemClick = { itemId ->
                                navController.navigate(Screen.TodoItem.createRoute(itemId))
                            },
                            onAddClick = {
                                navController.navigate(Screen.TodoItem.createRoute(null))
                            },
                            onDeleteItem = {
                                    itemId -> repository.deleteItem(itemId)
                            }
                        )
                    }

                    composable(
                        route = Screen.TodoItem.route,
                        arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getString("itemId")
                        val item = if (itemId == "new") null else todoItems.firstOrNull { it.uid == itemId }

                        ItemScreen(
                            item = item,
                            onSave = { newItem ->
                                if (itemId == "new") {
                                    repository.addItem(newItem)
                                } else {
                                    repository.updateItem(newItem)
                                }
                                navController.popBackStack()
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}