# Registro de prompts de ConIA

## Alcance de esta documentación

Este archivo registra las solicitudes y ajustes correspondientes a la rama `mejora-ia` y sus proyectos en `ConIA`.

La misma solicitud apareció primero sin la indicación del repositorio y después ampliada con esa estructura. No hubo otros prompts del alumno durante esta implementación. Las siguientes decisiones y correcciones son del asistente; no se presentan como solicitudes adicionales ni como correcciones hechas personalmente por el alumno.

## Interpretación y decisiones

- Se leyó el DOCX y se inspeccionaron sus cuatro imágenes incrustadas.
- Se conservaron las carpetas solicitadas y además las ramas main y mejora-ia exigidas por el docente.
- La guía permite elegir un tema; se construyeron ambos porque el alumno lo pidió explícitamente.
- Se usaron Kotlin, Compose, Navigation Compose y estado en memoria. No se incorporó MVVM ni ViewModel.
- Las versiones ConIA parten del código base copiado desde main; no son aplicaciones construidas desde cero en una rama sin relación.

## Mejoras de la segunda fase

- Búsqueda por nombre y categoría combinada con los filtros.
- Cabecera destacada y tarjetas con una presentación visual más elaborada.
- Cancelación con AlertDialog; conservar una reserva no la cambia y confirmar la marca Cancelada.
- Filtros de reservas por estado y recuentos de próximas y completadas.
- Estadísticas del perfil derivadas de las reservas de la sesión.
- Comentarios en el código sobre estado, parámetros, navegación, accesibilidad y reglas de disponibilidad.

## Ajustes reales durante el trabajo

1. La lectura inicial del documento falló porque estaba abierto por otro proceso. Se leyó con acceso compartido, sin cerrar Word ni modificar el original.
2. Se corrigió la interpretación de ocho commits: la guía pide un mínimo de ocho en main y tres adicionales en mejora-ia.
3. Las figuras de Fit no muestran todos los controles descritos en la rúbrica. Se añadió una pantalla de elección de fecha y horario antes de confirmar.
4. Se añadió regreso al inicio en confirmación además del botón que lleva al listado.
5. Se conservaron fechas, nombres y datos demostrativos de las referencias; no representan datos clínicos ni reservas reales.
6. Se evitó duplicar una reserva activa del mismo elemento, fecha y hora. Una cancelada deja de bloquear el horario.
7. El SDK instalado estaba bloqueado por Windows. La verificación utilizó un SDK descargado en la carpeta de trabajo, sin incluirlo en Git.
8. La compilación necesitó Build Tools 37.0.0 explícito. Se comprobó la compilación de Kotlin y la generación de APK usando la configuración del laboratorio.
9. La creación automática del bloqueo de la clave de depuración falló por permisos. Se generó una clave local de pruebas y se omitió únicamente esa tarea de validación del entorno al empaquetar. La clave no se incluye en el repositorio.

## Autoría y límites

Los commits de esta rama registran las mejoras funcionales y la documentación técnica de `ConIA`.

La guía de sustentación explica el código, pero la comprensión y defensa oral corresponden al alumno. La persistencia entre reinicios no está implementada. La equivalencia visual píxel a píxel y las pruebas manuales en emulador no se consideran verificadas solo por compilar.

