package com.castillo.lab04carritotecsup

/** Reglas puras para poder probar el comportamiento sin iniciar Android. */
object StoreRules {
    fun validarProducto(nombre: String, precio: String, cantidad: String): String? {
        if (nombre.trim().length < 3) return "Escribe un nombre de al menos 3 caracteres."
        if ((precio.replace(',', '.').toDoubleOrNull() ?: 0.0) <= 0.0) {
            return "El precio debe ser mayor que cero."
        }
        if ((cantidad.toIntOrNull() ?: 0) <= 0) return "La cantidad debe ser mayor que cero."
        return null
    }

    fun alternarFavorito(actuales: Set<Int>, productoId: Int): Set<Int> =
        if (productoId in actuales) actuales - productoId else actuales + productoId

    fun contarFavoritos(productos: List<Producto>, favoritos: Set<Int>): Int =
        productos.count { it.id in favoritos }
}
