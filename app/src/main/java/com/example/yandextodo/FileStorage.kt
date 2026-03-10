package com.example.yandextodo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class FileStorage(val file: File) {
    private val _itemList = mutableListOf<ToDoItem>()
    private val _itemsFlow = MutableStateFlow<List<ToDoItem>>(emptyList())

    val itemsFlow: StateFlow<List<ToDoItem>> = _itemsFlow.asStateFlow()

    private val logger: Logger = LoggerFactory.getLogger("FileStorage")

    init {
        loadFromFile()
    }

    val itemList: List<ToDoItem>
            get(){
                logger.info("Получение листа задач ${_itemList}")
                return _itemList
            }

    fun getItem(uid: String): ToDoItem? {
        return _itemList.find { it.uid == uid }
    }

    fun update(item: ToDoItem): Boolean {
        val index = _itemList.indexOfFirst { it.uid == item.uid }
        if (index != -1) {
            logger.info("Обновление задачи: ${item.uid}")
            _itemList[index] = item
            saveAndPublish()
            return true
        }
        return false
    }

    fun addItem(item: ToDoItem): Boolean {
        logger.info("Добавление задачи ${item.text}")
        saveAndPublish()
        return _itemList.add(item)
    }

    private fun saveAndPublish() {
        val jsonItems = JSONArray()
        _itemList.forEach { item ->
            jsonItems.put(item.json)
        }
        file.writeText(jsonItems.toString())
        logger.info("Сохранено в кэш (${file.absolutePath}): ${_itemList.size} задач")

        _itemsFlow.value = _itemList.toList()
    }

    fun replaceAll(newItems: List<ToDoItem>) {
        _itemList.clear()
        _itemList.addAll(newItems)
        saveAndPublish()
    }

    fun removeItem(uid: String): Boolean {
        logger.info("Удаление задачи с ${uid}")
        val removed = _itemList.removeIf { it.uid == uid }
        if (removed) {
            saveAndPublish()
        }
        return removed
    }

    fun saveItems(){
        val jsItems = JSONArray()
        _itemList.forEach { item ->
            jsItems.put(item.json)
        }
        file.writeText(jsItems.toString())
        logger.info("Сохранено ${_itemList.size} задач в ${file.name}")
    }

    fun loadFromFile(){
        if (!file.exists()) {
            logger.warn("Файл кэша не существует: ${file.name}")
            _itemsFlow.value = emptyList()
            return
        }

        val content = file.readText().trim()
        if (content.isEmpty()) {
            logger.warn("Файл кэша пуст")
            _itemList.clear()
            _itemsFlow.value = emptyList()
            return
        }

        try {
            val jsonArray = JSONArray(content)
            _itemList.clear()
            for (i in 0 until jsonArray.length()) {
                if (!jsonArray.isNull(i)) {
                    val jsonItem = jsonArray.getJSONObject(i)
                    val newItem = parse(jsonItem) // ← ваша функция
                    if (newItem != null) {
                        _itemList.add(newItem)
                    }
                }
            }
            logger.info("Загружено из кэша: ${_itemList.size} задач")
            saveAndPublish()
        } catch (e: Exception) {
            logger.error("Ошибка загрузки кэша", e)
            _itemList.clear()
            _itemsFlow.value = emptyList()
        }
    }
}