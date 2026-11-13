package com.castillo.lab04carritotecsup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Fuente única de datos para catálogo, favoritos y pedidos. AppNavegacion conserva una sola
 * instancia y la entrega a las pantallas y al drawer; por eso el badge observa los mismos datos
 * que cambia el DropdownMenu. Los datos son de demostración y viven solo durante esta sesión.
 */
class StoreState {
    val productos = mutableStateListOf(
        Producto(1, "Audífonos inalámbricos", 89.90, 8, "Tecnología", "Sonido nítido para estudiar y disfrutar música."),
        Producto(2, "Cuaderno premium", 18.50, 16, "Estudio", "Páginas resistentes para apuntes y proyectos."),
        Producto(3, "Botella térmica", 45.00, 10, "Accesorios", "Mantiene tus bebidas a la temperatura ideal."),
        Producto(4, "Mouse ergonómico", 65.90, 6, "Tecnología", "Más comodidad para tus jornadas frente al equipo."),
        Producto(5, "Mochila urbana", 119.00, 4, "Accesorios", "Espacio para laptop, libros y objetos cotidianos.")
    )
    val pedidos = mutableStateListOf<Pedido>()
    var favoritos by mutableStateOf<Set<Int>>(emptySet())
        private set
    var reportados by mutableStateOf<Set<Int>>(emptySet())
        private set
    private var siguienteProductoId by mutableIntStateOf(6)
    private var siguientePedidoId by mutableIntStateOf(1)

    val totalFavoritos: Int
        get() = StoreRules.contarFavoritos(productos, favoritos)

    fun esFavorito(productoId: Int): Boolean = productoId in favoritos

    fun alternarFavorito(productoId: Int) {
        favoritos = StoreRules.alternarFavorito(favoritos, productoId)
    }

    fun agregarProducto(nombre: String, precio: String, cantidad: String, categoria: String): String? {
        val error = StoreRules.validarProducto(nombre, precio, cantidad)
        if (error != null) return error
        productos.add(
            Producto(
                id = siguienteProductoId++,
                nombre = nombre.trim(),
                precio = precio.replace(',', '.').toDouble(),
                cantidad = cantidad.toInt(),
                categoria = categoria,
                descripcion = "Producto añadido al catálogo durante esta sesión."
            )
        )
        return null
    }

    fun crearPedido(productoId: Int): Boolean {
        val indice = productos.indexOfFirst { it.id == productoId }
        if (indice < 0 || productos[indice].cantidad <= 0) return false
        val producto = productos[indice]
        pedidos.add(0, Pedido(siguientePedidoId++, producto.nombre, producto.precio))
        productos[indice] = producto.copy(cantidad = producto.cantidad - 1)
        return true
    }

    fun reportar(productoId: Int) {
        reportados = reportados + productoId
    }
}
