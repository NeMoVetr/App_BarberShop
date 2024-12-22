package com.example.barbershop


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.barbershop.network.Service

class ServiceSpinnerAdapter(
    context: Context,
    private val services: List<Service> // Список услуг, который мы будем отображать
) : ArrayAdapter<Service>(context, android.R.layout.simple_spinner_item, services) {

    // Инициализация адаптера, установка шаблона для отображения элемента списка
    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Устанавливаем ресурс для выпадающего списка
    }

    // Переопределяем метод getView, чтобы отображать только название услуги
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val serviceName =
            view.findViewById<TextView>(android.R.id.text1) // Находим TextView для имени услуги
        serviceName.text =
            services[position].name  // Устанавливаем текст в TextView с именем услуги
        return view // Возвращаем настроенный вид
    }

    // Переопределяем метод getDropDownView для отображения списка услуг в выпадающем списке
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val serviceName =
            view.findViewById<TextView>(android.R.id.text1) // Находим TextView для имени услуги
        serviceName.text =
            services[position].name  // Устанавливаем текст в TextView с именем услуги
        return view // Возвращаем настроенный вид для выпадающего списка
    }
}
