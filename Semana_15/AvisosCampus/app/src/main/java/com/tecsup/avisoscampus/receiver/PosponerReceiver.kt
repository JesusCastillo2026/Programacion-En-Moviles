package com.tecsup.avisoscampus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.tecsup.avisoscampus.worker.PosponerWorker
import java.util.concurrent.TimeUnit

class PosponerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val datos = Data.Builder()
            .putInt(PosponerWorker.ID, intent?.getIntExtra(PosponerWorker.ID, 0) ?: 0)
            .putString(PosponerWorker.TITULO, intent?.getStringExtra(PosponerWorker.TITULO).orEmpty())
            .putString(PosponerWorker.TEXTO, intent?.getStringExtra(PosponerWorker.TEXTO).orEmpty())
            .build()
        val trabajo = OneTimeWorkRequestBuilder<PosponerWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setInputData(datos)
            .build()
        WorkManager.getInstance(context).enqueue(trabajo)
    }
}
