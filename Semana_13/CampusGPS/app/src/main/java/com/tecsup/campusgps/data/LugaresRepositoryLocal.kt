package com.tecsup.campusgps.data

import com.tecsup.campusgps.model.Lugar
import com.tecsup.campusgps.model.TECSUP_LIMA_LATITUD
import com.tecsup.campusgps.model.TECSUP_LIMA_LONGITUD

class LugaresRepositoryLocal : LugaresRepository {
    override fun obtenerLugares() = listOf(
        // Coordenadas copiadas de los enlaces compartidos de Google Maps.
        Lugar(1, "Biblioteca", "Estudio", -12.045007, -76.952958),
        Lugar(2, "Cafetería", "Comida", -12.044004, -76.952229),
        Lugar(3, "Comedor", "Comida", -12.045004, -76.952453),
        Lugar(4, "Auditorio", "Eventos", -12.044828, -76.952715),
        Lugar(5, "Estacionamiento", "Servicios", -12.044541, -76.951658),
        Lugar(6, "Salones de cómputo", "Estudio", -12.044376, -76.952827),
        Lugar(7, "Cancha de grass", "Deporte", -12.043732, -76.953629)
    )
}
