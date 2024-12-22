package com.example.barbershop

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.barbershop.network.Employee

// Адаптер для отображения списка сотрудников в RecyclerView
class EmployeeAdapter : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    // Список сотрудников, который будет отображаться в RecyclerView
    private val employees = mutableListOf<Employee>()

    // Обновление списка сотрудников в адаптере
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newEmployees: List<Employee>) {
        employees.clear() // Очищаем текущий список
        employees.addAll(newEmployees) // Добавляем новые данные в список
        notifyDataSetChanged() // Уведомляем адаптер о том, что данные изменились
    }

    // Создание нового ViewHolder для отображения элемента списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        // Inflating (создание) представления для элемента списка из layout файла
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_employee, parent, false)
        return EmployeeViewHolder(view) // Возвращаем новый ViewHolder
    }

    // Привязка данных сотрудника к соответствующему ViewHolder
    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(employees[position]) // Привязываем данные сотрудника на указанной позиции
    }

    // Получение количества сотрудников в списке
    override fun getItemCount(): Int = employees.size // Возвращаем размер списка сотрудников

    // ViewHolder для каждого элемента списка
    class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Определяем ссылки на TextView элементы для отображения данных сотрудника
        private val tvName: TextView = itemView.findViewById(R.id.tvEmployeeName)
        private val tvPosition: TextView = itemView.findViewById(R.id.tvEmployeePosition)
        private val tvPhoneNumber: TextView = itemView.findViewById(R.id.tvEmployeePhoneNumber)
        private val tvHalls: TextView = itemView.findViewById(R.id.tvEmployeeHalls)
        private val tvServices: TextView = itemView.findViewById(R.id.tvEmployeeServices)

        // Метод для привязки данных сотрудника к элементам представления
        @SuppressLint("SetTextI18n")
        fun bind(employee: Employee) {
            // Заполнение текстовых полей данными из объекта employee
            tvName.text = "${employee.user.first_name} ${employee.user.last_name}" // Имя сотрудника
            tvPosition.text = employee.position // Должность сотрудника
            tvPhoneNumber.text = "Телефон: ${employee.phone_number}" // Номер телефона сотрудника
            tvHalls.text =
                "Залы: ${employee.halls.joinToString(", ") { it.name }}" // Залы, в которых работает сотрудник
            tvServices.text =
                "Услуги: ${employee.services.joinToString(", ") { it.name }}" // Услуги, которые предоставляет сотрудник
        }
    }
}
