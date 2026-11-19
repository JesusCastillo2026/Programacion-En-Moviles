# Pruebas de aceptación

El mapa ahora usa MapLibre Native con estilos públicos de OpenFreeMap. No necesita una clave de Google Maps; la descarga del estilo y los mosaicos requiere Internet. Los resultados se actualizan solo después de ejecutar cada caso en el emulador.

| # | Caso | Resultado esperado | Estado |
|---:|---|---|---|
| 1 | Abrir y pulsar Obtener mi ubicación | Android muestra la solicitud de permiso | Aprobado en ejecución anterior |
| 2 | Aceptar permiso | Se muestra la posición y el marcador “Estás aquí” | Pendiente de repetir con OpenFreeMap |
| 3 | Denegar permiso | Aparece un error con “Abrir ajustes”; la app sigue abierta | Parcial: mensaje y acción observados; apertura de ajustes no ejecutada |
| 4 | Situar el emulador en el punto base | Se ven los cinco marcadores cerca de la posición | Pendiente de verificar carga del mapa nuevo |
| 5 | Tocar un marcador del mapa | Se muestra nombre y categoría | Pendiente de verificar tras la migración |
| 6 | Revisar la lista | Los cinco lugares aparecen de menor a mayor distancia, en m o km | Aprobado en ejecución anterior |
| 7 | Seleccionar una tarjeta | La tarjeta queda resaltada y el mapa se mueve al punto | Pendiente de verificar cámara tras la migración |
| 8 | Pulsar “Ir al más cercano” | El mapa enfoca el marcador verde; el botón antes de buscar está deshabilitado | Pendiente de verificar cámara y marcador tras la migración |
| 9 | Cambiar la ubicación y volver a buscar | Distancias, orden y marcador verde se actualizan | Parcial: cálculo probado previamente; falta verificar el mapa nuevo |
| 10 | Apagar ubicación del emulador | Se muestra el mensaje de GPS con “Reintentar” | Aprobado en ejecución anterior |
| 11 | Rotar la pantalla | La posición y la lista permanecen | Parcial: la rotación se observó, pero la posición no persistió |
| 12 | Revisar la configuración del mapa | No se requiere clave de Google ni hay secretos del proveedor en Git | Aprobado por inspección del código |

## Registro de ejecución

| Fecha | Dispositivo | Compilación | Estado | Observaciones |
|---|---|---|---|---|
| 19/11/2026 | Pixel 8 API 37.2, Android Emulator | Ejecución anterior a la migración | 3 aprobados, 6 parciales, 3 pendientes | La evidencia visual corresponde a la integración anterior. No representa una prueba del nuevo mapa. |

La compilación y las pruebas unitarias de la versión anterior constan en el historial del proyecto. Los casos visuales pendientes no se marcan como aprobados hasta probar OpenFreeMap en el emulador y guardar capturas nuevas en `docs/evidencias/`.
