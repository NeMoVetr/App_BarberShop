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

class ServiceFragment : Fragment(R.layout.fragment_service) {

    // RecyclerView для отображения списка услуг
    private lateinit var recyclerView: RecyclerView

    // Адаптер для связи данных с RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter

    // Репозиторий для работы с API
    private lateinit var apiRepository: ApiRepository

    // Метод для создания представления фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Возвращаем layout фрагмента
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    // Метод для настройки фрагмента после создания представления
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewServices)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext()) // Устанавливаем LinearLayoutManager для вертикального списка

        // Инициализация адаптера
        serviceAdapter = ServiceAdapter()
        recyclerView.adapter = serviceAdapter // Устанавливаем адаптер для RecyclerView

        // Инициализация репозитория API
        apiRepository = ApiRepository(SessionManager(requireContext()))

        // Загрузка списка услуг
        loadServices()
    }

    // Метод для загрузки списка услуг
    private fun loadServices() {
        // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса

        lifecycleScope.launch(Dispatchers.IO) {
            // Асинхронный запрос к API для получения списка услуг
            val response = apiRepository.getServices().execute()
            if (response.isSuccessful && response.body() != null) {
                // Если запрос успешен, получаем список услуг
                val services = response.body() ?: emptyList()
                withContext(Dispatchers.Main) {
                    // Обновляем адаптер с новыми данными
                    serviceAdapter.submitList(services)
                }
            } else {
                withContext(Dispatchers.Main) {
                    // Если запрос не удался, показываем сообщение об ошибке
                    Toast.makeText(
                        requireContext(),
                        "Ошибка загрузки данных",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

