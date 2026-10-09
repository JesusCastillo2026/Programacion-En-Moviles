package com.saludplus.paciente.data.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/** Las citas se publican en la hora local de la clínica, independientemente del emulador. */
object RelojClinica {
    private val zona = ZoneId.of("America/Lima")

    fun ahora(): LocalDateTime = LocalDateTime.now(zona)
    fun hoy(): LocalDate = LocalDate.now(zona)
}
