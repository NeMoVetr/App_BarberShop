package com.example.barbershop

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.barbershop.network.Employee

// Адаптер для отображения списка сотрудников в Spinner
class EmployeeSpinnerAdapter(
    context: Context,
    private val employees: List<Employee> // Список сотрудников
) : ArrayAdapter<Employee>(context, android.R.layout.simple_spinner_item, employees) {

    // Инициализация адаптера с указанием ресурса для отображения элемента в списке
    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Установка ресурса для отображения выпадающего списка
    }

    // Переопределяем метод getView для отображения только имени сотрудника в Spinner
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Получаем стандартное представление элемента из родительского класса
        val view = super.getView(position, convertView, parent)
        // Находим TextView для отображения текста в элементе Spinner
        val employeeName = view.findViewById<TextView>(android.R.id.text1)

        // Получаем сотрудника по позиции и устанавливаем его имя (first_name + last_name)
        val employee = employees[position]
        employeeName.text = "${employee.user.first_name} ${employee.user.last_name}"

        // Возвращаем измененное представление для отображения
        return view
    }

    // Переопределяем метод getDropDownView для отображения списка сотрудников в выпадающем списке Spinner
    @SuppressLint("SetTextI18n")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Получаем стандартное представление элемента из родительского класса
        val view = super.getDropDownView(position, convertView, parent)
        // Находим TextView для отображения текста в выпадающем списке
        val employeeName = view.findViewById<TextView>(android.R.id.text1)

        // Получаем сотрудника по позиции и устанавливаем его имя (first_name + last_name) в выпадающем списке
        val employee = employees[position]
        employeeName.text = "${employee.user.first_name} ${employee.user.last_name}"

        // Возвращаем измененное представление для отображения в выпадающем списке
        return view
    }
}
