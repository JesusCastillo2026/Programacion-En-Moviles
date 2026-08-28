package com.castillo.lab02carritokotlin

import java.util.Locale

// 1. ABSTRACCIÓN
abstract class Producto(
    private val codigo: String,
    private val nombre: String,
    private var precioBase: Double,
    private var stock: Int
) {
    // 2. ENCAPSULAMIENTO
    fun getCodigo(): String = codigo
    fun getNombre(): String = nombre
    fun getPrecioBase(): Double = precioBase
    fun getStock(): Int = stock

    fun reducirStock(cantidad: Int): Boolean {
        return if (stock >= cantidad) {
            stock -= cantidad
            true
        } else {
            false
        }
    }

    fun reponerStock(cantidad: Int) {
        stock += cantidad
    }

    // 3. POLIMORFISMO
    abstract fun calcularTotalItem(cantidad: Int): Double
    abstract fun obtenerDetalleTipo(): String
}

// 4. HERENCIA: ProductoFisico
class ProductoFisico(
    codigo: String,
    nombre: String,
    precioBase: Double,
    stock: Int,
    private val costoEnvio: Double
) : Producto(codigo, nombre, precioBase, stock) {

    override fun calcularTotalItem(cantidad: Int): Double {
        return (getPrecioBase() * cantidad) + costoEnvio
    }

    override fun obtenerDetalleTipo(): String {
        return "FISICO (Envio: S/ ${String.format(Locale.US, "%.2f", costoEnvio)})"
    }
}

// 4. HERENCIA: ProductoDigital
class ProductoDigital(
    codigo: String,
    nombre: String,
    precioBase: Double,
    stock: Int,
    private val linkDescarga: String
) : Producto(codigo, nombre, precioBase, stock) {

    override fun calcularTotalItem(cantidad: Int): Double {
        return getPrecioBase() * cantidad
    }

    override fun obtenerDetalleTipo(): String {
        return "DIGITAL (Descarga: $linkDescarga)"
    }
}

data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int
)

// Función del reto adicional para buscar sobre una lista de productos
fun buscarProducto(productos: List<Producto>, nombre: String): Producto? {
    return productos.find { it.getNombre().equals(nombre, ignoreCase = true) }
}

class CarritoDeCompras(private val cliente: String) {
    private val items = mutableListOf<ItemCarrito>()

    fun agregarProducto(producto: Producto, cantidad: Int) {
        if (producto.reducirStock(cantidad)) {
            items.add(ItemCarrito(producto, cantidad))
        } else {
            println("Stock insuficiente para: ${producto.getNombre()} (Disponibles: ${producto.getStock()})")
        }
    }

    // Reto adicional: Eliminación con removeIf y reposición de stock
    fun eliminarProductoPorNombre(nombre: String): Boolean {
        val item = items.find { it.producto.getNombre().equals(nombre, ignoreCase = true) }
        return if (item != null) {
            item.producto.reponerStock(item.cantidad)
            items.removeIf { it.producto.getNombre().equals(nombre, ignoreCase = true) }
            true
        } else {
            false
        }
    }

    fun calcularSubtotal(): Double {
        var subtotal = 0.00
        for (item in items) {
            subtotal += item.producto.calcularTotalItem(item.cantidad)
        }
        return subtotal
    }

    fun calcularDescuento(total: Double): Double {
        return when {
            total > 5000.00 -> total * 0.10
            total > 3000.00 -> total * 0.05
            else -> 0.00
        }
    }

    fun generarReporte() {
        println("=========================================")
        println("       CARRITO POO - TIENDA TECSUP       ")
        println("=========================================")
        println("Cliente: $cliente\n")
        println("--------- DETALLE DE PRODUCTOS ----------")

        var i = 1
        for (item in items) {
            val totalLinea = item.producto.calcularTotalItem(item.cantidad)
            println(
                String.format(
                    Locale.US,
                    "%d. [%s] %-20s x%d S/ %8.2f",
                    i,
                    item.producto.getCodigo(),
                    item.producto.getNombre(),
                    item.cantidad,
                    totalLinea
                )
            )
            println("   Detalle: ${item.producto.obtenerDetalleTipo()} | Stock restante: ${item.producto.getStock()}")
            i++
        }
        println("-----------------------------------------")

        val subtotal = calcularSubtotal()
        val igv = subtotal * 0.18
        val totalBruto = subtotal + igv
        val descuento = calcularDescuento(totalBruto)
        val totalPagar = totalBruto - descuento

        println("Cantidad de productos : ${items.size}")
        println(String.format(Locale.US, "Subtotal              : S/ %8.2f", subtotal))
        println(String.format(Locale.US, "IGV (18%%)             : S/ %8.2f", igv))
        println(String.format(Locale.US, "Total Bruto           : S/ %8.2f", totalBruto))
        if (descuento > 0.00) {
            println(String.format(Locale.US, "Descuento Aplicado    : S/ %8.2f", descuento))
        }
        println(String.format(Locale.US, "TOTAL A PAGAR         : S/ %8.2f", totalPagar))
    }
}

fun main() {
    val carrito = CarritoDeCompras("Jesus Castillo")

    val catalogo = listOf(
        ProductoFisico("TEC-01", "Laptop Gamer Victus", 3200.00, 5, 35.00),
        ProductoFisico("TEC-02", "Mouse Inalambrico", 85.00, 12, 10.00),
        ProductoDigital("LIC-01", "Licencia Windows 11", 150.00, 50, "https://tecsup.edu.pe/keys/win"),
        ProductoDigital("CUR-01", "Curso Kotlin Online", 60.00, 100, "https://tecsup.edu.pe/campus/kt")
    )

    for (p in catalogo) {
        carrito.agregarProducto(p, 1)
    }

    println("ESTADO INICIAL DEL CARRITO:")
    carrito.generarReporte()

    println("\n=========================================")
    println("            RETO ADICIONAL               ")
    println("=========================================")

    // 1. Demostración de búsqueda con find
    val nombreABuscar = "Mouse Inalambrico"
    val buscado = buscarProducto(catalogo, nombreABuscar)
    if (buscado != null) {
        println("Producto encontrado: [${buscado.getCodigo()}] ${buscado.getNombre()} (Precio: S/ ${String.format(Locale.US, "%.2f", buscado.getPrecioBase())})")
    } else {
        println("Producto '$nombreABuscar' no encontrado.")
    }

    // 2. Demostración de eliminación con removeIf
    val productoAEliminar = "Mouse Inalambrico"
    println("\nEliminando '$productoAEliminar' del carrito con removeIf...")
    val eliminado = carrito.eliminarProductoPorNombre(productoAEliminar)

    if (eliminado) {
        println("Producto eliminado correctamente y stock restablecido.")
    }

    println("\nESTADO ACTUALIZADO TRAS ELIMINAR:")
    carrito.generarReporte()
}