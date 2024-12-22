package com.example.barbershop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.app.DatePickerDialog
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


class BookVisitFragment : Fragment(R.layout.fragment_book_visit) {

    private lateinit var apiRepository: ApiRepository // Инициализация ApiRepository
    private lateinit var employeeSpinner: Spinner // Инициализация Spinner
    private lateinit var serviceSpinner: Spinner // Инициализация Spinner
    private lateinit var selectDateText: EditText // Инициализация EditText
    private lateinit var selectedDate: String  // Инициализация переменной выбранной даты
    private lateinit var timeSpinner: Spinner // Инициализация Spinner
    private lateinit var bookButton: Button // Инициализация Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_book_visit, container, false)

        apiRepository = ApiRepository(SessionManager(requireContext()))

        // Инициализация UI элементов
        employeeSpinner = rootView.findViewById(R.id.spEmployeeBookVisit)
        serviceSpinner = rootView.findViewById(R.id.spServiceBookVisit)
        selectDateText = rootView.findViewById(R.id.etSelectDateBookVisit)
        timeSpinner = rootView.findViewById(R.id.spTimeBookVisit)
        bookButton = rootView.findViewById(R.id.btnBookVisit)


        // Установить текущую дату в selectDateText
        selectedDate = getCurrentDate()
        selectDateText.setText(selectedDate)

        // Получение услуг из базы данных
        fetchServices()

        // Установка обработчиков
        selectDateText.setOnClickListener {
            showDatePickerDialog()
        }

        // Установка обработчиков
        setupListeners()

        // Обработка кнопки
        bookButton.setOnClickListener {
            val visit = handleBooking()
            if (visit != null) {
                bookVisit(visit)
            } else {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                selectDateText.setText(selectedDate)


            },
            year,
            month,
            day
        )

        calendar.add(Calendar.DAY_OF_MONTH, 7)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.show()


    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Форматируем текущую дату в нужный формат
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }


    private fun setupListeners() {

        // Обработчик потери фокуса для employeeSpinner
        employeeSpinner.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val employeeId = (employeeSpinner.selectedItem as? Employee)?.id
                val serviceId = (serviceSpinner.selectedItem as? Service)?.id

                // Можно делать дополнительные действия с сотрудником и услугой
                if (employeeId != null && serviceId != null) {

                    fetchAvailableTime(serviceId, employeeId, selectedDate) // Перезапросить доступное время при выборе сотрудника
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
                val employeeId = (parent.getItemAtPosition(position) as? Employee)?.id
                val serviceId = (serviceSpinner.selectedItem as? Service)?.id

                if (employeeId != null && serviceId != null) {

                    fetchAvailableTime(serviceId, employeeId, selectedDate) // Обновляем доступное время при изменении сотрудника
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Обработчик потери фокуса
        serviceSpinner.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val serviceId = (serviceSpinner.selectedItem as? Service)?.id
                if (serviceId != null) {
                    fetchEmployeesForService(serviceId)
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
                val serviceId = (parent.getItemAtPosition(position) as? Service)?.id
                if (serviceId != null) {
                    fetchEmployeesForService(serviceId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun fetchEmployeesForService(serviceId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {

            val response = apiRepository.getEmployeeForService(serviceId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val employees = response.body() as? List<Employee> ?: emptyList()
                    updateEmployeeSpinner(employees)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка загрузки сотрудников",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }


    private fun updateEmployeeSpinner(employees: List<Employee>) {
        val adapter = EmployeeSpinnerAdapter(requireContext(), employees)
        employeeSpinner.adapter = adapter
    }


    private fun fetchAvailableTime(serviceId: Int?, employeeId: Int?, selectedDate: String?) {
        if (selectedDate == null) return

        if (employeeId != null && serviceId != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val response = apiRepository.getAvailableTime(
                    employeeId = employeeId,
                    serviceId = serviceId,
                    date = selectedDate
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val availableTime = response.body() as? List<String> ?: emptyList()
                        updateAvailableTimeSpinner(availableTime)
                    } else {
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

    private fun updateAvailableTimeSpinner(availableTime: List<String>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, availableTime)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner.adapter = adapter
    }


    private fun handleBooking(): Map<String, Any>? {
        val employee = employeeSpinner.selectedItem as? Employee
        val service = serviceSpinner.selectedItem as? Service
        val selectedTime = timeSpinner.selectedItem as? String

        if (employee == null || service == null || selectedTime == null) {
            return null
        }

        return mapOf(
            "employeeId" to employee.id,
            "serviceId" to service.id,
            "date" to selectedDate,
            "time" to selectedTime
        )
    }

    private fun bookVisit(bookingData: Map<String, Any>) {
        lifecycleScope.launch(Dispatchers.IO) {
            val employee = bookingData["employeeId"] as Int
            val service = bookingData["serviceId"] as Int
            val date = bookingData["date"] as String
            val time = bookingData["time"] as String


            val visit_ip = VisitIP(employee, service, date, time)

            val response = apiRepository.bookVisit(visit_ip).execute()


            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Визит успешно забронирован",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Переход на экран истории
                    val fragment = VisitShowFragment() // фрагмент входа
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null) // Позволяет вернуться назад
                        .commit()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка бронирования визита",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun fetchServices() {
        lifecycleScope.launch(Dispatchers.IO) {
            val response = apiRepository.getServices().execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val services = response.body() ?: emptyList()
                    updateServicesSpinner(services)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка загрузки услуг",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateServicesSpinner(services: List<Service>) {

        // Создаем адаптер для спиннера
        val adapter = ServiceSpinnerAdapter(requireContext(), services)
        serviceSpinner.adapter = adapter
    }
}
