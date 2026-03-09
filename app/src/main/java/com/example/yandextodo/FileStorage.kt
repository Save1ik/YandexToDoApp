package com.example.yandextodo

import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class FileStorage(val file: File) {
    private val _itemList = mutableListOf<ToDoItem>()

    private val logger: Logger = LoggerFactory.getLogger("FileStorage")

    val itemList: List<ToDoItem>
            get(){
                logger.info("Получение листа задач ${_itemList}")
                return _itemList
            }

    fun addItem(item: ToDoItem): Boolean {
        logger.info("Добавление задачи ${item.text}")
        return _itemList.add(item)
    }

    fun removeItem(uid: String): Boolean {
        logger.info("Удаление задачи с ${uid}")
        return _itemList.removeIf { it.uid == uid }
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
        val jsItems = JSONArray(file.readText())
        _itemList.clear()
        for (i in 0 until jsItems.length()) {
            val jsonItem = jsItems.getJSONObject(i)
            val newItem = parse(jsonItem)
            _itemList.add(newItem)
        }
        logger.info("Загружено ${_itemList.size} задач из ${file.name}")
    }
}