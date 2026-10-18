package com.tecsup.huellitasvet.data.repository

import androidx.room.withTransaction
import com.tecsup.huellitasvet.data.local.VetDatabase
import com.tecsup.huellitasvet.data.local.entity.Cita
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.local.entity.Usuario
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.local.relation.MascotaConDueno
import com.tecsup.huellitasvet.model.ConteoServicio
import com.tecsup.huellitasvet.model.EstadoCita
import com.tecsup.huellitasvet.model.Horarios
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class VetRepositoryRoom(private val db: VetDatabase) : VetRepository {
    override suspend fun login(correo: String, clave: String): Usuario? = db.usuarioDao().login(correo.trim(), clave)
    override suspend fun obtenerUsuario(id: Int): Usuario? = db.usuarioDao().obtener(id)
    override fun observarServicios(): Flow<List<Servicio>> = db.servicioDao().observarTodos()
    override fun observarServiciosActivos(): Flow<List<Servicio>> = db.servicioDao().observarActivos()
    override suspend fun guardarServicio(servicio: Servicio) {
        require(servicio.nombre.isNotBlank() && servicio.precio > 0 && servicio.duracionMin in listOf(15, 30, 45, 60))
        if (servicio.id == 0) db.servicioDao().insertar(servicio) else db.servicioDao().actualizar(servicio)
    }
    override suspend fun eliminarServicio(servicio: Servicio) {
        require(!servicioTieneCitas(servicio.id)) { "El servicio tiene citas" }
        db.servicioDao().eliminar(servicio)
    }
    override suspend fun cambiarActivo(id: Int, activo: Boolean) = db.servicioDao().cambiarActivo(id, activo)
    override suspend fun servicioTieneCitas(id: Int): Boolean = db.servicioDao().contarCitas(id) > 0
    override fun observarMascotasDeDueno(duenoId: Int): Flow<List<Mascota>> = db.mascotaDao().observarPorDueno(duenoId)
    override fun observarMascota(id: Int): Flow<Mascota?> = db.mascotaDao().observarUna(id)
    override fun observarTodasLasMascotas(): Flow<List<MascotaConDueno>> = db.mascotaDao().observarTodas()
    override suspend fun guardarMascota(mascota: Mascota) {
        if (mascota.id == 0) db.mascotaDao().insertar(mascota) else db.mascotaDao().actualizar(mascota)
    }
    override suspend fun eliminarMascota(mascota: Mascota) {
        require(!mascotaTieneCitas(mascota.id)) { "La mascota tiene citas" }
        db.mascotaDao().eliminar(mascota)
    }
    override suspend fun mascotaTieneCitas(id: Int): Boolean = db.mascotaDao().contarCitas(id) > 0
    override fun observarHorasOcupadas(fecha: String): Flow<List<String>> = db.citaDao().observarHorasOcupadas(fecha)
    override suspend fun crearCita(mascotaId: Int, servicioId: Int, fecha: String, hora: String, nota: String): Long = db.withTransaction {
        val mascota = db.mascotaDao().obtener(mascotaId) ?: error("Mascota no encontrada")
        require(db.usuarioDao().obtener(mascota.duenoId)?.rol == "CLIENTE") { "Dueño no válido" }
        val servicio = db.servicioDao().obtener(servicioId) ?: error("Servicio no encontrado")
        require(servicio.activo) { "Servicio inactivo" }
        val dia = LocalDate.parse(fecha)
        val ahora = LocalDateTime.now()
        require(!dia.isBefore(ahora.toLocalDate()) && !dia.isAfter(ahora.toLocalDate().plusDays(7))) { "Fecha fuera de rango" }
        require(hora in Horarios.todos && LocalDateTime.of(dia, LocalTime.parse(hora)).isAfter(ahora)) { "Horario no disponible" }
        require(db.citaDao().contarOcupadas(fecha, hora) == 0) { "Horario ocupado" }
        db.citaDao().insertar(Cita(mascotaId = mascotaId, servicioId = servicioId, fecha = fecha, hora = hora, nota = nota, estado = EstadoCita.PENDIENTE.name, precio = servicio.precio))
    }
    override fun observarCitasDeDueno(duenoId: Int): Flow<List<CitaConDetalle>> = db.citaDao().observarPorDueno(duenoId)
    override fun observarCitasPorFecha(fecha: String): Flow<List<CitaConDetalle>> = db.citaDao().observarPorFecha(fecha)
    override fun observarCita(id: Int): Flow<CitaConDetalle?> = db.citaDao().observarUna(id)
    override fun observarHistorial(mascotaId: Int): Flow<List<CitaConDetalle>> = db.citaDao().observarHistorial(mascotaId)
    override suspend fun cambiarEstadoCita(id: Int, estado: EstadoCita) {
        val cita = db.citaDao().obtener(id) ?: error("Cita no encontrada")
        val actual = EstadoCita.valueOf(cita.estado)
        require((actual == EstadoCita.PENDIENTE && estado == EstadoCita.CONFIRMADA) ||
            (actual in listOf(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA) && estado == EstadoCita.CANCELADA)) { "Cambio de estado no permitido" }
        db.citaDao().actualizarEstado(id, estado.name)
    }
    override suspend fun registrarAtencion(citaId: Int, diagnostico: String, tratamiento: String, peso: Double) {
        require(diagnostico.trim().length >= 5 && peso > 0) { "Completa diagnóstico y peso" }
        db.withTransaction {
            val cita = db.citaDao().obtener(citaId) ?: error("Cita no encontrada")
            require(cita.estado in listOf("PENDIENTE", "CONFIRMADA")) { "Esta cita ya está cerrada" }
            db.citaDao().registrarAtencion(citaId, diagnostico.trim(), tratamiento.trim(), peso)
            db.mascotaDao().actualizarPeso(cita.mascotaId, peso)
        }
    }
    override fun observarCitasDeFecha(fecha: String): Flow<Int> = db.citaDao().contarPorFecha(fecha)
    override fun observarCantidadPorEstado(estado: EstadoCita): Flow<Int> = db.citaDao().contarPorEstado(estado.name)
    override fun observarCitasPorServicio(): Flow<List<ConteoServicio>> = db.citaDao().contarPorServicio()
    override fun observarIngresosDesde(fechaInicio: String): Flow<Double> = db.citaDao().ingresosDesde(fechaInicio)
}
