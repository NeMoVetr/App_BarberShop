package com.example.barbershop.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.lang.reflect.Type

object ApiClient {
    // Устанавливаем базовый URL для всех запросов API
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // Функция для получения экземпляра Retrofit, который используется для выполнения запросов API
    // Retrofit- автоматически выполняет HTTP-запросы (GET POST PUT PACTH), преобразует ответы в объекты
    fun getRetrofitInstance(sessionManager: SessionManager): Retrofit {

        // Создаем клиент OkHttp с добавленным перехватчиком для авторизации
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager)) // Добавляем перехватчик авторизации
            .build()

        // Создаем объект Gson для сериализации/десериализации объектов
        val gson = GsonBuilder()
            // Регистрируем адаптер для типа Gender, чтобы правильно преобразовывать значения из API
            .registerTypeAdapter(Gender::class.java, object : JsonDeserializer<Gender> {
                // Переопределяем метод десериализации для преобразования строки из API в объект Gender
                override fun deserialize(
                    json: JsonElement,
                    typeOfT: Type,
                    context: JsonDeserializationContext
                ): Gender {
                    val genderStr = json.asString // Получаем строку из JSON
                    return when (genderStr) {
                        "Мужской" -> Gender.Male // Преобразуем строку в объект Male
                        "Женский" -> Gender.Female // Преобразуем строку в объект Female
                        else -> throw IllegalArgumentException("Unknown gender: $genderStr") // Ошибка при неизвестном значении
                    }
                }
            })
            // Регистрируем адаптер для типа Gender для сериализации
            .registerTypeAdapter(Gender::class.java, object : JsonSerializer<Gender> {
                // Переопределяем метод сериализации для преобразования объекта Gender в строку
                override fun serialize(
                    src: Gender,
                    typeOfSrc: Type,
                    context: JsonSerializationContext
                ): JsonElement {
                    return JsonPrimitive(
                        when (src) {
                            Gender.Male -> "Мужской" // Преобразуем Male в строку "Мужской"
                            Gender.Female -> "Женский" // Преобразуем Female в строку "Женский"
                        }
                    )
                }
            })
            // Создаем Gson с зарегистрированными адаптерами
            .create()

        // Строим и возвращаем экземпляр Retrofit с заданным базовым URL, клиентом и конвертером Gson
        return Retrofit.Builder()
            .baseUrl(BASE_URL) // Устанавливаем базовый URL для Retrofit
            .client(client) // Устанавливаем OkHttpClient с перехватчиком
            .addConverterFactory(GsonConverterFactory.create(gson)) // Устанавливаем GsonConverterFactory для обработки JSON
            .build() // Строим Retrofit экземпляр
    }
}

