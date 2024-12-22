package com.example.barbershop


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.widget.*
import java.util.*
import com.example.barbershop.network.ApiRepository


import android.app.DatePickerDialog
import androidx.lifecycle.lifecycleScope
import com.example.barbershop.network.*

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment(R.layout.fragment_register) {

    // Объявление переменных для API репозитория и UI элементов
    private lateinit var apiRepository: ApiRepository
    private lateinit var etUsername: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etDateOfBirth: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPassword2: EditText
    private lateinit var selectedDateOfBirth: String
    private lateinit var spGender: Spinner
    private lateinit var btnRegisterSubmit: Button

    // Переопределение метода onCreateView для инициализации UI элементов
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflating layout для фрагмента
        val rootView = inflater.inflate(R.layout.fragment_register, container, false)

        // Инициализация репозитория API
        apiRepository = ApiRepository(SessionManager(requireContext()))

        // Инициализация UI элементов с помощью findViewById
        etUsername = rootView.findViewById(R.id.etUsernameRegister)
        etFirstName = rootView.findViewById(R.id.etFirstNameRegister)
        etLastName = rootView.findViewById(R.id.etLastNameRegister)
        etEmail = rootView.findViewById(R.id.etEmailRegister)
        etPhoneNumber = rootView.findViewById(R.id.etPhoneNumberRegister)
        etDateOfBirth = rootView.findViewById(R.id.etDateOfBirthRegister)
        spGender = rootView.findViewById(R.id.spGenderRegister)
        etPassword = rootView.findViewById(R.id.etPasswordRegister)
        etPassword2 = rootView.findViewById(R.id.etPassword2Register)
        btnRegisterSubmit = rootView.findViewById(R.id.btnRegisterSubmit)

        // Открытие DatePicker при клике на поле "Дата рождения"
        etDateOfBirth.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Создание и отображение DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Сохранение выбранной даты и отображение в поле ввода
                    selectedDateOfBirth = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    etDateOfBirth.setText(selectedDateOfBirth)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        // Обработка нажатия на кнопку "Зарегистрироваться"
        btnRegisterSubmit.setOnClickListener {
            // Сбор данных клиента из полей ввода
            val client = collectClientData()
            if (client != null) {
                // Регистрация клиента, если данные валидны
                registerClient(client)
            } else {
                // Отображение сообщения об ошибке, если данные заполнены неверно
                Toast.makeText(requireContext(), "Заполните все поля корректно", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return rootView
    }

    // Функция для сбора данных клиента из UI элементов
    private fun collectClientData(): Client? {
        // Получение текста из полей ввода
        val username = etUsername.text.toString()
        val firstName = etFirstName.text.toString()
        val lastName = etLastName.text.toString()
        val password = etPassword.text.toString()
        val password2 = etPassword2.text.toString()
        val email = etEmail.text.toString()
        val phoneNumber = etPhoneNumber.text.toString()
        val genderString = spGender.selectedItem.toString()

        // Проверка на пустые значения
        if (username.isBlank() || firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank() || password2.isBlank() || phoneNumber.isBlank() || selectedDateOfBirth.isNullOrBlank()) {
            return null
        }

        // Проверка на совпадение паролей
        if (password != password2) {
            // Если пароли не совпадают, выводится ошибка
            Toast.makeText(requireContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            return null
        }

        // Преобразование строки пола в enum
        val gender = when (genderString) {
            "Мужской" -> Gender.Male
            "Женский" -> Gender.Female
            else -> Gender.Male // По умолчанию или если произошла ошибка
        }

        // Возвращение объекта клиента с собранными данными
        return Client(
            user = User(username, firstName, lastName, email, password, password2),
            phone_number = phoneNumber,
            date_of_birth = selectedDateOfBirth!!,
            gender = gender
        )
    }

    // Функция для регистрации клиента
    private fun registerClient(client: Client) {
        // Запускаем ассинхронную операцию жизненного цикла в IO-потоке (потока ввода-вывода) для выполнения сетевого запроса
        lifecycleScope.launch(Dispatchers.IO) {
            // Выполнение запроса на регистрацию
            val response = apiRepository.registerClient(client).execute()
            withContext(Dispatchers.Main) {
                // Проверка успешности ответа
                if (response.isSuccessful && response.body() != null) {
                    // Успешная регистрация
                    Toast.makeText(requireContext(), "Регистрация успешна", Toast.LENGTH_SHORT)
                        .show()
                    // Переход на экран входа
                    val fragment = LoginFragment() // фрагмент входа
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                } else {
                    // Ошибка регистрации
                    val errorMessage = response.errorBody()?.string() ?: "Ошибка регистрации"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

