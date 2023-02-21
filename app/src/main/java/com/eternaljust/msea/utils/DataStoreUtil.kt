package com.eternaljust.msea.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object DataStoreUtil {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "MseaDataStore"
    )
    private lateinit var dataStore: DataStore<Preferences>

    fun init(context: Context) {
        dataStore = context.dataStore
    }

    fun <U> getData(
        key: String,
        default: U
    ): U {
        val res = when (default) {
            is String -> getStringData(key, default)
            is Int -> getIntData(key, default)
            is Boolean -> getBooleanData(key, default)
            else -> throw IllegalArgumentException("This type can't be readied into DataStore")
        }
        @Suppress("UNCHECKED_CAST")
        return res as U
    }

    private suspend fun <U> setData(
        key: String,
        value: U
    ) {
        when (value) {
            is String -> setStringData(key, value)
            is Int -> setIntData(key, value)
            is Boolean -> setBooleanData(key, value)
            else -> throw IllegalArgumentException("This type can't be saved into DataStore")
        }
    }

    fun <U> syncSetData(
        key: String,
        value: U
    ) {
        runBlocking { setData(key, value) }
    }

    private suspend fun <U> removeData(
        key: String,
        value: U
    ) {
        when (value) {
            is String -> removeStringData(key)
            is Int -> removeIntData(key)
            is Boolean -> removeBooleanData(key)
            else -> throw IllegalArgumentException("This type can't be removed into DataStore")
        }
    }

    fun <U> syncRemoveData(
        key: String,
        value: U
    ) {
        runBlocking { removeData(key, value) }
    }

    private fun getStringData(
        key: String,
        default: String = ""
    ): String {
        var value = default
        runBlocking {
            dataStore.data.first {
                value = it[stringPreferencesKey(key)] ?: default
                true
            }
        }
        return value
    }

    private fun getIntData(
        key: String,
        default: Int = 0
    ): Int {
        var value = default
        runBlocking {
            dataStore.data.first {
                value = it[intPreferencesKey(key)] ?: default
                true
            }
        }
        return value
    }

    private fun getBooleanData(
        key: String,
        default: Boolean = false
    ): Boolean {
        var value = default
        runBlocking {
            dataStore.data.first {
                value = it[booleanPreferencesKey(key)] ?: default
                true
            }
        }
        return value
    }

    private suspend fun setStringData(
        key: String,
        value: String
    ) {
        dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    private suspend fun setIntData(
        key: String,
        value: Int
    ) {
        dataStore.edit {
            it[intPreferencesKey(key)] = value
        }
    }

    private suspend fun setBooleanData(
        key: String,
        value: Boolean
    ) {
        dataStore.edit {
            it[booleanPreferencesKey(key)] = value
        }
    }

    private suspend fun removeStringData(key: String) {
        dataStore.edit {
            it.remove(stringPreferencesKey(key))
        }
    }

    private suspend fun removeIntData(key: String) {
        dataStore.edit {
            it.remove(intPreferencesKey(key))
        }
    }

    private suspend fun removeBooleanData(key: String) {
        dataStore.edit {
            it.remove(booleanPreferencesKey(key))
        }
    }

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
}