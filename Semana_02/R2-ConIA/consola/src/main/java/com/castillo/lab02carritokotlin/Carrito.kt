package com.castillo.lab02carritokotlin

import java.util.Locale

// 1. ABSTRACCIÓN: Clase base con contrato general
abstract class Producto(
    private val codigo: String,
    private val nombre: String,
    private var precioBase: Double,
    private var stock: Int
) {
    // 2. ENCAPSULAMIENTO: Acceso seguro a atributos protegidos
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

    // 3. POLIMORFISMO: Métodos abstractos con comportamiento según la subclase
    abstract fun calcularTotalItem(cantidad: Int): Double
    abstract fun obtenerDetalleTipo(): String
}

// 4. HERENCIA: ProductoFisico extiende Producto
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

// 4. HERENCIA: ProductoDigital extiende Producto
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

class CarritoDeCompras(private val cliente: String) {
    private val items = mutableListOf<ItemCarrito>()

    fun agregarProducto(producto: Producto, cantidad: Int) {
        if (producto.reducirStock(cantidad)) {
            items.add(ItemCarrito(producto, cantidad))
        } else {
            println("Stock insuficiente para: ${producto.getNombre()} (Disponibles: ${producto.getStock()})")
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

    val laptop = ProductoFisico("TEC-01", "Laptop Gamer Victus", 3200.00, 5, 35.00)
    val mouse = ProductoFisico("TEC-02", "Mouse Inalambrico", 85.00, 12, 10.00)
    val winLicense = ProductoDigital("LIC-01", "Licencia Windows 11", 150.00, 50, "https://tecsup.edu.pe/keys/win")
    val kotlinCourse = ProductoDigital("CUR-01", "Curso Kotlin Online", 60.00, 100, "https://tecsup.edu.pe/campus/kt")

    carrito.agregarProducto(laptop, 1)
    carrito.agregarProducto(mouse, 2)
    carrito.agregarProducto(winLicense, 1)
    carrito.agregarProducto(kotlinCourse, 1)

    carrito.generarReporte()
}