package com.tecsup.cinecastillo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tecsup.cinecastillo.ui.theme.CineTheme
import com.tecsup.cinecastillo.ui.navigation.CineApp
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SyncNotifications.createChannel(this)
        val container = (application as CineApplication).container
        setContent { CineTheme { CineApp(container) } }
    }
}
