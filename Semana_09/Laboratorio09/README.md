# Laboratorio 09 · Consumo de API con Retrofit

Aplicación Android del curso Programación en Móviles de TECSUP. Desarrollada con Kotlin, Jetpack Compose, Retrofit y Room.

## Funcionalidad

- Consulta de publicaciones desde `https://jsonplaceholder.typicode.com/posts`.
- Apertura del detalle de cada publicación con el parámetro `id`.
- Guardado y eliminación de favoritos persistidos localmente en Room.
- Ejercicio adicional que consume el endpoint `/todos`.
- Navegación inferior entre publicaciones, tareas y favoritos.
- Estados de carga, error y reintento para las solicitudes principales.

## Estructura

```text
app/src/main/java/com/tecsup/lab09/
├── data/
│   ├── local/       # Base de datos Room, entidad y DAO de favoritos
│   ├── model/       # Modelos Post y Todo
│   ├── remote/      # Configuración Retrofit y endpoints
│   └── repository/  # Acceso unificado a API y almacenamiento local
├── ui/
│   ├── components/  # Tarjetas y estados de carga/error
│   ├── navigation/  # Rutas y navegación Compose
│   ├── screens/     # Posts, detalle, favoritos y tareas
│   └── theme/       # Paleta de la aplicación
└── viewmodel/       # Estado de interfaz y operaciones asíncronas
```

## Ejecutar

Abre esta carpeta en Android Studio, sincroniza Gradle y ejecuta `app` en un emulador o dispositivo con Internet. Los posts y las tareas vienen del servicio remoto; los favoritos se conservan en la base local del dispositivo.

## Evidencias y reflexión

Consulta [EVIDENCIAS.md](EVIDENCIAS.md) para la lista de capturas y el video pedidos en el laboratorio, y [REFLEXION.md](REFLEXION.md) para las preguntas de observación.
