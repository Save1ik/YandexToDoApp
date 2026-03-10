package com.example.yandextodo

enum class Importance {
    low,
    medium,
    hight;

    fun toServerImportance(): String = when (this) {
        low -> "low"
        medium -> "basic"
        hight -> "important"
    }

    companion object {
        fun fromServerImportance(importance: String): Importance = when (importance) {
            "low" -> low
            "basic" -> medium
            "important" -> hight
            else -> medium
        }
    }
}