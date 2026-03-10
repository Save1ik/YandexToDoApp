package com.example.yandextodo.database

import androidx.compose.ui.graphics.toArgb
import com.example.yandextodo.Importance
import com.example.yandextodo.ToDoItem
import androidx.compose.ui.graphics.Color as ComposeColor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RoomRepository(
    private val todoItemDao: ToDoItemDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val logger: Logger = LoggerFactory.getLogger(RoomRepository::class.java)
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private val _itemsState = MutableStateFlow<List<ToDoItem>>(emptyList())
    val itemsState: StateFlow<List<ToDoItem>> = _itemsState.asStateFlow()

    init {
        scope.launch {
            todoItemDao.getAll().collect { entities ->
                val items = entities.mapNotNull { entity ->
                    try {
                        entity.toTodoItem()
                    } catch (e: Exception) {
                        null
                    }
                }
                _itemsState.value = items
            }
        }
    }

    suspend fun getItem(uid: String): ToDoItem? {
        return withContext(ioDispatcher) {
            todoItemDao.getById(uid)?.toTodoItem()
        }
    }

    fun getItemFlow(uid: String): Flow<ToDoItem?> {
        return todoItemDao.getByIdFlow(uid)
            .map { entity -> entity?.toTodoItem() }
            .flowOn(ioDispatcher)
    }

    suspend fun add(item: ToDoItem) {
        withContext(ioDispatcher) {
            todoItemDao.insert(item.toToDoItemEntity())
        }
    }

    suspend fun update(item: ToDoItem) {
        withContext(ioDispatcher) {
            todoItemDao.update(item.toToDoItemEntity())
        }
    }

    suspend fun remove(uid: String): Boolean {
        return withContext(ioDispatcher) {
            val deleted = todoItemDao.getById(uid)
            if (deleted != null) {
                todoItemDao.delete(deleted)
                true
            } else {
                false
            }
        }
    }


    suspend fun replaceAll(newItems: List<ToDoItem>) {
        withContext(ioDispatcher) {
            todoItemDao.deleteAll()
            if (newItems.isNotEmpty()) {
                todoItemDao.insertAll(newItems.map { it.toToDoItemEntity() })
            }
        }
    }

    suspend fun getAllSnapshot(): List<ToDoItem> {
        return withContext(ioDispatcher) {
            todoItemDao.getAllSnapshot().map { it.toTodoItem() }
        }
    }
}

fun ToDoItem.toToDoItemEntity(): ToDoItemEntity {
    return ToDoItemEntity(
        uid = uid,
        text = text,
        importance = importance.name,
        color = color?.toArgb() ?: 0,
        deadline = deadline?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        isDone = isDone,
        createdAt = createdAt,
        changedAt = changedAt,
        lastUpdatedBy = lastUpdatedBy
    )
}

fun ToDoItemEntity.toTodoItem(): ToDoItem {
    return ToDoItem(
        uid = uid,
        text = text,
        importance = Importance.valueOf(importance),
        color = ComposeColor(color),
        deadline = deadline?.let {
            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        },
        isDone = isDone,
        createdAt = createdAt,
        changedAt = changedAt,
        lastUpdatedBy = lastUpdatedBy
    )
}