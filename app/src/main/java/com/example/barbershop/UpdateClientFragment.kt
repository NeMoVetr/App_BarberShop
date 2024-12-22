package com.example.barbershop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.barbershop.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import android.app.DatePickerDialog

class UpdateClientFragment : Fragment(R.layout.fragment_update_client) {

    // Инициализация переменных для UI элементов
    private lateinit var apiRepository: ApiRepository

    private lateinit var etFirstName: EditText  // Поле для имени
    private lateinit var etLastName: EditText   // Поле для фамилии
    private lateinit var etEmail: EditText      // Поле для email
    private lateinit var etPhoneNumber: EditText // Поле для номера телефона
    private lateinit var etDateOfBirth: EditText // Поле для даты рождения
    private lateinit var spGender: Spinner      // Spinner для выбора пола
    private lateinit var btnSave: Button        // Кнопка для сохранения изменений

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Возвращаем вью фрагмента, инфлейтируем layout
        val view = inflater.inflate(R.layout.fragment_update_client, container, false)

        // Инициализация UI элементов с помощью findViewById
        etFirstName = view.findViewById(R.id.etFirstNameUpdateClient)
        etLastName = view.findViewById(R.id.etLastNameUpdateClient)
        etEmail = view.findViewById(R.id.etEmailUpdateClient)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumberUpdateClient)
        etDateOfBirth = view.findViewById(R.id.etDateOfBirthUpdateClient)
        spGender = view.findViewById(R.id.spGenderUpdateClient)
        btnSave = view.findViewById(R.id.btnSaveUpdateClient)

        // Инициализация репозитория для работы с API
        apiRepository = ApiRepository(SessionManager(requireContext()))

        // Загружаем данные клиента
        loadClientData()

        // Обработчик для поля даты рождения
        etDateOfBirth.setOnClickListener {
            showDatePickerDialog()
        }

        // Обработчик для кнопки "Сохранить"
        btnSave.setOnClickListener {
            saveUpdatedData()  // Сохраняем обновленные данные клиента
        }

        return view  // Возвращаем view, которое отображается в фрагменте
    }

    // Функция для загрузки данных клиента с API
    private fun loadClientData() {
        // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
        lifecycleScope.launch(Dispatchers.IO) {
            // Получаем данные клиента через API
            val client = apiRepository.getClient().execute()
            if (client.isSuccessful) {
                // Если запрос успешен, передаем полученные данные в функцию для заполнения полей
                val client = client.body()
                withContext(Dispatchers.Main) {
                    populateClientFields(client)  // Заполняем поля с данными клиента
                }
            } else {
                // Если запрос неуспешен, показываем сообщение об ошибке
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка загрузки данных",  // Сообщение об ошибке
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Функция для заполнения полей данными клиента
    private fun populateClientFields(client: ClientResponse?) {
        // Заполняем поля на основе данных клиента
        etFirstName.setText(client?.user?.first_name)  // Имя
        etLastName.setText(client?.user?.last_name)    // Фамилия
        etEmail.setText(client?.user?.email)           // Email
        etPhoneNumber.setText(client?.phone_number)   // Номер телефона
        etDateOfBirth.setText(client?.date_of_birth)   // Дата рождения
        // Устанавливаем выбранный пол
        spGender.setSelection(client?.gender?.ordinal ?: 0)
    }

    // Функция для сохранения обновленных данных клиента
    private fun saveUpdatedData() {
        // Считываем значения из полей
        val first_name = etFirstName.text.toString().trim()
        val last_name = etLastName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone_number = etPhoneNumber.text.toString().trim()
        val date_of_birth = etDateOfBirth.text.toString().trim()
        val genderString = spGender.selectedItem.toString().trim()

        // Преобразуем строку с полом в объект Gender
        val gender = when (genderString) {
            "Мужской" -> Gender.Male
            "Женский" -> Gender.Female
            else -> Gender.Male  // По умолчанию мужской
        }

        // Создаем объект для обновления данных клиента
        val update_client = ClientUpdate(
            user = UserUpdate(
                first_name = first_name,
                last_name = last_name,
                email = email
            ),
            phone_number = phone_number,
            date_of_birth = date_of_birth,
            gender = gender
        )

        // Отправляем обновленные данные на сервер
        lifecycleScope.launch(Dispatchers.IO) { // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса

            // Выполняем запрос на обновление данных клиента
            val response = apiRepository.updateClient(update_client).execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    // Если запрос успешен, показываем сообщение об успешном обновлении
                    Toast.makeText(
                        requireContext(),
                        "Данные успешно обновлены",  // Сообщение об успешном обновлении
                        Toast.LENGTH_SHORT
                    ).show()

                    // Возвращаемся к предыдущему фрагменту
                    parentFragmentManager.popBackStack()
                } else {
                    // Если запрос неуспешен, показываем сообщение об ошибке
                    Toast.makeText(
                        requireContext(),
                        "Ошибка обновления данных",  // Сообщение об ошибке
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Функция для отображения DatePickerDialog
    private fun showDatePickerDialog() {
        // Получаем текущую дату
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Создаем и показываем диалог выбора даты
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                // После выбора даты, устанавливаем ее в поле даты рождения
                etDateOfBirth.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}

