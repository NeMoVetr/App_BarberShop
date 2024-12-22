package com.example.barbershop.network

import retrofit2.Call
import retrofit2.http.*
import retrofit2.Response


// Интерфейс ApiService, который описывает методы для работы с API
interface ApiService {

    // Метод для выполнения логина пользователя
    @POST("auth/login/") // HTTP POST запрос на эндпоинт "/auth/login/"
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse> // Ожидается ответ с объектом LoginResponse

    // Метод для регистрации клиента
    @POST("registration/client/") // HTTP POST запрос на эндпоинт "/registration/client/"
    fun registerClient(@Body client: Client): Call<ClientResponse> // Ожидается ответ в виде ClientResponse

    // Метод для получения списка сотрудников
    @GET("employee/show/") // HTTP GET запрос на эндпоинт "/employee/show/"
    fun getEmployees(): Call<List<Employee>> // Ожидается ответ с списком сотрудников

    // Метод для обновления данных клиента
    @PATCH("client/update/") // HTTP PATCH запрос на эндпоинт "/client/update/"
    fun updateClient(@Body client: ClientUpdate): Call<Void> // Ожидается ответ без данных (Void)

    // Метод для получения профиля клиента
    @GET("client/profile/") // HTTP GET запрос на эндпоинт "/client/profile/"
    fun getClient(): Call<ClientResponse> // Ожидается ответ с объектом ClientResponse

    // Метод для получения списка залов
    @GET("hall/show/") // HTTP GET запрос на эндпоинт "/hall/show/"
    fun getHalls(): Call<List<Hall>> // Ожидается ответ с списком залов

    // Метод для получения списка доступных услуг
    @GET("service/show/") // HTTP GET запрос на эндпоинт "/service/show/"
    fun getServices(): Call<List<Service>> // Ожидается ответ с списком услуг

    // Метод для бронирования визита
    @POST("book/visit/") // HTTP POST запрос на эндпоинт "/book/visit/"
    fun bookVisit(@Body visit_ip: VisitIP): Call<Void> // Ожидается ответ без данных (Void)

    // Метод для получения доступного времени для услуги
    @GET("get_available_time/") // HTTP GET запрос на эндпоинт "/get_available_time/"
    suspend fun getAvailableTime(
        @Query("service") serviceId: Int, // Параметр запроса с ID услуги
        @Query("employee") employeeId: Int, // Параметр запроса с ID сотрудника
        @Query("date") date: String // Параметр запроса с датой
    ): Response<List<String>> // Ожидается ответ с доступными временами в виде списка строк

    // Метод для получения сотрудников для конкретной услуги
    @GET("get_employee_for_service/") // HTTP GET запрос на эндпоинт "/get_employee_for_service/"
    suspend fun getEmployeeForService(
        @Query("service") serviceId: Int // Параметр запроса с ID услуги
    ): Response<List<Employee>> // Ожидается ответ с списком сотрудников, которые могут предоставить эту услугу

    // Метод для получения списка визитов клиента
    @GET("visit/show/client/") // HTTP GET запрос на эндпоинт "/visit/show/client/"
    fun getVisits(): Call<List<VisitListResponse>> // Ожидается ответ с списком визитов клиента

    // Метод для обновления информации о визите
    @PATCH("visits/{id}/update/") // HTTP PATCH запрос на эндпоинт "/visits/{id}/update/"
    fun updateVisit(
        @Path("id") visitId: Int, // Параметр запроса с ID визита
        @Body visit: VisitIP // Объект с новыми данными для визита
    ): Call<Void> // Ожидается ответ без данных (Void)

    // Метод для удаления визита
    @DELETE("visit/{id}/delete/") // HTTP DELETE запрос на эндпоинт "/visit/{id}/delete/"
    fun deleteVisit(@Path("id") visitId: Int): Call<Void> // Ожидается ответ без данных (Void)
}
