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

## Consulta y revisión final — 11/11/2026

**Herramienta:** ChatGPT Astra.

**Indicación del estudiante:** completar el Laboratorio 12 con temática de películas, trabajo individual y un proyecto nuevo de Firebase; sustituir las API básicas sugeridas por el documento. También se solicitó subir los avances y publicar el APK.

**Respuesta resumida:** se construyó Cine Castillo con autenticación, roles, catálogo TMDB, caché Room, favoritos, panel de administración, notificación de sincronización y atribución de la fuente. Se organizaron las fuentes en capas y se compiló el proyecto.

**Qué se corrigió:** compatibilidad de Kotlin y KSP, archivo correcto de configuración Firebase, conservación de favoritos al sincronizar, ordenamiento rotulado según su función real y notificación al resolverse el permiso de Android.

**Qué aprendí:** la clave de una API integrada en un APK puede extraerse; por ello no se debe tratar como un secreto de servidor. También comprobé que las pruebas unitarias y la compilación no reemplazan las pruebas visuales y de conectividad en el emulador.

## Límites de la verificación

La compilación y las pruebas unitarias no sustituyen la revisión visual en el emulador. Las capturas de esa revisión se agregan al informe después de ejecutar la app en Android Studio.
