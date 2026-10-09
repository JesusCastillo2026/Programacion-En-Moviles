package com.saludplus.paciente.data.model

/** Causas diferenciadas que permiten ofrecer una acción de recuperación adecuada. */
enum class ErrorReserva(val mensaje: String) {
    SIN_SESION("Necesitas iniciar sesión para confirmar tu cita."),
    SEDE_INVALIDA("Elige una sede antes de reservar."),
    ESPECIALIDAD_INVALIDA("La especialidad seleccionada ya no está disponible."),
    MEDICO_INVALIDO("No encontramos al médico seleccionado."),
    ESPECIALIDAD_NO_COINCIDE("El médico no pertenece a la especialidad seleccionada."),
    MEDICO_NO_DISPONIBLE_EN_SEDE("Este médico no atiende en la sede elegida. Selecciona otro profesional o sede."),
    FECHA_INVALIDA("Selecciona una fecha válida."),
    FECHA_PASADA("La fecha elegida ya pasó. Selecciona un nuevo día."),
    FIN_DE_SEMANA("Las citas se ofrecen de lunes a viernes."),
    MEDICO_NO_ATIENDE_ESE_DIA("Este profesional no atiende el día seleccionado. Elige uno de los días disponibles."),
    HORA_INVALIDA("Selecciona uno de los horarios disponibles."),
    HORA_PASADA("Ese horario ya pasó. Elige un turno posterior a la hora actual."),
    HORARIO_OCUPADO("Este horario acaba de ocuparse. Elige otro turno."),
    NO_CONFIRMADA("No pudimos confirmar la cita. Revisa el horario e inténtalo de nuevo.")
}
