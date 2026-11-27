package com.tecsup.avisoscampus.viewmodel

import com.tecsup.avisoscampus.data.AvisosRepositoryMemoria
import com.tecsup.avisoscampus.model.Origen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AvisosViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun botonRegistraAvisoYEmiteEvento() = runTest(mainDispatcherRule.dispatcher) {
        val repo = AvisosRepositoryMemoria()
        val vm = AvisosViewModel(repo)
        val evento = backgroundScope.async(UnconfinedTestDispatcher(testScheduler)) {
            vm.eventos.first()
        }

        vm.avisarAhora()
        runCurrent()

        assertEquals("Tarea lista", evento.await().titulo)
        assertEquals("Tu entrega fue registrada", repo.avisos.value.single().texto)
        assertEquals(Origen.BOTON, repo.avisos.value.single().origen)
    }

    @Test
    fun recordatorioTerminaComoAvisoUrgente() = runTest(mainDispatcherRule.dispatcher) {
        val repo = AvisosRepositoryMemoria()
        val vm = AvisosViewModel(repo)
        val evento = backgroundScope.async(UnconfinedTestDispatcher(testScheduler)) {
            vm.eventos.first()
        }

        vm.recordarEn(2)
        runCurrent()
        advanceTimeBy(2_000)
        runCurrent()

        assertEquals(Origen.RECORDATORIO, evento.await().origen)
        assertTrue(evento.await().urgente)
    }

    @Test
    fun cancelarRecordatorioNoGuardaAviso() = runTest(mainDispatcherRule.dispatcher) {
        val repo = AvisosRepositoryMemoria()
        val vm = AvisosViewModel(repo)

        vm.recordarEn(10)
        runCurrent()
        vm.cancelarRecordatorio()
        runCurrent()

        assertTrue(repo.avisos.value.isEmpty())
        assertEquals(null, vm.faltan.value)
    }
}


