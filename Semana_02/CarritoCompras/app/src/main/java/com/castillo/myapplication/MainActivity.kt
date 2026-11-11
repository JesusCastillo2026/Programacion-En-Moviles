package com.castillo.lab02carritokotlin

fun main() {
    val cliente = "Jesus Castillo"
    val productos = listOf("Laptop", "Mouse", "Licencia", "Curso")
    val precios = listOf(3200.00, 85.00, 150.00, 60.00)

    println("Cliente: $cliente")
    println("--- Lista de Productos ---")
    for (i in productos.indices) {
        println("${productos[i]} - S/ ${precios[i]}")
    }
}