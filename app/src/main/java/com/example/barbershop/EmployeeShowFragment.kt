package com.example.barbershop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barbershop.network.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Фрагмент для отображения списка сотрудников в RecyclerView
class EmployeeShowFragment : Fragment(R.layout.fragment_employee_show) {

    // Объявление переменных для RecyclerView, адаптера и репозитория
    private lateinit var recyclerView: RecyclerView
    private lateinit var employeeAdapter: EmployeeAdapter
    private lateinit var apiRepository: ApiRepository

    // Метод, который вызывается для создания представления фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflating (создание) представления для фрагмента из layout файла
        val rootView = inflater.inflate(R.layout.fragment_employee_show, container, false)

        // Инициализация репозитория для работы с API
        apiRepository = ApiRepository(SessionManager(requireContext()))

        // Инициализация RecyclerView и установка менеджера компоновки (LinearLayoutManager)
        recyclerView = rootView.findViewById(R.id.recyclerViewEmployees)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Инициализация адаптера для списка сотрудников
        employeeAdapter = EmployeeAdapter()
        recyclerView.adapter = employeeAdapter // Установка адаптера для RecyclerView

        // Загрузка данных сотрудников
        loadEmployees()

        // Возвращаем корневое представление фрагмента
        return rootView
    }

    // Функция для загрузки списка сотрудников из API
    private fun loadEmployees() {
        // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
        lifecycleScope.launch(Dispatchers.IO) {
            // Выполнение запроса к API на фоне
            val response = apiRepository.getEmployees().execute()
            if (response.isSuccessful) {
                // Если запрос успешен, извлекаем данные и обновляем адаптер
                val employees = response.body() as? List<Employee> ?: emptyList()
                withContext(Dispatchers.Main) {
                    // Обновление списка сотрудников в адаптере на главном потоке
                    employeeAdapter.submitList(employees)
                }
            } else {
                // Если запрос не успешен, показываем сообщение об ошибке
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка загрузки данных", // Сообщение об ошибке
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

