package com.example.barbershop

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.barbershop.network.ApiRepository
import com.example.barbershop.network.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Фрагмент для отображения экрана авторизации
class LoginFragment : Fragment(R.layout.fragment_login) {

    // Переменные для UI элементов и репозиториев
    private lateinit var apiRepository: ApiRepository  // Репозиторий для работы с API
    private lateinit var sessionManager: SessionManager  // Менеджер сессий для работы с токеном
    private lateinit var etUsername: EditText  // Поле для ввода логина
    private lateinit var etPassword: EditText  // Поле для ввода пароля
    private lateinit var btnLogin: Button  // Кнопка для входа
    private lateinit var btnRegister: Button  // Кнопка для перехода к регистрации

    // Метод, вызываемый при создании представления фрагмента
    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация менеджера сессий и репозитория API
        sessionManager = SessionManager(requireContext())
        apiRepository = ApiRepository(sessionManager)

        // Инициализация UI элементов
        etUsername = view.findViewById(R.id.etUsernameLogin)
        etPassword = view.findViewById(R.id.etPasswordLogin)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnRegister = view.findViewById(R.id.btnRegister)

        // Обработка клика по кнопке "Войти"
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()  // Получаем логин
            val password = etPassword.text.toString()  // Получаем пароль

            // Проверяем, чтобы поля не были пустыми
            if (username.isNotBlank() && password.isNotBlank()) {
                login(username, password)  // Пытаемся выполнить вход
            } else {
                Toast.makeText(requireContext(), "Введите логин и пароль", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Обработка клика по кнопке "Зарегистрироваться"
        btnRegister.setOnClickListener {
            val registerFragment = RegisterFragment()  // Создаем фрагмент для регистрации
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    registerFragment
                )  // Заменяем текущий фрагмент на RegisterFragment
                .addToBackStack(null)  // Добавляем фрагмент в стек возврата
                .commit()  // Выполняем транзакцию
        }
    }

    // Метод для выполнения авторизации с логином и паролем
    private fun login(username: String, password: String) {
        // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
        lifecycleScope.launch(Dispatchers.IO) {
            // Отправляем запрос на авторизацию через API
            val response = apiRepository.login(username, password)

            // Переход в главный поток для обновления UI
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val token = response.body()?.token  // Получаем токен из ответа
                    token?.let {
                        sessionManager.saveAuthToken(it)  // Сохраняем токен в менеджере сессий
                        Toast.makeText(requireContext(), "Авторизация успешна", Toast.LENGTH_SHORT)
                            .show()

                        // Переход на фрагмент профиля клиента после успешного входа
                        val profileFragment = ProfileClientFragment()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                profileFragment
                            )  // Заменяем текущий фрагмент на ProfileClientFragment
                            .commit()  // Выполняем транзакцию

                        // Обновляем меню
                        requireActivity().invalidateOptionsMenu()
                    }
                } else {
                    // Если авторизация не успешна, показываем сообщение об ошибке
                    Toast.makeText(requireContext(), "Ошибка авторизации", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    // Метод для замены фрагмента
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)  // Заменяем текущий фрагмент на переданный
            .addToBackStack(null)  // Добавляем фрагмент в стек возврата
            .commit()  // Выполняем транзакцию
    }
}

