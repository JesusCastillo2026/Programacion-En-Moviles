package com.tecsup.campusgps

import com.tecsup.campusgps.model.Lugar
import com.tecsup.campusgps.model.LugarConDistancia
import com.tecsup.campusgps.util.dentroDelRadio
import com.tecsup.campusgps.util.filtrarPorCategoria
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasCampusTest {
    private val lugares = listOf(
        LugarConDistancia(Lugar(1, "Biblioteca", "Estudio", 0.0, 0.0), 80f),
        LugarConDistancia(Lugar(2, "Cafetería", "Comida", 0.0, 0.0), 150f),
        LugarConDistancia(Lugar(3, "Laboratorio", "Estudio", 0.0, 0.0), null)
    )

    @Test fun sinCategoriaDevuelveTodosLosLugares() {
        assertEquals(3, filtrarPorCategoria(lugares, null).size)
    }

    @Test fun categoriaDevuelveSoloCoincidencias() {
        assertEquals(listOf(1, 3), filtrarPorCategoria(lugares, "Estudio").map { it.lugar.id })
    }

    @Test fun lugarDentroDelRadioSeMarcaCorrectamente() {
        assertTrue(dentroDelRadio(80f, 100))
    }

    @Test fun lugarFueraDelRadioSeMarcaCorrectamente() {
        assertFalse(dentroDelRadio(150f, 100))
    }

    @Test fun distanciaNoCalculadaNoSeMarcaDentro() {
        assertFalse(dentroDelRadio(null, 500))
    }
}
