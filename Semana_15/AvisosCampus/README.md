# Laboratorio 15 · AvisosCampus

Esta rama reúne el laboratorio de notificaciones y ampliaciones de experiencia para AvisosCampus. La aplicación está escrita en Kotlin con Jetpack Compose y Material 3, y mantiene la separación MVVM + Repository.

## Funciones

- Canal normal y canal de alta importancia para recordatorios, con contenido expandible.
- Permiso de notificaciones de Android 13 o superior y acceso a los ajustes de la app.
- Aviso inmediato, cuenta regresiva configurable y cancelación del recordatorio.
- Receptores dinámicos para cambios del modo avión y conexión del cargador; ambos se retiran al salir de la composición.
- Acción para posponer un recordatorio diez segundos mediante un trabajo diferido de WorkManager.
- Historial persistente con DataStore, filtros por origen y opción para borrarlo.
- Tres pruebas unitarias del ViewModel para aviso, vencimiento y cancelación.

## Arquitectura

```text
Pantalla Compose → AvisosViewModel → AvisosRepository
                         ↓ SharedFlow
Pantalla Compose → Notificador → Android

ModoAvionReceiver / CargadorReceiver → callback corto → AvisosViewModel
PosponerReceiver → WorkManager → PosponerWorker → Repository + Notificador
```

El ViewModel no recibe `Context` y no usa servicios del sistema. `AvisosRepositoryLocal` contiene DataStore y transforma los registros persistentes en un `StateFlow`. El Worker programa el aviso pospuesto sin mantener una corrutina ligada a la pantalla.

## Archivos principales

- `app/src/main/java/com/tecsup/avisoscampus/model/Modelos.kt`: avisos, origen y estado del permiso.
- `data/AvisosRepository.kt`: contrato y repositorio de memoria para pruebas.
- `data/AvisosRepositoryLocal.kt`: persistencia local del historial mediante DataStore.
- `notificacion/Notificador.kt`: canales, permiso, construcción y publicación de notificaciones.
- `receiver/ModoAvionReceiver.kt` y `receiver/CargadorReceiver.kt`: recepción breve de broadcasts del sistema.
- `receiver/PosponerReceiver.kt` y `worker/PosponerWorker.kt`: programación del aviso pospuesto.
- `viewmodel/AvisosViewModel.kt`: reglas de presentación, temporizador y eventos únicos.
- `ui/PantallaAvisos.kt` y `ui/HistorialAvisos.kt`: pantalla, controles, filtros y lista limitada a veinte avisos.

## Ejecución y comprobación

Abre esta carpeta en Android Studio, sincroniza Gradle y ejecuta la configuración `app`. El SDK objetivo es Android 15; el emulador debe usar Android 13 o posterior para revisar la solicitud del permiso. Ejecuta `gradlew testDebugUnitTest` y `gradlew assembleDebug` desde esta carpeta.

Las capturas de ejecución se guardarán en `capturas/`. Aún no se adjuntaron imágenes del emulador, así que esta rama no declara evidencia visual aprobada.

## Registro de asistencia

Este trabajo se preparó con asistencia de Codex. `PROMPTS.md` registra las consultas reales y decisiones adoptadas; no atribuye respuestas a Gemini cuando no se utilizó esa herramienta.
