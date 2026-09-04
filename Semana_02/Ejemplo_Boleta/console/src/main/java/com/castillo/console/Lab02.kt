package com.castillo.myapplication

import java.util.Locale

fun main() {
    val tablaTarifas = """
        =========================================================================
                          SISTEMA DE CONTROL DE ESTACIONAMIENTO                  
        ----------------+--------------+-----------------------------------------
          TIPO VEHICULO | TARIFA BASE  |         CONDICIONES Y RECARGOS          
        ----------------+--------------+-----------------------------------------
          Moto          |  S/  2.00/h  | * De 1 a 2 horas : Tarifa Normal (0%)   
          Auto          |  S/  4.00/h  | * De 3 a 5 horas : +20% de recargo      
          Camioneta     |  S/ 10.00/h  | * De 6 a 10 horas: +40% de recargo      
          Trailer       |  S/ 20.00/h  | * Mas de 10 horas: +50% de recargo      
        ----------------+--------------+-----------------------------------------
          * Cliente frecuente       : 10% de descuento adicional.                
          * Descuento por volumen   : 20% si el monto supera los S/ 500.00.      
          * Impuestos               : Se agregara el 18% de IGV a la operacion.  
          * Tiempo minimo a cobrar  : 1 hora por vehiculo.                       
        =========================================================================
    """.trimIndent()

    println(tablaTarifas)
    println()

    var cantidad = 0
    while (true) {
        print("> Ingrese la cantidad de vehiculos a registrar (maximo 30): ")
        val input = readln().toIntOrNull()
        if (input != null && input in 1..30) {
            cantidad = input
            break
        }
        println("  [!] Entrada invalida. Debe ser un numero entero entre 1 y 30.")
    }

    var recaudacionTotal = 0.0

    for (i in 1..cantidad) {
        println("\n--------------------------------------------")
        println("       REGISTRO VEHICULO Nro $i DE $cantidad         ")
        println("--------------------------------------------")

        print("> Placa: ")
        val placa = readln().trim()

        var tipo = ""
        var tarifaBase = 0.0
        while (true) {
            print("> Tipo (Moto, Auto, Camioneta, Trailer): ")
            val inputTipo = readln().trim().lowercase()
            when (inputTipo) {
                "moto" -> {
                    tipo = "Moto"
                    tarifaBase = 2.0
                    break
                }
                "auto", "carro" -> {
                    tipo = "Auto"
                    tarifaBase = 4.0
                    break
                }
                "camioneta" -> {
                    tipo = "Camioneta"
                    tarifaBase = 10.0
                    break
                }
                "trailer" -> {
                    tipo = "Trailer"
                    tarifaBase = 20.0
                    break
                }
                else -> println("  [!] Tipo invalido. Verifique su escritura e ingrese Moto, Auto, Camioneta o Trailer.")
            }
        }

        var horas = 0
        while (true) {
            print("> Horas de estadia (minimo 1): ")
            val inputHoras = readln().toIntOrNull()
            if (inputHoras != null && inputHoras >= 1) {
                horas = inputHoras
                break
            }
            println("  [!] Ningun vehiculo puede registrar menos de 1 hora.")
        }

        print("> Cliente: ")
        val cliente = readln().trim()

        var frecuente = false
        while (true) {
            print("> Es Cliente Frecuente? (S/N): ")
            val inputFrec = readln().trim().lowercase()
            if (inputFrec in listOf("s", "si", "true", "y", "yes")) {
                frecuente = true
                break
            } else if (inputFrec in listOf("n", "no", "false")) {
                frecuente = false
                break
            }
            println("  [!] Opcion invalida. Ingrese S (Si) o N (No).")
        }

        println("\n==========================================")
        println("               BOLETA DE PAGO             ")
        println("==========================================")
        println(" Cliente : $cliente")
        println(" Placa   : $placa")
        println(" Tipo    : $tipo")
        println(" Horas   : $horas")
        println(String.format(Locale.US, " TARIFA BASICA: S/ %.2f", tarifaBase))
        println("------------------------------------------")
        println(String.format(" %-6s | %-8s | %-7s | %-8s", "Hora", "Tarifa", "Recargo", "Importe"))
        println("--------+----------+---------+------------")

        var subtotal = 0.0
        for (h in 1..horas) {
            val recargoPct: String
            val importeHora: Double

            if (h <= 2) {
                recargoPct = "0%"
                importeHora = tarifaBase
            } else if (h <= 5) {
                recargoPct = "20%"
                importeHora = tarifaBase * 1.20
            } else if (h <= 10) {
                recargoPct = "40%"
                importeHora = tarifaBase * 1.40
            } else {
                recargoPct = "50%"
                importeHora = tarifaBase * 1.50
            }

            subtotal += importeHora
            println(
                String.format(
                    Locale.US,
                    " %-6d | %-8.2f | %-7s | %-8.2f",
                    h,
                    tarifaBase,
                    recargoPct,
                    importeHora
                )
            )
        }

        println("------------------------------------------")
        var baseImponible = subtotal

        println(String.format(Locale.US, " Subtotal:               S/ %8.2f", subtotal))

        // 1. Primero se evalúa el descuento por volumen (> 500)
        if (subtotal > 500.0) {
            val descuentoVolumen = subtotal * 0.20
            baseImponible -= descuentoVolumen
            println(String.format(Locale.US, " Descuento >S/500 (20%%):-S/ %8.2f", descuentoVolumen))
        }

        // 2. Después se aplica el descuento de cliente frecuente (10%) sobre el saldo resultante
        if (frecuente) {
            val descuentoFrecuente = baseImponible * 0.10
            baseImponible -= descuentoFrecuente
            println(String.format(Locale.US, " Descuento Frec. (10%%): -S/ %8.2f", descuentoFrecuente))
        }

        val igv = baseImponible * 0.18
        val totalPagar = baseImponible + igv

        println(String.format(Locale.US, " IGV (18%%):              S/ %8.2f", igv))
        println(String.format(Locale.US, " TOTAL A PAGAR:          S/ %8.2f", totalPagar))
        println("==========================================\n")

        recaudacionTotal += totalPagar
    }

    println("::::::::::::::::::::::::::::::::::::::::::::::::::")
    println(String.format(Locale.US, "  >>> RECAUDACION TOTAL DEL DIA: S/ %.2f <<<", recaudacionTotal))
    println("::::::::::::::::::::::::::::::::::::::::::::::::::")
}