package com.tecsup.huellitasvet.model

/** Una medición registrada durante una atención, ordenada cronológicamente por Room. */
data class PuntoPeso(val fecha: String, val hora: String, val peso: Double)
