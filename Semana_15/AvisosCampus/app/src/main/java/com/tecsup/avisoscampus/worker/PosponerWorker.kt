package com.tecsup.avisoscampus.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tecsup.avisoscampus.data.AvisosRepositoryLocal
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import com.tecsup.avisoscampus.notificacion.Notificador
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PosponerWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val aviso = Aviso(
            id = inputData.getInt(ID, System.currentTimeMillis().toInt()),
            titulo = inputData.getString(TITULO).orEmpty().ifBlank { "Recordatorio" },
            texto = inputData.getString(TEXTO).orEmpty().ifBlank { "Revisa tu laboratorio pendiente" },
            origen = Origen.RECORDATORIO,
            hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
            urgente = true
        )
        AvisosRepositoryLocal(applicationContext).registrar(aviso)
        Notificador.mostrar(applicationContext, aviso)
        return Result.success()
    }

    companion object {
        const val ID = "aviso_id"
        const val TITULO = "aviso_titulo"
        const val TEXTO = "aviso_texto"
    }
}
