package com.castillo.lab02carritokotlin

import java.util.Locale

fun main() {
    val cliente = "Jesus Castillo"
    val productos = listOf("Laptop", "Mouse", "Licencia", "Curso")
    val precios = listOf(3200.00, 85.00, 150.00, 60.00)
    val cantidades = listOf(1, 1, 1, 1)

    println("=========================================")
    println("  CARRITO ESTRUCTURADO - TIENDA TECSUP   ")
    println("=========================================")
    println("Cliente: $cliente\n")

    println("--------- DETALLE DE PRODUCTOS ----------")
    var subtotal = 0.00
    for (i in productos.indices) {
        val totalLinea = precios[i] * cantidades[i]
        subtotal += totalLinea
        println(
            String.format(
                Locale.US,
                "%d. %-20s x%d S/ %8.2f",
                i + 1,
                productos[i],
                cantidades[i],
                totalLinea
            )
        )
    }
    println("-----------------------------------------")

    val igv = subtotal * 0.18
    val totalBruto = subtotal + igv

    val descuento = when {
        totalBruto > 5000.00 -> totalBruto * 0.10
        totalBruto > 3000.00 -> totalBruto * 0.05
        else -> 0.00
    }

    val totalPagar = totalBruto - descuento

    println(String.format(Locale.US, "Subtotal              : S/ %8.2f", subtotal))
    println(String.format(Locale.US, "IGV (18%%)             : S/ %8.2f", igv))
    println(String.format(Locale.US, "Total Bruto           : S/ %8.2f", totalBruto))
    if (descuento > 0.00) {
        println(String.format(Locale.US, "Descuento Aplicado    : S/ %8.2f", descuento))
    }
    println(String.format(Locale.US, "TOTAL A PAGAR         : S/ %8.2f", totalPagar))
}