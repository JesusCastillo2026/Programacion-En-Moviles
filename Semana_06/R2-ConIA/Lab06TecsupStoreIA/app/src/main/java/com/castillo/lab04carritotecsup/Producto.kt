package com.castillo.lab04carritotecsup

/** Producto visible en el catálogo. La cantidad representa unidades disponibles. */
data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val categoria: String,
    val descripcion: String
)

/** Copia del precio y nombre al crear un pedido, para que el historial no cambie después. */
data class Pedido(
    val numero: Int,
    val producto: String,
    val precio: Double
)
