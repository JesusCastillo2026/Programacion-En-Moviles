package com.tecsup.avisoscampus.data

import com.tecsup.avisoscampus.model.Aviso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AvisosRepository {
    val avisos: StateFlow<List<Aviso>>
    suspend fun registrar(aviso: Aviso)
    suspend fun limpiarHistorial()
}

class AvisosRepositoryMemoria : AvisosRepository {
    private val _avisos = MutableStateFlow<List<Aviso>>(emptyList())
    override val avisos: StateFlow<List<Aviso>> = _avisos

    override suspend fun registrar(aviso: Aviso) {
        _avisos.value = listOf(aviso) + _avisos.value
    }

    override suspend fun limpiarHistorial() {
        _avisos.value = emptyList()
    }
}
