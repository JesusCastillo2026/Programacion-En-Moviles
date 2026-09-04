package com.castillo.myapplication

import java.util.Locale

fun main() {
    var cantidad = 0

    while (true) {
        println("Ingrese la cantidad de vehiculos a registrar (maximo 30):")
        cantidad = readln().toInt()
        if (cantidad in 1..30) {
            break
        }
    }

    var recaudacionTotal = 0.0

    for (i in 1..cantidad) {
        println("Ingrese la placa del vehiculo:")
        val placa = readln()

        println("Ingrese el tipo de vehiculo (Moto, Auto, Camioneta):")
        val tipo = readln()

        println("Ingrese el nombre del cliente:")
        val nombre = readln()

        var horas = 0
        while (true) {
            println("Ingrese las horas de estadia (minimo 1):")
            horas = readln().toInt()
            if (horas >= 1) {
                break
            }
        }

        println("¿Es cliente frecuente? (true/false):")
        val frecuente = readln().toBoolean()

        var tarifa = 0.0
        if (tipo == "Moto") {
            tarifa = 2.00
        } else if (tipo == "Auto") {
            tarifa = 4.00
        } else if (tipo == "Camioneta") {
            tarifa = 10.00
        }

        var total = 0.0
        for (h in 1..horas) {
            if (h <= 2) {
                total += tarifa
            } else if (h <= 4) {
                total += tarifa * 1.20
            } else {
                total += tarifa * 1.50
            }
        }

        if (frecuente) {
            total -= (total * 0.10)
        }

        recaudacionTotal += total

        val totalFormateado = String.format(Locale.US, "%.2f", total)
        println("Cliente: $nombre")
        println("Placa: $placa")
        println("Total a pagar: $totalFormateado")
        println("-------------------------")
    }

    val recaudacionFormateada = String.format(Locale.US, "%.2f", recaudacionTotal)
    println("Recaudacion total del dia: $recaudacionFormateada")
}