package com.example.barbershop.network

import okhttp3.Interceptor
import okhttp3.Response

// Класс AuthInterceptor реализует интерфейс Interceptor для перехвата HTTP-запросов
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    // Переопределяем метод intercept, который будет перехватывать запросы
    override fun intercept(chain: Interceptor.Chain): Response {

        // Создаем новый билд запроса на основе текущего запроса
        val requestBuilder = chain.request().newBuilder()

        // Проверяем, есть ли у нас токен авторизации в sessionManager
        // Если токен существует, добавляем его в заголовок запроса
        sessionManager.fetchAuthToken()?.let { token ->
            // Добавляем заголовок Authorization с токеном в запрос
            requestBuilder.addHeader("Authorization", "Token $token")
        }

        // Отправляем измененный запрос и возвращаем ответ
        // Используем chain.proceed, чтобы продолжить выполнение запроса с изменениями
        return chain.proceed(requestBuilder.build())
    }
}
