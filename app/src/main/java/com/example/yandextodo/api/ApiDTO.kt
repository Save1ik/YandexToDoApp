package com.example.yandextodo.api

import androidx.compose.ui.graphics.toArgb
import com.example.yandextodo.Importance
import com.example.yandextodo.ToDoItem

import androidx.compose.ui.graphics.Color as ComposeColor
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@JsonClass(generateAdapter = true)
data class ToDoItemResponse(
    @Json(name = "id") val id: String,
    @Json(name = "text") val text: String,
    @Json(name = "importance") val importance: String,
    @Json(name = "deadline") val deadline: Long? = null,
    @Json(name = "done") val done: Boolean,
    @Json(name = "color") val color: String? = null,
    @Json(name = "created_at") val createdAt: Long,
    @Json(name = "changed_at") val changedAt: Long,
    @Json(name = "last_updated_by") val lastUpdatedBy: String
)

@JsonClass(generateAdapter = true)
data class ToDoListResponse(
    @Json(name = "status") val status: String,
    @Json(name = "list") val list: List<ToDoItemResponse>,
    @Json(name = "revision") val revision: Int
)

@JsonClass(generateAdapter = true)
data class ToDoElementResponse(
    @Json(name = "status") val status: String,
    @Json(name = "element") val element: ToDoItemResponse,
    @Json(name = "revision") val revision: Int
)


@JsonClass(generateAdapter = true)
data class ToDoItemElementRequest(
    @Json(name = "element") val element: ToDoItemRequest
)


@JsonClass(generateAdapter = true)
data class ToDoItemUpdateRequest(
    @Json(name = "element") val element: ToDoItemRequest
)


@JsonClass(generateAdapter = true)
data class ToDoItemRequest(
    @Json(name = "id") val id: String,
    @Json(name = "text") val text: String,
    @Json(name = "importance") val importance: String,
    @Json(name = "deadline") val deadline: Long? = null,
    @Json(name = "done") val done: Boolean,
    @Json(name = "color") val color: String? = null,
    @Json(name = "last_updated_by") val lastUpdatedBy: String = "android-device",
    @Json(name = "changed_at") val changedAt: Long = System.currentTimeMillis() / 1000,
    @Json(name = "created_at") val createdAt: Long = System.currentTimeMillis() / 1000
)


@JsonClass(generateAdapter = true)
data class ToDoListRequest(
    @Json(name = "list") val list: List<ToDoItemRequest>
)

fun ToDoItem.toTodoItemRequest(): ToDoItemRequest {
    val hexColor = if (color != null && color != ComposeColor.White) {
        String.format("#%06X", 0xFFFFFF and color.toArgb())
    } else {
        "#FFFFFF"
    }

    val deadlineSeconds = deadline?.toEpochSecond(ZoneOffset.UTC)

    return ToDoItemRequest(
        id = uid,
        text = text,
        importance = importance.toServerImportance(),
        deadline = deadlineSeconds,
        done = isDone,
        color = hexColor,
        lastUpdatedBy = lastUpdatedBy,
        changedAt = changedAt / 1000,
        createdAt = createdAt / 1000
    )
}

fun ToDoItem.toToDoItemElementRequest(): ToDoItemElementRequest {
    return ToDoItemElementRequest(element = this.toTodoItemRequest())
}

fun ToDoItem.toToDoItemUpdateRequest(): ToDoItemUpdateRequest {
    return ToDoItemUpdateRequest(element = this.toTodoItemRequest())
}

fun ToDoItemResponse.toToDoItem(): ToDoItem {
    val deadline = deadline?.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it), ZoneId.systemDefault())
    }

    val color = if (!color.isNullOrBlank() && color != "#FFFFFF") {
        try {
            ComposeColor(android.graphics.Color.parseColor(color))
        } catch (e: Exception) {
            ComposeColor.White
        }
    } else {
        ComposeColor.White
    }

    return ToDoItem(
        uid = id,
        text = text,
        importance = Importance.fromServerImportance(importance),
        deadline = deadline,
        isDone = done,
        color = color,
        createdAt = createdAt * 1000,
        changedAt = changedAt * 1000,
        lastUpdatedBy = lastUpdatedBy
    )
}

fun List<ToDoItem>.toToDoListRequest(): ToDoListRequest {
    return ToDoListRequest(
        list = this.map { it.toTodoItemRequest() }
    )
}

fun List<ToDoItemResponse>.toToDoItems(): List<ToDoItem> {
    return this.map { it.toToDoItem() }
}