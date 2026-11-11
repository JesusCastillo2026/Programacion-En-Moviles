package com.castillo.lab04carritotecsup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreRulesTest {
    @Test
    fun favoritoSeAgregaUnaSolaVezYSePuedeQuitar() {
        val first = StoreRules.alternarFavorito(emptySet(), 7)
        assertEquals(setOf(7), first)
        val second = StoreRules.alternarFavorito(first, 7)
        assertTrue(second.isEmpty())
    }

    @Test
    fun contadorSoloIncluyeProductosExistentes() {
        val products = listOf(Producto(1, "Libro", 20.0, 2, "Estudio", "Lectura"))
        assertEquals(1, StoreRules.contarFavoritos(products, setOf(1, 999)))
    }

    @Test
    fun formularioRechazaDatosInvalidos() {
        assertFalse(StoreRules.validarProducto("AB", "10", "1") == null)
        assertFalse(StoreRules.validarProducto("Libro", "0", "1") == null)
        assertFalse(StoreRules.validarProducto("Libro", "10", "0") == null)
        assertNull(StoreRules.validarProducto("Libro", "10,50", "2"))
    }
}
