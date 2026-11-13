package com.tecsup.cinecastillo.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.tecsup.cinecastillo.SyncNotifications

/** Implementación Android: conserva el aviso mientras se resuelve el permiso. */
class NotificadorAndroid(private val context: Context) : Notificador {
    private var pendiente: Pair<String, String>? = null

    override fun mostrar(titulo: String, texto: String) {
        if (puedeNotificar()) SyncNotifications.show(context, titulo, texto)
        else pendiente = titulo to texto
    }

    fun permisoActualizado(concedido: Boolean) {
        val aviso = pendiente ?: return
        pendiente = null
        if (concedido) SyncNotifications.show(context, aviso.first, aviso.second)
    }

    private fun puedeNotificar() = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
}
