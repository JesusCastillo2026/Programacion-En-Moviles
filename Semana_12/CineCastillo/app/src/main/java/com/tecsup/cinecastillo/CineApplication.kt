package com.tecsup.cinecastillo
import android.app.Application
/** Una instancia por proceso: Room y repositorios sobreviven a la rotación. */
class CineApplication : Application() {
    val container by lazy { AppContainer(this) }
}
