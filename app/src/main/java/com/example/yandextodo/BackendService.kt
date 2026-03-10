package com.example.yandextodo

import com.example.yandextodo.api.ToDoApiService
import com.example.yandextodo.api.toToDoItem
import com.example.yandextodo.api.toToDoItemElementRequest
import com.example.yandextodo.api.toToDoItemUpdateRequest
import com.example.yandextodo.api.toToDoItems
import com.example.yandextodo.api.toToDoListRequest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BackendService(private val apiService: ToDoApiService) {
    private var currentRevision: Int? = null
    private val revisionMutex = Mutex()

    suspend fun getCurrentRevision(): Int? {
        return revisionMutex.withLock { currentRevision }
    }

    private suspend fun updateRevision(newRevision: Int) {
        revisionMutex.withLock {
            currentRevision = newRevision
            println("Обновлена ревизия: $newRevision")
        }
    }

    suspend fun initializeRevision(): Result<Int> {
        return try {
            val result = apiService.getToDoList()
            if (result.isSuccess) {
                val response = result.getOrThrow()
                updateRevision(response.revision)
                Result.success(response.revision)
            } else {
                Result.failure(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncLocalToServer(localItems: List<ToDoItem>): Result<List<ToDoItem>> {
        return try {
            val serverResult = apiService.getToDoList()

            if (serverResult.isSuccess) {
                val serverResponse = serverResult.getOrThrow()
                updateRevision(serverResponse.revision)

                val request = localItems.toToDoListRequest()

                val updateResult = apiService.updateToDoList(serverResponse.revision, request)

                if (updateResult.isSuccess) {
                    val updateResponse = updateResult.getOrThrow()
                    updateRevision(updateResponse.revision)
                    val mergedItems = updateResponse.list.toToDoItems()
                    Result.success(mergedItems)
                } else {
                    Result.failure(updateResult.exceptionOrNull()!!)
                }
            } else {
                Result.failure(serverResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun addItem(item: ToDoItem): Result<ToDoItem> {
        return try {
            var revision = getCurrentRevision()
            if (revision == null) {
                val initResult = initializeRevision()
                if (initResult.isFailure) {
                    return Result.failure(initResult.exceptionOrNull()!!)
                }
                revision = initResult.getOrThrow()
            }

            val request = item.toToDoItemElementRequest()


            val result = apiService.addToDoItem(revision, request)

            if (result.isSuccess) {
                val response = result.getOrThrow()
                updateRevision(response.revision)
                Result.success(response.element.toToDoItem())
            } else {
                Result.failure(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateItem(item: ToDoItem): Result<ToDoItem> {
        return try {
            val revision = getCurrentRevision() ?: return Result.failure(Exception("No revision available"))

            val request = item.toToDoItemUpdateRequest()
            val result = apiService.updateToDoItem(item.uid, revision, request)

            if (result.isSuccess) {
                val response = result.getOrThrow()
                updateRevision(response.revision)
                Result.success(response.element.toToDoItem())
            } else {
                Result.failure(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteItem(itemId: String): Result<ToDoItem> {
        return try {
            val revision = getCurrentRevision() ?: return Result.failure(Exception("No revision available"))

            val result = apiService.deleteToDoItem(itemId, revision)

            if (result.isSuccess) {
                val response = result.getOrThrow()
                updateRevision(response.revision)
                Result.success(response.element.toToDoItem())
            } else {
                Result.failure(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}