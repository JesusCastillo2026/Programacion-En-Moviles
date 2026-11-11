# GymTracker Pro - ConIA

Versión ampliada del Laboratorio 07 para Android Studio, Kotlin, Jetpack Compose y Room. Abre directamente esta carpeta en Android Studio.

## Funciones

- Registro y autenticación local con Navigation Compose y paso de `usuarioId`.
- Room/SQLite como fuente persistente offline para las tablas `Usuario` y `Rutina`.
- Operaciones CREATE, READ, UPDATE y DELETE asociadas al usuario autenticado.
- Búsqueda y filtro por grupo muscular en el historial, con confirmación antes de borrar.
- Perfil con estadísticas reactivas: cantidad de rutinas y volumen calculado por SQL.
- Interfaz Material 3 con una portada gráfica propia, tarjetas de progreso, estados vacíos y campos validados.

Los datos se guardan localmente en `gymtracker.db`. La contraseña sigue el esquema académico simplificado del PDF; esta app es una demostración offline y no debe usarse como sistema real de autenticación.

## Abrir

En Android Studio selecciona **Open** y elige esta carpeta. Ejecuta la configuración `app` en un emulador o dispositivo Android API 24 o superior.
