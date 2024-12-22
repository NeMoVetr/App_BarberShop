package com.example.barbershop.network

import android.content.Context
import android.content.SharedPreferences

// Класс для управления сессией пользователя, использует SharedPreferences для хранения данных
// SharedPreferences - это механизм хранения данных в Android, который позволяет сохранять небольшие объемы информации
class SessionManager(context: Context) {

    // Получение SharedPreferences для хранения данных в локальном хранилище устройства
    private val prefs: SharedPreferences =
        context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    // Сохранение токена авторизации в SharedPreferences
    fun saveAuthToken(token: String) {
        // Используется edit() для изменения данных, затем putString() для записи токена
        prefs.edit().putString("auth_token", token)
            .apply()  // Метод apply() асинхронно сохраняет данные
    }

    // Получение токена авторизации из SharedPreferences
    fun fetchAuthToken(): String? {
        // Возвращается токен из хранилища, если он есть, иначе null
        return prefs.getString("auth_token", null)
    }

    // Проверка, авторизован ли пользователь, основываясь на наличии токена
    fun isLoggedIn(): Boolean {
        // Возвращает true, если токен существует, иначе false
        return fetchAuthToken() != null
    }

    // Очистка токена авторизации при выходе из системы
    fun clearAuthToken() {
        // Удаляет токен из SharedPreferences
        prefs.edit().remove("auth_token").apply()  // Метод apply() асинхронно удаляет данные
    }

}

