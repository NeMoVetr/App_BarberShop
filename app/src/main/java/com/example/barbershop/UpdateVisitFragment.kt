package com.example.barbershop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.app.DatePickerDialog
import android.util.Log
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.barbershop.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.text.SimpleDateFormat

class UpdateVisitFragment : Fragment(R.layout.fragment_update_visit) {

    private lateinit var apiRepository: ApiRepository // Инициализация репозитория
    private lateinit var visit: VisitListResponse // Инициализация визита

    private lateinit var employeeSpinner: Spinner // Инициализация списка сотрудников
    private lateinit var serviceSpinner: Spinner // Инициализация списка услуг
    private lateinit var selectDateText: EditText // Инициализация поля выбора даты
    private lateinit var selectedDate: String // Инициализация выбранной даты
    private lateinit var timeSpinner: Spinner // Инициализация списка времени
    private lateinit var updateButton: Button // Инициализация кнопки обновления


    // Метод для создания и инициализации фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Создание корневого представления для фрагмента
        val rootView = inflater.inflate(R.layout.fragment_update_visit, container, false)

        // Инициализация репозитория для работы с API
        apiRepository = ApiRepository(SessionManager(requireContext()))

        // Инициализация UI элементов
        employeeSpinner = rootView.findViewById(R.id.spEmployeeUpdateVisit)
        serviceSpinner = rootView.findViewById(R.id.spServiceUpdateVisit)
        selectDateText = rootView.findViewById(R.id.etSelectDateUpdateVisit)
        timeSpinner = rootView.findViewById(R.id.spTimeUpdateVisit)
        updateButton = rootView.findViewById(R.id.button_update)

        // Получение данных визита
        visit = arguments?.getSerializable("visit") as VisitListResponse

        // Получение и установка даты визита
        selectedDate = getDateVisits()
        selectDateText.setText(selectedDate)

        // Получение услуг из базы данных
        fetchServices()


        // Установка обработчиков кликов
        selectDateText.setOnClickListener {
            showDatePickerDialog()
        }

        // Установка обработчиков фокусов
        setupListeners()

        // Обработка кнопки обновления визита
        updateButton.setOnClickListener {
            val visitUpdate = handleUpdate() // Получение данных для обновления визита
            if (visitUpdate != null) {
                updateVisit(visitUpdate) // Обновление визита
            } else {
                // Показать сообщение, если не все поля заполнены
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    // Загрузка списка услуг из API
    private fun fetchServices() {
        // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
        lifecycleScope.launch(Dispatchers.IO) {
            // Выполнение запроса к API
            val response = apiRepository.getServices().execute()
            withContext(Dispatchers.Main) { // Возврат в главный поток
                if (response.isSuccessful) { // Если запрос успешен
                    val services = response.body() ?: emptyList() // Получение списка услуг
                    Log.d("Service", services.toString())
                    updateServicesSpinner(services) // Обновление спиннера с услугами
                } else {
                    // Показать сообщение об ошибке
                    Toast.makeText(
                        requireContext(),
                        "Ошибка загрузки услуг",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Преобразование строки даты визита в нужный формат
    private fun getDateVisits(): String {
        val dateTimeString = visit.date_time // Строка с датой и временем визита
        val inputFormat =
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())  // Формат входной строки
        val outputFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Формат преобразованой строки
        val date = inputFormat.parse(dateTimeString) // Преобразование строки в Date
        return outputFormat.format(date!!) // Форматирование в нужный вид
    }

    // Окно выбора даты
    private fun showDatePickerDialog() {
        // Инициализация диалога выбора даты
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR) // Год
        val month = calendar.get(Calendar.MONTH) // Месяц
        val day = calendar.get(Calendar.DAY_OF_MONTH) // День

        // Создание диалога выбора даты
        val datePickerDialog = DatePickerDialog(
            requireContext(), // Контекст активности
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate =
                    "$selectedYear-${selectedMonth + 1}-$selectedDay" // Обновление выбранной даты
                selectDateText.setText(selectedDate) // Установка текста в поле


            },
            year,
            month,
            day
        )

        // Установка ограничений на выбор даты
        calendar.add(Calendar.DAY_OF_MONTH, 7)
        datePickerDialog.datePicker.minDate =
            System.currentTimeMillis() // Минимальная дата - текущая
        datePickerDialog.datePicker.maxDate =
            calendar.timeInMillis // Максимальная дата - через 7 дней

        datePickerDialog.show() // Отображение диалогового окна


    }

    // Настройка обработчиков для спиннеров
    private fun setupListeners() {

        // Обработчик потери фокуса для employeeSpinner
        employeeSpinner.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Получение выбранного сотрудника
                val employeeId = (employeeSpinner.selectedItem as? Employee)?.id
                // Получение выбранной услуги
                val serviceId = (serviceSpinner.selectedItem as? Service)?.id


                // Если выбран сотрудник и услуга, обновляем доступное время
                if (employeeId != null && serviceId != null) {

                    fetchAvailableTime(
                        serviceId,
                        employeeId,
                        selectedDate
                    ) // Перезапросить доступное время при выборе сотрудника
                }
            }
        }

        // Обработчик изменения выбранного элемента для employeeSpinner (если нужно)
        employeeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Получение выбранного сотрудника
                val employeeId = (parent.getItemAtPosition(position) as? Employee)?.id

                // Получение выбранной услуги
                val serviceId = (serviceSpinner.selectedItem as? Service)?.id

                // Если выбран сотрудник и услуга, обновляем доступное время
                if (employeeId != null && serviceId != null) {

                    fetchAvailableTime(
                        serviceId,
                        employeeId,
                        selectedDate
                    ) // Обновляем доступное время при изменении сотрудника
                }
            }

            // Обработка случая, когда ничего не выбрано
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Обработчик потери фокуса
        serviceSpinner.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Получение выбранной услуги
                val serviceId = (serviceSpinner.selectedItem as? Service)?.id

                Log.d("serviceId1", serviceId.toString())

                // Если выбрана услуга, обновляем список сотрудников
                if (serviceId != null) {
                    fetchEmployeesForService(serviceId) // Получение сотрудников для услуги
                }
            }
        }

        // Обработчик изменения выбранного элемента
        serviceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Получение выбранной услуги
                val serviceId = (parent.getItemAtPosition(position) as? Service)?.id

                // Если выбрана услуга, обновляем список сотрудников
                if (serviceId != null) {
                    Log.d("serviceId1", serviceId.toString())
                    fetchEmployeesForService(serviceId) // Получение сотрудников для услуги
                }
            }

            // Обработка случая, когда ничего не выбрано
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    // Загрузка списка сотрудников для выбранной услуги
    private fun fetchEmployeesForService(serviceId: Int) {
        // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
        lifecycleScope.launch(Dispatchers.IO) {
            // Выполнение запроса
            val response = apiRepository.getEmployeeForService(serviceId)
            withContext(Dispatchers.Main) {// Обновление UI в главном потоке
                if (response.isSuccessful) { // Если запрос успешен
                    val employees = response.body() as? List<Employee>
                        ?: emptyList() // Получение списка сотрудников
                    updateEmployeeSpinner(employees) // Обновление спиннера сотрудников
                } else {
                    // Показать сообщение об ошибке
                    Toast.makeText(
                        requireContext(),
                        "Ошибка загрузки сотрудников",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    // Обновление спиннера сотрудников
    private fun updateEmployeeSpinner(employees: List<Employee>) {
        // Создаем адаптер для спиннера
        val adapter = EmployeeSpinnerAdapter(requireContext(), employees)
        employeeSpinner.adapter = adapter // Назначаем адаптер спиннеру
    }

    // Обновление спиннера услуг
    private fun updateServicesSpinner(services: List<Service>) {

        // Создаем адаптер для спиннера
        val adapter = ServiceSpinnerAdapter(requireContext(), services)
        serviceSpinner.adapter = adapter // Назначаем адаптер спиннеру
    }

    // Загрузка доступного времени для выбранного сотрудника и услуги
    private fun fetchAvailableTime(serviceId: Int?, employeeId: Int?, selectedDate: String?) {
        if (selectedDate == null) return // Если дата не выбрана, выходим


        if (employeeId != null && serviceId != null) {
            // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
            lifecycleScope.launch(Dispatchers.IO) {
                // Выполнение запроса
                val response = apiRepository.getAvailableTime(
                    employeeId = employeeId,
                    serviceId = serviceId,
                    date = selectedDate
                )
                // Обновление UI в главном потоке
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) { // Если запрос успешен
                        val availableTime = response.body() as? List<String>
                            ?: emptyList() // Получение доступного времени
                        updateAvailableTimeSpinner(availableTime) // Обновление спиннера доступного времени
                    } else {
                        // Показать сообщение об ошибке
                        Toast.makeText(
                            requireContext(),
                            "Ошибка загрузки доступного времени",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    // Обновление спиннера доступного времени
    private fun updateAvailableTimeSpinner(availableTime: List<String>) {
        // Создаем адаптер для спиннера
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, availableTime)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner.adapter = adapter // Назначаем адаптер спиннеру
    }

    // Обновление визита через API
    private fun updateVisit(bookingData: Map<String, Any>) {
        // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
        lifecycleScope.launch(Dispatchers.IO) {
            // Получение ID каждого элемента визита
            val employee = bookingData["employeeId"] as Int
            val service = bookingData["serviceId"] as Int
            val date = bookingData["date"] as String
            val time = bookingData["time"] as String

            // Создание объекта для отправки запроса
            val visit_ip = VisitIP(employee, service, date, time)

            // Выполнение запроса
            val response = apiRepository.updateVisit(visit.id, visit_ip).execute()

            // Обновление UI в главном потоке
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) { // Если запрос успешен
                    // Показать сообщение
                    Toast.makeText(
                        requireContext(),
                        "Визит успешно обновлен",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Переход на экран истории
                    val fragment = VisitShowFragment() // Фрагмент отображения визитов
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null) // Позволяет вернуться назад
                        .commit()
                } else {
                    // Показать сообщение об ошибке
                    Toast.makeText(
                        requireContext(),
                        "Ошибка обновления визита",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Сбор данных для обновления визита
    private fun handleUpdate(): Map<String, Any>? {
        // Получаем выбранные элементы
        val employee = employeeSpinner.selectedItem as? Employee
        val service = serviceSpinner.selectedItem as? Service
        val selectedTime = timeSpinner.selectedItem as? String

        // Если какое-либо поле не заполнено, возвращаем null
        if (employee == null || service == null || selectedTime == null) {
            return null
        }

        // Возвращаем данные в виде словаря
        return mapOf(
            "employeeId" to employee.id,
            "serviceId" to service.id,
            "date" to selectedDate,
            "time" to selectedTime
        )
    }
}
