package com.example.yandextodo

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

val ToDoItem.json: JSONObject
    get(){
        return getJsonFromItem(item = this)
    }

fun getJsonFromItem(item:ToDoItem): JSONObject {
    val jsonItem = JSONObject()
    jsonItem.put("uid", item.uid)
    jsonItem.put("text", item.text)
    jsonItem.put("status", item.isDone)

    if(item.color != null)
        jsonItem.put("color", item.color.toArgb())

    if(item.importance != Importance.medium)
        jsonItem.put("importance", item.importance)

    if(item.deadline != null)
        jsonItem.put("deadline", item.deadline.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))

    return jsonItem
}

fun parse(jsItem: JSONObject):ToDoItem
{
    val uid = jsItem.getString("uid")
    val text = jsItem.getString("text")
    val isDone = jsItem.getBoolean("status")

    var deadline:LocalDateTime? = null
    if(jsItem.has("deadline"))
        deadline = LocalDateTime.parse(jsItem.getString("deadline"), DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    var color:Color = Color.White
    if(jsItem.has("color"))
        color = Color(jsItem.getInt("color"))

    val importance = Importance.values()
        .find { it.name == jsItem.optString("importance") }
        ?: Importance.medium

    return ToDoItem(
        importance=importance,
        uid = uid,
        text = text,
        deadline = deadline,
        isDone = isDone,
        color = color)
}