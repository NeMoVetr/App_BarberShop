package com.example.barbershop

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity

import android.view.View

import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barbershop.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.Typeface


// Определение фрагмента для отображения информации о визитах
class VisitShowFragment : Fragment(R.layout.fragment_visit_show) {

    // Инициализация переменных для UI-элементов
    private lateinit var apiRepository: ApiRepository  // Репозиторий для работы с API
    private lateinit var recyclerView: RecyclerView  // RecyclerView для отображения списка визитов
    private lateinit var statusMessage: TextView  // TextView для отображения статуса (например, сообщения о пустом списке)
    private lateinit var tableLayout: TableLayout  // TableLayout для отображения таблицы с визитами
    private lateinit var btnEditProfile: Button  // Кнопка для перехода к экрану записи визита

    // Переопределение метода onViewCreated для инициализации UI и логики
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация элементов UI, связываем их с элементами в макете
        recyclerView = view.findViewById(R.id.recycler_view)
        statusMessage = view.findViewById(R.id.status_message)
        tableLayout = view.findViewById(R.id.table_layout)
        btnEditProfile = view.findViewById(R.id.btnEditBookVisit)

        // Инициализация репозитория для работы с API
        apiRepository = ApiRepository(SessionManager(requireContext()))

        // Загружаем визиты с помощью функции fetchVisits
        fetchVisits()

        // Обработчик для кнопки редактирования профиля: переходим к экрану записи визита
        btnEditProfile.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    BookVisitFragment()
                )  // Заменяем текущий фрагмент на BookVisitFragment
                .addToBackStack(null)  // Добавляем транзакцию в стек назад
                .commit()  // Выполняем транзакцию
        }
    }

    // Функция для загрузки визитов с сервера
    private fun fetchVisits() {
        // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
        lifecycleScope.launch(Dispatchers.IO) {  // Запускаем корутину в фоновом потоке
            val response =
                apiRepository.getVisits().execute()  // Выполняем запрос на получение визитов
            withContext(Dispatchers.Main) {  // Возвращаемся в главный поток для обновления UI
                if (response.isSuccessful && response.body() != null) {  // Проверка успешности ответа
                    val visits = response.body()  // Получаем список визитов
                    if (visits != null) {
                        setupUI(visits)  // Настроить UI, если визиты успешно получены
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Данные недоступны",  // Показываем сообщение, если визиты пусты
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    // Функция для настройки UI с полученными визитами
    private fun setupUI(visitResponse: List<VisitListResponse>) {
        val plannedVisits =
            visitResponse.filter { it.status == "Запланирована" }  // Отбираем запланированные визиты

        // Присваиваем JSON-данные для таблицы
        val jsonDfDataVisit = visitResponse

        if (plannedVisits.isEmpty()) {  // Если нет запланированных визитов
            statusMessage.visibility = View.VISIBLE  // Показываем сообщение о статусе
            recyclerView.visibility = View.GONE  // Скрываем список визитов
        } else {  // Если есть запланированные визиты
            statusMessage.visibility = View.GONE  // Скрываем сообщение о статусе
            recyclerView.visibility = View.VISIBLE  // Показываем список визитов
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext())  // Устанавливаем менеджер компоновки
            recyclerView.adapter = VisitAdapter(  // Устанавливаем адаптер для RecyclerView
                plannedVisits,
                onEditClick = { visit ->  // Обработчик клика на редактирование
                    val bundle = Bundle().apply {
                        putSerializable("visit", visit)  // Передаем данные визита в новый фрагмент
                    }
                    val updateVisitFragment = UpdateVisitFragment().apply {
                        arguments = bundle  // Устанавливаем аргументы для фрагмента
                    }
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            updateVisitFragment
                        )  // Заменяем фрагмент на UpdateVisitFragment
                        .addToBackStack(null)  // Добавляем транзакцию в стек назад
                        .commit()  // Выполняем транзакцию
                },
                onDeleteClick = { visit ->  // Обработчик клика на удаление
                    deleteVisit(visit.id)  // Удаляем визит по id
                }
            )
        }

        // Если данные для таблицы есть, показываем таблицу
        if (jsonDfDataVisit.isNotEmpty()) {
            tableLayout.visibility = View.VISIBLE  // Показываем таблицу
            displayTable(jsonDfDataVisit)  // Отображаем таблицу с данными
        } else {
            tableLayout.visibility = View.GONE  // Скрываем таблицу, если данных нет
        }
    }

    // Функция для удаления визита
    private fun deleteVisit(visitId: Int) {
        // Показать диалоговое окно для подтверждения удаления визита
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Отменить посещение")  // Заголовок диалога
            .setMessage("Вы уверены, что хотите удалить визит?")  // Сообщение для подтверждения
            .setPositiveButton("Да") { _, _ ->  // При нажатии на "Да"
                lifecycleScope.launch(Dispatchers.IO) { // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
                    val response = apiRepository.deleteVisit(visitId)
                        .execute()  // Отправляем запрос на удаление визита
                    withContext(Dispatchers.Main) {  // Возвращаемся в главный поток
                        if (response.isSuccessful) {  // Проверка успешности удаления
                            Toast.makeText(
                                requireContext(),
                                "Визит успешно удален",  // Сообщение об успешном удалении
                                Toast.LENGTH_SHORT
                            ).show()

                            // Обновляем список визитов после удаления
                            fetchVisits()
                        } else {  // Если произошла ошибка при удалении
                            Toast.makeText(
                                requireContext(),
                                "Ошибка при удалении визита",  // Сообщение об ошибке
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }.setNegativeButton("Нет", null)  // При нажатии на "Нет" ничего не происходит
            .show()  // Показываем диалог
    }

    // Функция для отображения таблицы с визитами
    private fun displayTable(visitResponse: List<VisitListResponse>) {
        // Очищаем старые строки в таблице
        tableLayout.removeAllViews()

        // Создаем строку заголовков таблицы
        val headerRow = TableRow(requireContext()).apply {

            val employeeNameHeader =
                createTextView("Мастер", isHeader = true)  // Заголовок для имени сотрудника
            val serviceNameHeader =
                createTextView("Услуга", isHeader = true)  // Заголовок для услуги
            val servicePriceHeader =
                createTextView("Цена, руб.", isHeader = true)  // Заголовок для цены
            val dateTimeHeader =
                createTextView("Дата и время", isHeader = true)  // Заголовок для даты и времени

            // Добавляем заголовки в строку
            addView(employeeNameHeader)
            addView(serviceNameHeader)
            addView(servicePriceHeader)
            addView(dateTimeHeader)
        }
        tableLayout.addView(headerRow)  // Добавляем строку заголовков в таблицу

        // Заполняем таблицу данными визитов
        visitResponse.forEach { visit ->  // Для каждого визита из списка
            val tableRow = TableRow(requireContext()).apply {

                val employeeName = createTextView(visit.employee_name)  // Имя сотрудника
                val serviceName = createTextView(visit.service_name)  // Название услуги
                val servicePrice = createTextView(visit.service_price.toString())  // Цена услуги
                val dateTime = createTextView(visit.date_time)  // Дата и время визита

                // Добавляем данные в строку таблицы
                addView(employeeName)
                addView(serviceName)
                addView(servicePrice)
                addView(dateTime)
            }

            tableLayout.addView(tableRow)  // Добавляем строку данных в таблицу
        }
    }

    // Вспомогательная функция для создания TextView
    private fun createTextView(text: String, isHeader: Boolean = false): TextView {
        return TextView(requireContext()).apply {
            this.text = text  // Устанавливаем текст для TextView
            setPadding(16, 8, 16, 8)  // Устанавливаем отступы
            gravity = Gravity.CENTER  // Выравнивание текста по центру
            layoutParams = TableRow.LayoutParams(  // Параметры для размещения в таблице
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1.0f // Равномерное распределение ширины
            )

            if (isHeader) {  // Если это заголовок, делаем текст жирным
                setTypeface(null, Typeface.BOLD)
            }
        }
    }
}

