package com.example.yandextodo

import com.example.yandextodo.database.RoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Repository(
    private val backendService: BackendService,
    private val localDataRepository: RoomRepository
) {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun getItemsStateFlow(): StateFlow<List<ToDoItem>> {
        return localDataRepository.itemsState
    }

    suspend fun syncLocalToServer() {
        val localItems = localDataRepository.getAllSnapshot()

        if (localItems.isEmpty()) {
            return
        }

        val result = backendService.syncLocalToServer(localItems)

        if (result.isSuccess) {
            val mergedItems = result.getOrThrow()
            localDataRepository.replaceAll(mergedItems)
        }
    }



    fun addItem(item: ToDoItem) {
        ioScope.launch {
            localDataRepository.add(item)
            val result = backendService.addItem(item)
            if (result.isSuccess) {
                val serverItem = result.getOrThrow()
                localDataRepository.update(serverItem)
            }
        }
    }

    fun updateItem(item: ToDoItem) {
        ioScope.launch {
            localDataRepository.update(item)
            val result = backendService.updateItem(item)
            if (result.isSuccess) {
                val serverItem = result.getOrThrow()
                localDataRepository.update(serverItem)
            }
        }
    }

    fun deleteItem(uid: String) {
        ioScope.launch {
            val item = localDataRepository.getItem(uid)
            if (item != null) {
                localDataRepository.remove(uid)
                backendService.deleteItem(uid)
            }
        }
    }
}