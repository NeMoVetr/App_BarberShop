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
import com.example.barbershop.network.ApiRepository
import com.example.barbershop.network.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Фрагмент для отображения списка залов
class HallShowFragment : Fragment(R.layout.fragment_hall_show) {

    private lateinit var recyclerView: RecyclerView  // RecyclerView для отображения списка залов
    private lateinit var hallAdapter: HallAdapter    // Адаптер для залов
    private lateinit var apiRepository: ApiRepository  // Репозиторий для получения данных через API

    // Метод для создания представления фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Инфлятируем layout для фрагмента
        val view = inflater.inflate(R.layout.fragment_hall_show, container, false)

        // Инициализация RecyclerView для отображения данных
        recyclerView = view.findViewById(R.id.recyclerViewHalls)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())  // Настройка менеджера компоновки

        // Инициализация адаптера
        hallAdapter = HallAdapter()
        recyclerView.adapter = hallAdapter  // Устанавливаем адаптер для RecyclerView

        // Инициализация репозитория для работы с API
        apiRepository = ApiRepository(SessionManager(requireContext()))

        // Загружаем данные о залах
        loadHalls()

        return view  // Возвращаем инфлейтированное представление
    }

    // Метод для загрузки данных о залах из API
    private fun loadHalls() {
        // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
        lifecycleScope.launch(Dispatchers.IO) {
            // Выполняем API-запрос для получения списка залов
            val response = apiRepository.getHalls().execute()

            // Проверка успешности ответа от API
            if (response.isSuccessful && response.body() != null) {
                val halls = response.body() ?: emptyList()  // Получаем список залов или пустой список
                withContext(Dispatchers.Main) {
                    hallAdapter.submitList(halls)  // Обновляем список в адаптере
                }
            } else {
                withContext(Dispatchers.Main) {
                    // Если произошла ошибка, показываем сообщение об ошибке
                    Toast.makeText(
                        requireContext(),
                        "Ошибка загрузки залов",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

