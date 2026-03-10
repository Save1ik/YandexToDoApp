package com.example.yandextodo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoItemDao {

    @Query("SELECT * FROM todo_items ORDER BY changedAt DESC")
    fun getAll(): Flow<List<ToDoItemEntity>>

    @Query("SELECT * FROM todo_items WHERE uid = :uid")
    suspend fun getById(uid: String): ToDoItemEntity?

    @Query("SELECT * FROM todo_items WHERE uid = :uid")
    fun getByIdFlow(uid: String): Flow<ToDoItemEntity?>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(item: ToDoItemEntity)

    @Update
    suspend fun update(item: ToDoItemEntity)

    @Delete
    suspend fun delete(item: ToDoItemEntity)

    @Query("DELETE FROM todo_items WHERE uid = :uid")
    suspend fun deleteById(uid: String)

    @Query("DELETE FROM todo_items")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(items: List<ToDoItemEntity>)

    @Query("SELECT * FROM todo_items ORDER BY changedAt DESC")
    suspend fun getAllSnapshot(): List<ToDoItemEntity>
}