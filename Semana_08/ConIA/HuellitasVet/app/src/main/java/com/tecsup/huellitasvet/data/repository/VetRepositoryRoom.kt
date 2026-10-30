package com.tecsup.huellitasvet.data.repository

import androidx.room.withTransaction
import com.tecsup.huellitasvet.data.local.VetDatabase
import com.tecsup.huellitasvet.data.local.entity.Cita
import com.tecsup.huellitasvet.data.local.entity.CitaServicio
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.local.entity.Usuario
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.local.relation.MascotaConDueno
import com.tecsup.huellitasvet.model.ConteoServicio
import com.tecsup.huellitasvet.model.EstadoCita
import com.tecsup.huellitasvet.model.Horarios
import com.tecsup.huellitasvet.model.UltimaVisita
import com.tecsup.huellitasvet.model.PuntoPeso
import com.tecsup.huellitasvet.model.DiaAgenda
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Locale

class VetRepositoryRoom(private val db: VetDatabase) : VetRepository {
    override suspend fun login(correo: String, clave: String): Usuario? =
        db.usuarioDao().login(correo.trim().lowercase(Locale.ROOT), clave)
    override suspend fun registrarUsuario(usuario: Usuario): Boolean =
        db.usuarioDao().registrar(usuario.copy(nombre = usuario.nombre.trim(), correo = usuario.correo.trim().lowercase(Locale.ROOT))) > 0
    override suspend fun obtenerUsuario(id: Int): Usuario? = db.usuarioDao().obtener(id)
    override fun observarVeterinarios(): Flow<List<Usuario>> = db.usuarioDao().observarPorRol("VETERINARIO")
    override fun observarServicios(): Flow<List<Servicio>> = db.servicioDao().observarTodos()
    override fun observarServiciosActivos(): Flow<List<Servicio>> = db.servicioDao().observarActivos()
    override suspend fun guardarServicio(servicio: Servicio) {
        require(servicio.nombre.isNotBlank() && servicio.categorias.isNotBlank() && servicio.precio > 0 && servicio.duracionMin in listOf(15, 30, 45, 60))
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
    override suspend fun crearCita(mascotaId: Int, serviciosIds: List<Int>, fecha: String, hora: String, nota: String): Long = db.withTransaction {
        val mascota = db.mascotaDao().obtener(mascotaId) ?: error("Mascota no encontrada")
        require(db.usuarioDao().obtener(mascota.duenoId)?.rol == "CLIENTE") { "Dueño no válido" }
        val ids = serviciosIds.distinct()
        require(ids.isNotEmpty()) { "Selecciona al menos un servicio" }
        val servicios = ids.map { db.servicioDao().obtener(it) ?: error("Servicio no encontrado") }
        require(servicios.all { it.activo }) { "Uno de los servicios ya no está disponible" }
        val duracionTotal = servicios.sumOf { it.duracionMin }
        val primerServicio = servicios.first()
        val dia = LocalDate.parse(fecha)
        val ahora = LocalDateTime.now()
        require(!dia.isBefore(ahora.toLocalDate()) && !dia.isAfter(ahora.toLocalDate().plusDays(7))) { "Fecha fuera de rango" }
        require(hora in Horarios.paraFecha(dia, duracionTotal) && LocalDateTime.of(dia, LocalTime.parse(hora)).isAfter(ahora)) { "Horario no disponible para la duración seleccionada" }
        val inicioNuevo = LocalDateTime.of(dia, LocalTime.parse(hora))
        val finNuevo = inicioNuevo.plusMinutes(duracionTotal.toLong())
        val cruzaOtraCita = db.citaDao().obtenerCitasActivasDeFecha(fecha).any { existente ->
            val serviciosIdsExistentes = db.citaDao().obtenerServiciosDeCita(existente.id).ifEmpty { listOf(existente.servicioId) }
            val minutosExistentes = serviciosIdsExistentes.sumOf { db.servicioDao().obtener(it)?.duracionMin ?: 30 }
            val inicioExistente = LocalDateTime.of(dia, LocalTime.parse(existente.hora))
            inicioNuevo < inicioExistente.plusMinutes(minutosExistentes.toLong()) && finNuevo > inicioExistente
        }
        require(!cruzaOtraCita) { "Ese horario se cruza con otra cita; elige una hora libre" }
        val citaId = db.citaDao().insertar(Cita(mascotaId = mascotaId, servicioId = primerServicio.id, fecha = fecha, hora = hora, nota = nota, estado = EstadoCita.PENDIENTE.name, precio = servicios.sumOf { it.precio })).toInt()
        db.citaDao().insertarServiciosCita(ids.map { CitaServicio(citaId, it) })
        citaId.toLong()
    }
    override fun observarCitasDeDueno(duenoId: Int): Flow<List<CitaConDetalle>> = db.citaDao().observarPorDueno(duenoId)
    override fun observarCitasPorFecha(fecha: String): Flow<List<CitaConDetalle>> = db.citaDao().observarPorFecha(fecha)
    override fun observarCita(id: Int): Flow<CitaConDetalle?> = db.citaDao().observarUna(id)
    override fun observarHistorial(mascotaId: Int): Flow<List<CitaConDetalle>> = db.citaDao().observarHistorial(mascotaId)
    override fun observarPesos(mascotaId: Int): Flow<List<PuntoPeso>> = db.citaDao().observarPesos(mascotaId)
    override fun observarAgendaMensual(inicio: String, fin: String): Flow<List<DiaAgenda>> = db.citaDao().observarAgendaMensual(inicio, fin)

    /** Verifica propiedad, plazo y disponibilidad dentro de una única transacción. */
    override suspend fun reprogramarCita(id: Int, duenoId: Int, fecha: String, hora: String) = db.withTransaction {
        val cita = db.citaDao().obtener(id) ?: error("Cita no encontrada")
        val mascota = db.mascotaDao().obtener(cita.mascotaId) ?: error("Mascota no encontrada")
        require(mascota.duenoId == duenoId) { "La cita no pertenece a esta cuenta" }
        require(cita.estado in listOf("PENDIENTE", "CONFIRMADA")) { "La cita ya está cerrada" }
        val ahora = LocalDateTime.now()
        val original = LocalDateTime.of(LocalDate.parse(cita.fecha), LocalTime.parse(cita.hora))
        require(original.isAfter(ahora.plusHours(2))) { "Solo se puede reprogramar con más de 2 horas de anticipación" }
        val dia = LocalDate.parse(fecha)
        require(!dia.isBefore(ahora.toLocalDate()) && !dia.isAfter(ahora.toLocalDate().plusDays(7))) { "Fecha fuera de rango" }
        val servicioIds = db.citaDao().obtenerServiciosDeCita(cita.id).ifEmpty { listOf(cita.servicioId) }
        val duracion = servicioIds.sumOf { db.servicioDao().obtener(it)?.duracionMin ?: 30 }
        require(hora in Horarios.paraFecha(dia, duracion) && LocalDateTime.of(dia, LocalTime.parse(hora)).isAfter(ahora)) { "Elige una hora futura dentro del horario de atención" }
        require(fecha != cita.fecha || hora != cita.hora) { "Selecciona un horario distinto" }
        val inicioNuevo = LocalDateTime.of(dia, LocalTime.parse(hora))
        val finNuevo = inicioNuevo.plusMinutes(duracion.toLong())
        val cruzaOtraCita = db.citaDao().obtenerCitasActivasDeFecha(fecha).filter { it.id != cita.id }.any { existente ->
            val idsExistentes = db.citaDao().obtenerServiciosDeCita(existente.id).ifEmpty { listOf(existente.servicioId) }
            val minutos = idsExistentes.sumOf { db.servicioDao().obtener(it)?.duracionMin ?: 30 }
            val inicio = LocalDateTime.of(dia, LocalTime.parse(existente.hora))
            inicioNuevo < inicio.plusMinutes(minutos.toLong()) && finNuevo > inicio
        }
        require(!cruzaOtraCita) { "Horario ocupado" }
        db.citaDao().reprogramar(id, fecha, hora)
    }
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
    override fun observarUltimasVisitas(): Flow<List<UltimaVisita>> = db.citaDao().observarUltimasVisitas()
}
