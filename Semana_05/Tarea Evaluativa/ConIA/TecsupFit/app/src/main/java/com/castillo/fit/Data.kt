package com.castillo.fit

/** Elemento inmutable del catálogo. Su id es el parámetro que viaja entre destinos. */
data class Item(val id: Int, val name: String, val category: String, val subtitle: String, val description: String, val rating: String = "", val capacity: Int = 12)
/** Copia de los datos elegidos al confirmar; cancelar conserva el registro en el historial. */
data class Booking(val id: Int, val itemId: Int, val name: String, val date: String, val time: String, val status: String = "Confirmada")
val clinic = false
val appTitle = "TECSUP Fit"
val person = "Diego Ramos"
val catalog = listOf(
    Item(1, "Yoga funcional", "Hoy", "7:00 am · Sala 2", "Movilidad, equilibrio y respiración. Una práctica para todos los niveles."),
    Item(2, "Cross Training", "Hoy", "6:00 pm · Sala 1 · 45 min", "Entrenamiento funcional de alta intensidad. Cupos limitados."),
    Item(3, "Spinning", "Esta semana", "7:30 pm · Sala 3", "Entrenamiento cardiovascular en bicicleta con música y energía.")
)
val dates = listOf("Jue 26", "Vie 27", "Sáb 28")
fun timesFor(item: Item): List<String> = if (item.id == 1) listOf("7:00 am", "9:00 am", "5:00 pm") else if (item.id == 2) listOf("6:00 pm", "7:00 pm", "8:00 pm") else listOf("7:30 pm", "8:30 pm", "9:30 pm")
fun initialBookings() = listOf(
    Booking(1, 2, "Cross Training", "Hoy", "6:00 pm"),
    Booking(2, 1, "Yoga funcional", "Ayer", "7:00 am", "Completada")
)
/** Solo una reserva confirmada bloquea volver a elegir el mismo horario. */
fun isDuplicate(bookings: List<Booking>, item: Item, date: String, time: String) =
    bookings.any { it.itemId == item.id && it.date == date && it.time == time && it.status == "Confirmada" }
/** Simula cuatro cupos externos ocupados. Los cancelados liberan su cupo local. */
fun availableSeats(bookings: List<Booking>, item: Item, date: String, time: String) =
    (item.capacity - 4 - bookings.count { it.itemId == item.id && it.date == date && it.time == time && it.status == "Confirmada" }).coerceAtLeast(0)
