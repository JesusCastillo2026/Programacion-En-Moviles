package com.saludplus.citas.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

private val localeEspanol = Locale.forLanguageTag("es-PE")
private val meses = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "setiembre", "octubre", "noviembre", "diciembre")

/** Devuelve cinco días de atención desde la semana indicada, sin fines de semana ni fechas pasadas. */
fun proximosDiasHabiles(hoy: LocalDate, desplazamientoSemanas: Int): List<LocalDate> {
    require(desplazamientoSemanas >= 0)
    val inicio = hoy.plusWeeks(desplazamientoSemanas.toLong())
    return generateSequence(inicio) { it.plusDays(1) }
        .filter { it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY }
        .take(5)
        .toList()
}

/** Construye el título con los meses y años reales que aparecen en el período. */
fun tituloPeriodo(dias: List<LocalDate>): String {
    if (dias.isEmpty()) return ""
    val inicio = dias.first()
    val fin = dias.last()
    val mesInicio = meses[inicio.monthValue - 1].replaceFirstChar { it.uppercase(localeEspanol) }
    val mesFin = meses[fin.monthValue - 1].replaceFirstChar { it.uppercase(localeEspanol) }
    return if (inicio.month == fin.month && inicio.year == fin.year) {
        "$mesInicio ${inicio.year}"
    } else {
        "$mesInicio ${inicio.year} · $mesFin ${fin.year}"
    }
}

/** Presenta la fecha de confirmación en español peruano. */
fun fechaEnEspanol(fecha: LocalDate): String {
    val dia = fecha.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, localeEspanol)
        .replaceFirstChar { it.uppercase(localeEspanol) }
    return "$dia ${fecha.dayOfMonth} de ${meses[fecha.monthValue - 1]} ${fecha.year}"
}
