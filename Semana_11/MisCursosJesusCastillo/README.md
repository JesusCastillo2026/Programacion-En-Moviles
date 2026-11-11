# Mis Cursos - Semana 11

Proyecto Android con Kotlin y Jetpack Compose para el laboratorio de autenticacion y gestion de cursos.

## Abrir en Android Studio

Abre la carpeta que contiene este README. Android Studio debe reconocer `settings.gradle.kts` y sincronizar Gradle.

## Firebase configurado

La aplicacion Android está registrada con el identificador `com.jesuscastillo.miscursos`. El proyecto usa Firebase Authentication, Cloud Firestore y el plugin de Google Services. El archivo `app/google-services.json` contiene la configuración del proyecto Firebase enlazado.

En Firebase, habilita el proveedor de acceso Correo electrónico/contraseña. Firestore se creó en modo de producción. Publica el contenido de `firestore.rules` desde la pestaña **Reglas** de Firestore para permitir operaciones únicamente dentro de `usuarios/{uid}/cursos`, cuando el UID coincida con el usuario autenticado.

Cada curso almacena título, docente, créditos, descripción y fecha de creación. La aplicación registra, lista en tiempo real, edita y elimina cursos dentro de la ruta privada de cada usuario.
