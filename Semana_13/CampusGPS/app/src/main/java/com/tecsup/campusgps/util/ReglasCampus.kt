package com.tecsup.campusgps.util

import com.tecsup.campusgps.model.LugarConDistancia

fun filtrarPorCategoria(
    lugares: List<LugarConDistancia>,
    categoria: String?
): List<LugarConDistancia> =
    if (categoria == null) lugares else lugares.filter { it.lugar.categoria == categoria }

fun dentroDelRadio(metros: Float?, radioMetros: Int): Boolean =
    metros != null && metros <= radioMetros
