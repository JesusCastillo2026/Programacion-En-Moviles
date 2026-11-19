# Pruebas de aceptación

Registrar el resultado solo después de ejecutar cada caso en el emulador. La prueba del mapa requiere Maps SDK for Android configurado y una clave válida guardada localmente.

| # | Caso | Resultado esperado | Estado |
|---:|---|---|---|
| 1 | Abrir y pulsar Obtener mi ubicación | Android muestra la solicitud de permiso | Aprobado |
| 2 | Aceptar permiso | Se muestra la posición y el marcador “Estás aquí” | Parcial: ubicación obtenida; mosaicos y marcador requieren clave Maps |
| 3 | Denegar permiso | Aparece un error con “Abrir ajustes”; la app sigue abierta | Parcial: error y acción visibles; apertura de ajustes no ejecutada |
| 4 | Situar el emulador en el punto base | Se ven los cinco marcadores cerca de la posición | Pendiente: clave Maps |
| 5 | Tocar un marcador del mapa | Se muestra nombre y categoría | Pendiente: clave Maps |
| 6 | Revisar la lista | Los cinco lugares aparecen de menor a mayor distancia, en m o km | Aprobado |
| 7 | Seleccionar una tarjeta | La tarjeta queda resaltada y el mapa se mueve al punto | Parcial: selección visual disponible; cámara del mapa pendiente de clave |
| 8 | Pulsar “Ir al más cercano” | El mapa enfoca el marcador verde; el botón antes de buscar está deshabilitado | Parcial: el destino más cercano se calculó; el foco visual requiere clave |
| 9 | Cambiar la ubicación y volver a buscar | Distancias, orden y marcador verde se actualizan | Parcial: no pudo aislarse un cambio controlado; requiere repetirse con la clave Maps |
| 10 | Apagar ubicación del emulador | Se muestra el mensaje de GPS con “Reintentar” | Aprobado |
| 11 | Rotar la pantalla | La posición y la lista permanecen | Parcial: se observó el cambio de orientación; actividad reiniciada y estado no persistió |
| 12 | Revisar la seguridad del repositorio | No hay clave en Git y `local.properties` está excluido | Aprobado |

## Registro de ejecución

| Fecha | Dispositivo | Compilación | Casos aprobados | Observaciones |
|---|---|---|---:|---|
| 19/11/2026 | Pixel 8 API 37.2, Android Emulator | `:app:testDebugUnitTest :app:assembleDebug` aprobados | 3 aprobados, 6 parciales, 3 pendientes | Falta `MAPS_API_KEY`; marcadores, mosaicos, animación de cámara y estado tras rotación no se dan por aprobados. |

Las capturas de evidencia están en `docs/evidencias/`. La compilación y cinco pruebas unitarias pasan; el resultado de aceptación se informa por caso y no se marca como completo mientras falte la clave o una ejecución pendiente.

