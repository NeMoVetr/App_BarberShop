package com.example.barbershop


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.barbershop.network.SessionManager

// Главная активность приложения
class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager  // Менеджер сессий для управления авторизацией

    // Метод для создания активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Устанавливаем макет для активности

        sessionManager =
            SessionManager(this)  // Инициализация SessionManager для работы с токеном и состоянием авторизации

        // Проверяем, авторизован ли пользователь
        if (!sessionManager.isLoggedIn()) {
            // Если не авторизован, показываем экран входа
            replaceFragment(LoginFragment())
        } else {
            // Если авторизован, переходим на экран профиля
            replaceFragment(ProfileClientFragment())
        }
    }

    // Метод для создания меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Если пользователь авторизован, отображаем меню с навигацией
        if (sessionManager.isLoggedIn()) {
            menuInflater.inflate(R.menu.bottom_nav_menu, menu)  // Загружаем меню
        }
        return true
    }

    // Метод для обновления меню при изменении статуса авторизации
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()  // Очищаем текущее меню

        // Если пользователь авторизован, показываем меню
        if (sessionManager.isLoggedIn()) {
            menuInflater.inflate(R.menu.bottom_nav_menu, menu)  // Загружаем меню
        }
        return super.onPrepareOptionsMenu(menu)  // Возвращаем результат родительского метода
    }

    // Метод для обработки выбранных пунктов меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            // Обработка нажатия на пункт меню "Сотрудники"
            R.id.nav_employee -> {
                replaceFragment(EmployeeShowFragment())  // Переходим на фрагмент с сотрудниками
                true
            }

            // Обработка нажатия на пункт меню "Залы"
            R.id.nav_hall -> {
                replaceFragment(HallShowFragment())  // Переходим на фрагмент с залами
                true
            }

            // Обработка нажатия на пункт меню "Профиль"
            R.id.nav_profile -> {
                replaceFragment(ProfileClientFragment())  // Переходим на фрагмент профиля клиента
                true
            }

            // Обработка нажатия на пункт меню "Услуги"
            R.id.nav_service -> {
                replaceFragment(ServiceFragment())  // Переходим на фрагмент с услугами
                true
            }

            // Обработка нажатия на пункт меню "Посещения"
            R.id.nav_visit -> {
                replaceFragment(VisitShowFragment())  // Переходим на фрагмент с посещениями
                true
            }

            // Обработка нажатия на пункт меню "Выход"
            R.id.item_logout -> {
                logoutUser()  // Выполняем выход из системы
                true
            }

            else -> super.onOptionsItemSelected(item)  // Возвращаем результат для других пунктов меню
        }
    }

    // Метод для замены текущего фрагмента
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)  // Заменяем фрагмент в контейнере
            .commit()  // Выполняем транзакцию
    }

    // Метод для выхода из системы
    private fun logoutUser() {
        sessionManager.clearAuthToken()  // Очищаем токен авторизации
        Toast.makeText(this, "Вы вышли из системы", Toast.LENGTH_SHORT)
            .show()  // Показываем сообщение о выходе

        // Перезагружаем фрагмент LoginFragment
        replaceFragment(LoginFragment())
        invalidateOptionsMenu()  // Обновляем меню
    }
}

