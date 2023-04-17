package com.eternaljust.msea.utils

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

fun Parcelable.toJson(): String {
    return Gson().toJson(this)
}

inline fun <reified T> String.fromJson(): T? {
    return try {
        Gson().fromJson(this, T::class.java)
    } catch (e: JsonSyntaxException) {
        println("Gson failed: ${e.message}")
        null
    }
}