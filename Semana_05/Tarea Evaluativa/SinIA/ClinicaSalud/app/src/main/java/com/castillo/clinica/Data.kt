package com.castillo.clinica

data class Item(val id: Int, val name: String, val category: String, val subtitle: String, val description: String, val rating: String = "", val capacity: Int = 12)
data class Booking(val id: Int, val itemId: Int, val name: String, val date: String, val time: String, val status: String = "Confirmada")
val clinic = true
val appTitle = "Clínica Salud+"
val person = "Juan Pérez"
val catalog = listOf(
    Item(1, "Dra. Ana Torres", "Cardiología", "Cardióloga · 12 años exp.", "Especialista en arritmias e hipertensión, formación en la Clínica Mayo.", "4.9"),
    Item(2, "Dr. Luis Vega", "Pediatría", "Pediatra · 10 años exp.", "Atención integral para niños y adolescentes.", "4.7"),
    Item(3, "Dra. Rosa Díaz", "Dermatología", "Dermatóloga · 8 años exp.", "Especialista en cuidado de la piel y prevención.", "4.8")
)
val dates = listOf("Jue 26", "Vie 27", "Sáb 28")
fun timesFor(item: Item): List<String> = listOf("9:00 am", "10:30 am", "3:00 pm")
fun initialBookings() = listOf(
    Booking(1, 1, "Dra. Ana Torres", "Vie 27", "10:30 am"),
    Booking(2, 2, "Dr. Luis Vega", "Mié 15", "3:00 pm", "Completada")
)
fun isDuplicate(bookings: List<Booking>, item: Item, date: String, time: String) =
    bookings.any { it.itemId == item.id && it.date == date && it.time == time && it.status == "Confirmada" }
fun availableSeats(bookings: List<Booking>, item: Item, date: String, time: String) =
    (item.capacity - 4 - bookings.count { it.itemId == item.id && it.date == date && it.time == time && it.status == "Confirmada" }).coerceAtLeast(0)

