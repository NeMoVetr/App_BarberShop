package com.example.barbershop


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.barbershop.network.VisitListResponse


// Определение адаптера для RecyclerView, который будет работать с объектами VisitListResponse
class VisitAdapter(
    private val visits: List<VisitListResponse>,  // Список визитов, передаваемый в адаптер
    private val onEditClick: (VisitListResponse) -> Unit,  // Лямбда-функция для обработки клика на кнопку редактирования
    private val onDeleteClick: (VisitListResponse) -> Unit  // Лямбда-функция для обработки клика на кнопку удаления
) : RecyclerView.Adapter<VisitAdapter.VisitViewHolder>() {  // Унаследован от RecyclerView.Adapter с указанием ViewHolder

    // Внутренний класс ViewHolder для отображения данных в RecyclerView
    inner class VisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Инициализация UI-элементов для каждого элемента в списке
        val employeeName: TextView = itemView.findViewById(R.id.employee_name)  // Имя сотрудника
        val serviceName: TextView = itemView.findViewById(R.id.service_name)  // Название услуги
        val visitDateTime: TextView =
            itemView.findViewById(R.id.visit_date_time)  // Дата и время визита
        val visitStatus: TextView = itemView.findViewById(R.id.visit_status)  // Статус визита

        val buttonEdit: Button = itemView.findViewById(R.id.button_edit)  // Кнопка редактирования
        val buttonDelete: Button = itemView.findViewById(R.id.button_delete)  // Кнопка удаления
    }

    // Метод для создания нового ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        // Создание нового элемента списка, привязка его к макету item_visit
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_visit, parent, false)
        return VisitViewHolder(view)  // Возвращаем созданный ViewHolder
    }

    // Метод для привязки данных к элементам ViewHolder
    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        val visit = visits[position]  // Получаем текущий объект визита из списка

        // Привязываем данные визита к текстовым полям в ViewHolder
        holder.employeeName.text = visit.employee_name  // Устанавливаем имя сотрудника
        holder.serviceName.text = visit.service_name  // Устанавливаем название услуги
        holder.visitDateTime.text = visit.date_time  // Устанавливаем дату и время визита
        holder.visitStatus.text = visit.status  // Устанавливаем статус визита

        // Обработчик для кнопки редактирования: вызывает onEditClick с текущим визитом
        holder.buttonEdit.setOnClickListener { onEditClick(visit) }

        // Обработчик для кнопки удаления: вызывает onDeleteClick с текущим визитом
        holder.buttonDelete.setOnClickListener { onDeleteClick(visit) }
    }

    // Метод для получения количества элементов в списке
    override fun getItemCount(): Int = visits.size  // Возвращает количество визитов в списке
}
