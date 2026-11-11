# Registro de indicaciones y mejoras · Cine Castillo

Este archivo resume las indicaciones recibidas para la fase de mejora y las decisiones comprobables en el proyecto. No contiene contraseñas ni la clave de TMDB.

| Indicación recibida | Respuesta aplicada | Ajuste o corrección |
|---|---|---|
| “El tema tratará sobre PELÍCULAS”; trabajo individual y proyecto nuevo en Firebase. | Se creó Cine Castillo con Firebase Authentication, roles en Firestore y catálogo de películas. | Se registró el paquete exacto `com.tecsup.cinecastillo` para enlazar el archivo Android correcto. |
| El docente indicó no considerar las API básicas del documento. | Se eligió TMDB para catálogo, detalle, imágenes y valoración, con créditos dentro de la app. | La clave queda en `local.properties`, fuera de Git. Se añadió el aviso y logotipo oficiales de TMDB. |
| Completar la guía y mantener avances en GitHub. | La fase base está en `main` y la ampliación en `mejora-ia`. | Se añadieron filtros de orden y una prueba que comprueba la combinación de orden y favoritos. |

## Correcciones durante el desarrollo

- Se ajustaron las versiones de Kotlin y KSP para que Gradle compilara.
- Se comprobó que el archivo de Firebase descargado correspondiera a este proyecto y paquete; otro archivo descargado no era JSON válido.
- Se configuraron dos usuarios de prueba con roles distintos en Firestore y reglas que impiden modificar el propio rol desde la aplicación.
- Se evitó perder la notificación cuando la sincronización termina mientras Android todavía pregunta por el permiso.

## Límites de la verificación

La compilación y las pruebas unitarias no sustituyen la revisión visual en el emulador. Las capturas de esa revisión se agregan al informe después de ejecutar la app en Android Studio.
