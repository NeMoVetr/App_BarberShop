package com.example.barbershop

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.barbershop.network.Service


class ServiceAdapter : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    // Список услуг, который будет отображаться в RecyclerView
    private val services = mutableListOf<Service>()

    // Метод для обновления данных в адаптере
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newServices: List<Service>) {
        // Очистка текущего списка и добавление новых услуг
        services.clear()
        services.addAll(newServices)
        // Уведомление о том, что данные изменились
        notifyDataSetChanged()
    }

    // Создание нового ViewHolder для отображения данных
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        // Инфлейтинг макета для отдельного элемента списка
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        // Возвращаем новый ViewHolder
        return ServiceViewHolder(view)
    }

    // Привязка данных из списка к элементам UI
    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        // Привязка данных услуги к соответствующему ViewHolder
        holder.bind(services[position])
    }

    // Возвращает количество элементов в списке
    override fun getItemCount(): Int = services.size

    // ViewHolder для отдельного элемента списка
    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Инициализация UI элементов, которые будут отображать данные
        private val tvName: TextView = itemView.findViewById(R.id.tvServiceName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvServiceDescription)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvServicePrice)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvServiceDuration)

        // Метод для привязки данных услуги к UI элементам
        @SuppressLint("SetTextI18n")
        fun bind(service: Service) {
            // Установка данных для каждого TextView
            tvName.text = service.name
            tvDescription.text = service.description
            tvPrice.text = "Цена: ${service.price} руб."
            tvDuration.text = "Длительность (час): ${service.duration}"
        }
    }
}
