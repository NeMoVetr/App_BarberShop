package com.example.barbershop

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.barbershop.network.Hall

// Адаптер для отображения списка залов в RecyclerView
class HallAdapter : RecyclerView.Adapter<HallAdapter.HallViewHolder>() {
    // Список залов, который будет отображаться в RecyclerView
    private val halls = mutableListOf<Hall>()

    // Метод для обновления списка залов в адаптере
    fun submitList(newHalls: List<Hall>) {
        halls.clear()  // Очищаем текущий список
        halls.addAll(newHalls)  // Добавляем новые данные
        notifyDataSetChanged()  // Уведомляем адаптер, что данные обновились
    }

    // Метод для создания нового ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HallViewHolder {
        // Получаем представление для элемента списка
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hall, parent, false)
        return HallViewHolder(view)  // Возвращаем новый HallViewHolder
    }

    // Метод для привязки данных к элементу RecyclerView
    override fun onBindViewHolder(holder: HallViewHolder, position: Int) {
        holder.bind(halls[position])  // Передаем зал в метод bind для отображения данных
    }

    // Метод для получения общего количества элементов в списке
    override fun getItemCount(): Int = halls.size

    // ViewHolder для каждого элемента списка
    class HallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Инициализация TextView для отображения информации о зале
        private val tvName: TextView = itemView.findViewById(R.id.tvHallName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvHallDescription)
        private val tvCapacity: TextView = itemView.findViewById(R.id.tvHallCapacity)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvHallLocation)
        private val tvWorkingHours: TextView = itemView.findViewById(R.id.tvHallWorkingHours)

        // Метод для привязки данных о зале к соответствующим элементам в представлении
        @SuppressLint("SetTextI18n")
        fun bind(hall: Hall) {
            tvName.text = hall.name  // Отображаем имя зала
            tvDescription.text = hall.description  // Отображаем описание зала
            tvCapacity.text = "Вместимость: ${hall.capacity}"  // Отображаем вместимость зала
            tvLocation.text = "Расположение: ${hall.location}"  // Отображаем расположение зала
            tvWorkingHours.text =
                "Часы работы: ${hall.start_time} - ${hall.end_time}"  // Отображаем рабочие часы
        }
    }
}
