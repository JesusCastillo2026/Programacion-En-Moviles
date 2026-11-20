# Registro de pruebas de aceptación · main

Entorno: emulador Pixel 8 API 37.2, orientación vertical, 19 de noviembre de 2026. Se probaron interacciones reales de la app instalada.

| N.º | Caso | Evidencia / resultado | Estado |
|---:|---|---|:---:|
| 1 | Tocar el contador durante una tarea activa | El contador pasó de 0 a 1 mientras la barra avanzaba. | Aprobada |
| 2 | Revisar Logcat con filtro `HILO` | Se registró `Antes: main`, `Dentro: DefaultDispatcher-worker-1` y `Después: main`. | Aprobada |
| 3 | Iniciar la tarea con progreso | Alcanzó 100 % y terminó en aproximadamente cuatro segundos. | Aprobada |
| 4 | Cancelar a mitad y volver a iniciar | El mensaje cambió a Cancelado; el botón de inicio volvió a habilitarse y la nueva ejecución terminó. | Aprobada |
| 5 | Pulsar Iniciar dos veces durante la ejecución | El botón quedó deshabilitado mientras `corriendo` fue verdadero. | Aprobada |
| 6 | Contar primos hasta 1,000 | El resultado fue 168. | Aprobada |
| 7 | Iniciar un cálculo grande y cancelarlo | El cálculo se detuvo, la interfaz siguió respondiendo y mostró “Cálculo cancelado”. | Aprobada |
| 8 | Ejecutar la carga secuencial | Perfil ficticio y tres pedidos; 5,037 ms. | Aprobada |
| 9 | Ejecutar la carga paralela | Perfil ficticio y tres pedidos; 3,006 ms. | Aprobada |
| 10 | Simular error, apagarlo y reintentar | Se mostró “Sin conexión”; al desactivar el interruptor y reintentar, la carga terminó con éxito. | Aprobada |
| 11 | Rotar mientras la tarea estaba activa | El progreso siguió avanzando después del cambio de orientación; no volvió a cero. | Aprobada |
| 12 | Revisar APIs prohibidas en fuentes Kotlin | Búsqueda en el código final sin coincidencias. | Aprobada |

Las capturas de pantalla completas están en `docs/capturas`. El registro de Logcat está en `logcat-hilo-main.txt`.

## Repetición en mejora-ia

Se comprobaron en Pixel 8 API 37.2 el progreso y contador interactivos, cancelación, cálculo de primos con avance, cargas secuencial y concurrente, error simulado y recuperación mediante reintento. Las evidencias correspondientes están en docs/capturas. La revisión final de compilación de esta rama quedó pendiente por una denegación de acceso al directorio de trabajo del entorno; la compilación de main sí terminó correctamente.
