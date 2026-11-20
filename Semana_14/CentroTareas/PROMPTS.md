# Registro de prompts · mejora-ia

La rama base y la rama mejorada se mantienen separadas. Estos pedidos guiaron las ampliaciones de esta rama y se revisaron para conservar las actividades del laboratorio.

| Pedido | Cambio realizado | Comprobación |
|---|---|---|
| Mejora la interfaz de CentroTareas con una presentación oscura, tipografía clara y tarjetas más cuidadas, conservando las tres actividades del laboratorio. | Paleta nocturna con acento turquesa, panel de resumen y tarjetas numeradas. | Compilación e inspección en Pixel 8 API 37.2. |
| Muestra el avance del cálculo de primos sin bloquear la pantalla y deja cancelar el cálculo desde el ViewModel. | Progreso observable y cancelación cooperativa desde el Repository. | Cálculo de 2,000,000; se observó el porcentaje moverse y el contador siguió utilizable. |
| Guarda un historial pequeño y permite recuperar una carga si falla, sin ocultar errores ni perder la cancelación. | DataStore con los cinco resultados recientes, reintentos y límite temporal; `CancellationException` se relanza. | Error simulado visible, reintento y posterior carga correcta al desactivar el fallo. |
| Ordena la experiencia por etapas y haz que cada ejercicio explique su estado. | Secuencia de tarea/cancelación, primos, comparación y actividad; etiquetas de estado, progreso separado, explicación de tiempos y registro de eventos. | La versión actual compila; la inspección manual de esta renovación queda pendiente. |
| Añade una mejora adicional que demuestre concurrencia y permita controlar los trabajos por separado. | Panel que lanza cuatro tareas con `Job` independientes; cada una muestra estado, avance o tiempo y permite cancelar o reintentar sin afectar a las otras. | Compilación debug exitosa con JDK 21; falta recorrer el panel en el emulador. |
| Unifica el tamaño y la jerarquía del texto y da un acabado más cuidado a las acciones principales. | Escala tipográfica compartida, botones con degradado turquesa-azul y tarjetas oscuras con bordes translúcidos discretos. | Compilación debug exitosa; falta inspección visual en el emulador. |

La mejora adicional queda integrada en esta rama; se conserva la versión base en `main`. Las tareas de perfil y pedidos son datos simulados y se identifican como tales en la interfaz.
