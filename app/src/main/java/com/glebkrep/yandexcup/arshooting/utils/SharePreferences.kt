package com.glebkrep.yandexcup.arshooting.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object SharePreferences {
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }

    private fun put(key: String, value: Any?) {
        when (value) {
            is String? -> preferences.edit().putString(key, value).apply()
            is Int -> preferences.edit().putInt(key, value).apply()
            is Boolean -> preferences.edit().putBoolean(key, value).apply()
            is Float -> preferences.edit().putFloat(key, value).apply()
            is Long -> preferences.edit().putLong(key, value).apply()
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    private inline fun <reified T : Any> get(key: String): T? {
        return when (T::class) {
            String::class -> preferences.getString(key, null) as T?
            Int::class -> preferences.getInt(key, 0) as T?
            Boolean::class -> preferences.getBoolean(key, false) as T?
            Float::class -> preferences.getFloat(key, 0.0F) as T?
            Long::class -> preferences.getLong(key, 0L) as T?
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    fun getUdid(): String {
        var udid = get<String>("udid")
        if (udid == null || udid == "") {
            udid = UUID.randomUUID().toString().replace("-", "")
            put(
                "udid", udid
            )
        }
        return udid
    }
}