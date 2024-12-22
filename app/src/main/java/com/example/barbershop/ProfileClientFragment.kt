package com.example.barbershop


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.barbershop.network.*

import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Определение фрагмента с макетом, который отображает профиль клиента
class ProfileClientFragment : Fragment(R.layout.fragment_profile_client) {

    // Инициализация переменных для доступа к UI элементам
    private lateinit var apiRepository: ApiRepository // Репозиторий для работы с API
    private lateinit var tvUsername: TextView // Текстовое поле для отображения имени пользователя
    private lateinit var tvFirstName: TextView // Текстовое поле для отображения имени клиента
    private lateinit var tvLastName: TextView // Текстовое поле для отображения фамилии клиента
    private lateinit var tvEmail: TextView // Текстовое поле для отображения электронной почты клиента
    private lateinit var tvPhoneNumber: TextView // Текстовое поле для отображения номера телефона клиента
    private lateinit var tvDateOfBirth: TextView // Текстовое поле для отображения даты рождения клиента
    private lateinit var tvGender: TextView // Текстовое поле для отображения пола клиента
    private lateinit var btnEditProfile: Button // Кнопка для редактирования профиля

    // Метод вызывается при создании представления фрагмента
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация UI элементов с помощью findViewById для связи с элементами из макета
        tvUsername = view.findViewById(R.id.tvUsernameProfile)
        tvFirstName = view.findViewById(R.id.tvFirstNameProfile)
        tvLastName = view.findViewById(R.id.tvLastNameProfile)
        tvEmail = view.findViewById(R.id.tvEmailProfile)
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumberProfile)
        tvDateOfBirth = view.findViewById(R.id.tvDateOfBirthProfile)
        tvGender = view.findViewById(R.id.tvGenderProfile)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)

        // Инициализация репозитория для работы с API с использованием SessionManager
        apiRepository = ApiRepository(SessionManager(requireContext()))

        // Загружаем данные профиля клиента
        loadClientProfile()

        // Устанавливаем обработчик нажатия на кнопку редактирования профиля
        btnEditProfile.setOnClickListener {
            // Создаем новый фрагмент для редактирования профиля
            val fragment = UpdateClientFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                // Заменяем текущий фрагмент на новый фрагмент редактирования
                .replace(R.id.fragment_container, fragment)
                // Добавляем в стек назад, чтобы можно было вернуться к предыдущему фрагменту
                .addToBackStack(null)
                .commit() // Выполняем транзакцию
        }
    }

    // Метод для загрузки данных профиля клиента
    private fun loadClientProfile() {
        // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
        lifecycleScope.launch(Dispatchers.IO) {
            // Запрос к API для получения данных клиента
            val response = apiRepository.getClient().execute()
            // Проверка успешности ответа от сервера
            if (response.isSuccessful && response.body() != null) {
                val client = response.body() // Получаем тело ответа (данные клиента)
                // Переходим на главный поток для обновления UI
                withContext(Dispatchers.Main) {
                    if (client != null) {
                        // Если данные клиента получены, заполняем UI
                        populateClientData(client)
                    } else {
                        // Если данные клиента не получены, показываем ошибку
                        Toast.makeText(
                            requireContext(),
                            "Ошибка загрузки профиля",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                // Если запрос не успешен, показываем ошибку
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка загрузки профиля",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Метод для заполнения UI данными клиента
    private fun populateClientData(client: ClientResponse) {
        // Заполнение текстовых полей данными клиента
        tvUsername.text = client.user.username
        tvFirstName.text = client.user.first_name
        tvLastName.text = client.user.last_name
        tvEmail.text = client.user.email
        tvPhoneNumber.text = client.phone_number
        tvDateOfBirth.text = client.date_of_birth

        // Установка текста для пола клиента: "Мужской" или "Женский"
        tvGender.text = if (client.gender.name == "Male") "Мужской" else "Женский"
    }
}
