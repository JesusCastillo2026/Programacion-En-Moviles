package com.tecsup.avisoscampus.notificacion

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tecsup.avisoscampus.MainActivity
import com.tecsup.avisoscampus.R
import com.tecsup.avisoscampus.model.Aviso

object Notificador {
    const val CANAL = "avisos"
    const val CANAL_URGENTES = "urgentes"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canales = listOf(
                NotificationChannel(CANAL, "Avisos", NotificationManager.IMPORTANCE_DEFAULT)
                    .apply { description = "Notificaciones de la app" },
                NotificationChannel(CANAL_URGENTES, "Urgentes", NotificationManager.IMPORTANCE_HIGH)
                    .apply { description = "Recordatorios importantes" }
            )
            context.getSystemService(NotificationManager::class.java).createNotificationChannels(canales)
        }
    }

    fun tienePermiso(context: Context): Boolean {
        return Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, aviso: Aviso): Boolean {
        if (!tienePermiso(context)) return false
        val canal = if (aviso.urgente) CANAL_URGENTES else CANAL
        val intent = Intent(context, MainActivity::class.java)
        val pendiente = PendingIntent.getActivity(context, aviso.id, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificacion = NotificationCompat.Builder(context, canal)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(aviso.titulo)
            .setContentText(aviso.texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(aviso.texto))
            .setPriority(if (aviso.urgente) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendiente)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(aviso.id, notificacion)
        return true
    }
}
