package com.tecsup.huellitasvet.data.local

import androidx.room.withTransaction
import com.tecsup.huellitasvet.data.local.entity.Cita
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.local.entity.Usuario
import java.time.LocalDate

object DatosIniciales {
    suspend fun sembrar(db: VetDatabase) {
        db.withTransaction {
            if (db.usuarioDao().contar() != 0) return@withTransaction
            db.servicioDao().insertarTodos(listOf(
                Servicio(nombre = "Consulta general", descripcion = "Evaluación integral", duracionMin = 30, precio = 60.0, icono = "heart"),
                Servicio(nombre = "Vacunación", descripcion = "Vacunas preventivas", duracionMin = 30, precio = 45.0, icono = "syringe"),
                Servicio(nombre = "Baño y corte", descripcion = "Higiene y estética", duracionMin = 60, precio = 50.0, icono = "scissors"),
                Servicio(nombre = "Desparasitación", descripcion = "Control antiparasitario", duracionMin = 15, precio = 25.0, activo = false, icono = "heart")
            ))
            db.usuarioDao().insertarTodos(listOf(
                Usuario(nombre = "Ana Torres", correo = "ana@correo.test", clave = "1234", rol = "CLIENTE"),
                Usuario(nombre = "Luis Vega", correo = "luis@correo.test", clave = "1234", rol = "CLIENTE"),
                Usuario(nombre = "Dra. Valeria Paz", correo = "vet@huellitas.test", clave = "admin", rol = "ADMIN")
            ))
            val luna = db.mascotaDao().insertar(Mascota(duenoId = 1, nombre = "Luna", especie = "PERRO", raza = "Mestiza", anioNacimiento = 2020, peso = 12.4, sexo = "HEMBRA")).toInt()
            val max = db.mascotaDao().insertar(Mascota(duenoId = 1, nombre = "Max", especie = "GATO", raza = "Criollo", anioNacimiento = 2022, peso = 4.2, sexo = "MACHO")).toInt()
            val rocky = db.mascotaDao().insertar(Mascota(duenoId = 2, nombre = "Rocky", especie = "PERRO", raza = "Labrador", anioNacimiento = 2019, peso = 28.0, sexo = "MACHO")).toInt()
            val michi = db.mascotaDao().insertar(Mascota(duenoId = 2, nombre = "Michi", especie = "GATO", raza = "Atigrado", anioNacimiento = 2023, peso = 3.8, sexo = "HEMBRA")).toInt()
            val hoy = LocalDate.now()
            val citas = listOf(
                Cita(mascotaId = luna, servicioId = 1, fecha = hoy.minusDays(45).toString(), hora = "10:00", nota = "Control", estado = "ATENDIDA", precio = 60.0, diagnostico = "Control general sin novedades", tratamiento = "Seguimiento habitual", pesoRegistrado = 11.8),
                Cita(mascotaId = luna, servicioId = 2, fecha = hoy.minusDays(20).toString(), hora = "11:00", nota = "Vacunas", estado = "ATENDIDA", precio = 45.0, diagnostico = "Vacunación de rutina", tratamiento = "Próxima dosis según cartilla", pesoRegistrado = 12.0),
                Cita(mascotaId = luna, servicioId = 1, fecha = hoy.minusDays(2).toString(), hora = "09:30", nota = "Revisión", estado = "ATENDIDA", precio = 60.0, diagnostico = "Buen estado general", tratamiento = "Continuar cuidados", pesoRegistrado = 12.4),
                Cita(mascotaId = max, servicioId = 1, fecha = hoy.minusDays(10).toString(), hora = "15:00", nota = "Consulta", estado = "ATENDIDA", precio = 60.0, diagnostico = "Apetito normal", tratamiento = "Control de peso", pesoRegistrado = 4.2),
                Cita(mascotaId = rocky, servicioId = 1, fecha = hoy.minusDays(6).toString(), hora = "10:30", nota = "Control", estado = "ATENDIDA", precio = 60.0, diagnostico = "Paciente estable", tratamiento = "Reposo y agua", pesoRegistrado = 28.0),
                Cita(mascotaId = luna, servicioId = 1, fecha = hoy.toString(), hora = "16:00", nota = "Consulta", estado = "PENDIENTE", precio = 60.0),
                Cita(mascotaId = michi, servicioId = 2, fecha = hoy.toString(), hora = "17:00", nota = "Vacunación", estado = "CONFIRMADA", precio = 45.0),
                Cita(mascotaId = max, servicioId = 3, fecha = hoy.plusDays(1).toString(), hora = "09:00", nota = "Baño", estado = "PENDIENTE", precio = 50.0),
                Cita(mascotaId = rocky, servicioId = 1, fecha = hoy.plusDays(2).toString(), hora = "12:00", nota = "Consulta", estado = "CONFIRMADA", precio = 60.0),
                Cita(mascotaId = luna, servicioId = 3, fecha = hoy.plusDays(3).toString(), hora = "14:00", nota = "Aseo", estado = "CANCELADA", precio = 50.0)
            )
            citas.forEach { db.citaDao().insertar(it) }
        }
    }
}
