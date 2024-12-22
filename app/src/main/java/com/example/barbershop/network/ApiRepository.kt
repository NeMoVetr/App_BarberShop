package com.example.barbershop.network


import retrofit2.Response

// Класс ApiRepository, который служит для взаимодействия с API через ApiService
class ApiRepository(private val sessionManager: SessionManager) {

    // Ленивая инициализация apiService, который используется для отправки запросов API
    private val apiService: ApiService by lazy {
        // Получаем экземпляр ApiService с помощью Retrofit, передаем sessionManager для авторизации
        ApiClient.getRetrofitInstance(sessionManager).create(ApiService::class.java)
    }

    // Функция для получения доступного времени для услуги с указанными параметрами
    suspend fun getAvailableTime(
        serviceId: Int, // Идентификатор услуги
        employeeId: Int, // Идентификатор сотрудника
        date: String // Дата
    ): Response<List<String>> {
        // Отправка запроса API для получения доступного времени
        return apiService.getAvailableTime(serviceId, employeeId, date)
    }

    // Функция для получения сотрудников, которые могут предоставить услугу с указанным ID
    suspend fun getEmployeeForService(serviceId: Int) = apiService.getEmployeeForService(serviceId)

    // Функция для выполнения логина пользователя
    suspend fun login(username: String, password: String): Response<LoginResponse> {
        // Создаем объект запроса с данными для логина
        val loginRequest = LoginRequest(username, password)
        // Отправляем запрос на сервер для аутентификации
        return apiService.login(loginRequest)
    }

    // Функция для получения списка сотрудников
    fun getEmployees() = apiService.getEmployees()

    // Функция для регистрации нового клиента
    fun registerClient(client: Client) = apiService.registerClient(client)

    // Функция для обновления данных клиента
    fun updateClient(client: ClientUpdate) = apiService.updateClient(client)

    // Функция для получения информации о клиенте
    fun getClient() = apiService.getClient()

    // Функция для бронирования визита
    fun bookVisit(visit_ip: VisitIP) = apiService.bookVisit(visit_ip)

    // Функция для получения списка визитов
    fun getVisits() = apiService.getVisits()

    // Функция для обновления информации о визите
    fun updateVisit(visitId: Int, visit_ip: VisitIP) = apiService.updateVisit(visitId, visit_ip)

    // Функция для удаления визита
    fun deleteVisit(visitId: Int) = apiService.deleteVisit(visitId)

    // Функция для получения информации о залах
    fun getHalls() = apiService.getHalls()

    // Функция для получения списка доступных услуг
    fun getServices() = apiService.getServices()
}
