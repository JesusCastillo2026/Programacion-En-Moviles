package com.castillo.lab02carritokotlin

import java.util.Locale

fun main() {
    val cliente = "Jesus Castillo"
    val productos = listOf("Laptop", "Mouse", "Licencia", "Curso")
    val precios = listOf(3200.00, 85.00, 150.00, 60.00)
    val cantidades = listOf(1, 1, 1, 1)

    println("Cliente: $cliente")
    println("--- Detalle de Compras ---")

    var subtotal = 0.00
    for (i in productos.indices) {
        val totalLinea = precios[i] * cantidades[i]
        subtotal += totalLinea
        println("${productos[i]} x${cantidades[i]} - S/ ${String.format(Locale.US, "%.2f", totalLinea)}")
    }

    val igv = subtotal * 0.18
    val total = subtotal + igv

    println("-------------------")
    println(String.format(Locale.US, "Subtotal: S/ %.2f", subtotal))
    println(String.format(Locale.US, "IGV (18%%): S/ %.2f", igv))
    println(String.format(Locale.US, "Total: S/ %.2f", total))
}