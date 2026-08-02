package com.uibuilder.app.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

object JsonUtils {

    fun <T> toJson(moshi: Moshi, value: T, clazz: Class<T>): String {
        return runCatching {
            moshi.adapter(clazz).toJson(value)
        }.getOrDefault("{}")
    }

    fun <T> fromJson(moshi: Moshi, json: String, clazz: Class<T>): T? {
        return runCatching {
            moshi.adapter(clazz).fromJson(json)
        }.getOrNull()
    }

    fun <T> toJsonList(moshi: Moshi, values: List<T>, elementClass: Class<T>): String {
        val type = Types.newParameterizedType(List::class.java, elementClass)
        return runCatching {
            moshi.adapter<List<T>>(type).toJson(values)
        }.getOrDefault("[]")
    }

    fun <T> fromJsonList(moshi: Moshi, json: String, elementClass: Class<T>): List<T> {
        val type = Types.newParameterizedType(List::class.java, elementClass)
        return runCatching {
            moshi.adapter<List<T>>(type).fromJson(json) ?: emptyList()
        }.getOrDefault(emptyList())
    }
}
