package com.example.barbershop.network


import java.io.Serializable


// Перечисление Gender с возможными значениями: Male (Мужской), Female (Женский)
enum class Gender {
    Male, Female;
}

// Класс данных для информации о пользователе, включающий его ID и имя пользователя
data class UserInfo(
    val id: Int?,             // Идентификатор пользователя (может быть null)
    val username: String?     // Имя пользователя (может быть null)
)

// Ответ на запрос для входа (Login), включающий токен и информацию о пользователе
data class LoginResponse(
    val token: String?,       // Токен для авторизации (может быть null)
    val user: UserInfo?       // Информация о пользователе (может быть null)
)

// Запрос для входа (Login), который включает имя пользователя и пароль
data class LoginRequest(
    val username: String,     // Имя пользователя для входа
    val password: String      // Пароль пользователя для входа
)

// Данные клиента, включающие информацию о пользователе, номере телефона, дате рождения и поле "gender"
data class Client(
    val user: User,                     // Информация о пользователе
    val phone_number: String,           // Номер телефона клиента
    val date_of_birth: String,          // Дата рождения клиента
    val gender: Gender                  // Пол клиента
)

// Данные пользователя для создания учетной записи (включая имя, фамилию, email и пароли)
data class User(
    val username: String,               // Имя пользователя
    val first_name: String,             // Имя пользователя
    val last_name: String,              // Фамилия пользователя
    val email: String,                  // Email пользователя
    val password: String,               // Пароль пользователя
    val password2: String              // Подтверждение пароля пользователя
)

// Ответ на запрос о пользователе, включающий его имя, фамилию, email
data class UserResponse(
    val username: String,               // Имя пользователя
    val first_name: String,             // Имя пользователя
    val last_name: String,              // Фамилия пользователя
    val email: String                   // Email пользователя
)

// Данные для обновления информации о пользователе
data class UserUpdate(
    val first_name: String,             // Имя пользователя
    val last_name: String,              // Фамилия пользователя
    val email: String                   // Email пользователя
)

// Данные для обновления информации о клиенте
data class ClientUpdate(
    val user: UserUpdate,               // Информация для обновления данных пользователя
    val phone_number: String,           // Новый номер телефона клиента
    val date_of_birth: String,          // Новая дата рождения клиента
    val gender: Gender                  // Новый пол клиента
)

// Ответ на запрос о клиенте, включающий ID, информацию о пользователе, номер телефона, дату рождения и пол
data class ClientResponse(
    val id: Int?,                       // Идентификатор клиента (может быть null)
    val user: UserResponse,             // Ответ с данными о пользователе
    val phone_number: String,           // Номер телефона клиента
    val date_of_birth: String,          // Дата рождения клиента
    val gender: Gender                  // Пол клиента
)

// Данные сотрудника, включая ID, информацию о пользователе, номер телефона, позицию, залы и услуги
data class Employee(
    val id: Int,                        // Идентификатор сотрудника
    val user: UserResponse,             // Данные о пользователе
    val phone_number: String,           // Номер телефона сотрудника
    val position: String,               // Должность сотрудника
    val halls: List<Hall>,              // Список залов, в которых работает сотрудник
    val services: List<Service>         // Список услуг, которые предоставляет сотрудник
)

// Данные о зале, включая его ID, описание, вместимость, локацию и рабочие часы
data class Hall(
    val id: Int,                        // Идентификатор зала
    val name: String,                   // Название зала
    val description: String,            // Описание зала
    val capacity: Int,                  // Вместимость зала
    val location: String,               // Локация зала
    val start_time: String,             // Время начала работы зала
    val end_time: String                // Время окончания работы зала
)

// Данные об услуге, включая ID, описание, цену и продолжительность
data class Service(
    val id: Int,                        // Идентификатор услуги
    val name: String,                   // Название услуги
    val description: String,            // Описание услуги
    val price: Double,                  // Цена услуги
    val duration: String                // Продолжительность услуги
)

// Данные для бронирования визита, включая ID сотрудника, услугу, дату и время визита
data class VisitIP(
    val employee: Int,                  // ID сотрудника
    val service: Int,                   // ID услуги
    val date: String,                   // Дата визита
    val time: String                    // Время визита
)

// Ответ на запрос о списке визитов клиента, включая ID, имя сотрудника, услугу и цену
data class VisitListResponse(
    val id: Int,                        // Идентификатор визита
    val employee_name: String,          // Имя сотрудника
    val employee_phone: String,         // Номер телефона сотрудника
    val service_name: String,           // Название услуги
    val service_price: Double,          // Цена услуги
    val date_time: String,              // Дата и время визита
    val status: String,                 // Статус визита
) : Serializable  // Реализация интерфейса Serializable для сериализации данных
