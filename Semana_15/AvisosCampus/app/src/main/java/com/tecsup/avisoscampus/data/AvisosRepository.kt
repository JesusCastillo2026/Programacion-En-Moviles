package com.tecsup.avisoscampus.data

import com.tecsup.avisoscampus.model.Aviso
import kotlinx.coroutines.flow.StateFlow

interface AvisosRepository {
    val avisos: StateFlow<List<Aviso>>
    suspend fun registrar(aviso: Aviso)
}

class AvisosRepositoryMemoria : AvisosRepository {
    private val _avisos = kotlinx.coroutines.flow.MutableStateFlow<List<Aviso>>(emptyList())
    override val avisos: StateFlow<List<Aviso>> = _avisos

    override suspend fun registrar(aviso: Aviso) {
        _avisos.value = listOf(aviso) + _avisos.value
    }
}
