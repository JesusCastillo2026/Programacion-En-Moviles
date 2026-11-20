package com.tecsup.campusgps.ui

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.tecsup.campusgps.R

@Composable
fun IntroVideo(onTerminar: () -> Unit) {
    val terminarActualizado = rememberUpdatedState(onTerminar)
    val reproductor = remember { mutableStateOf<VideoView?>(null) }

    DisposableEffect(Unit) {
        onDispose { reproductor.value?.stopPlayback() }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                VideoView(viewContext).apply {
                    reproductor.value = this
                    setVideoURI(Uri.parse("android.resource://${viewContext.packageName}/${R.raw.campusgps_intro}"))
                    setOnPreparedListener { player ->
                        player.isLooping = false
                        start()
                    }
                    setOnCompletionListener { terminarActualizado.value.invoke() }
                    setOnErrorListener { _, _, _ ->
                        terminarActualizado.value.invoke()
                        true
                    }
                }
            }
        )
        TextButton(
            onClick = onTerminar,
            modifier = Modifier.align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(end = 16.dp, top = 8.dp)
        ) {
            Text("Omitir", color = Color.White)
        }
    }
}
