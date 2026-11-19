# Pruebas de aceptación

Registrar el resultado solo después de ejecutar cada caso en el emulador. La prueba del mapa requiere Maps SDK for Android configurado y una clave válida guardada localmente.

| # | Caso | Resultado esperado | Estado |
|---:|---|---|---|
| 1 | Abrir y pulsar Obtener mi ubicación | Android muestra la solicitud de permiso | Pendiente |
| 2 | Aceptar permiso | Se muestra la posición y el marcador “Estás aquí” | Pendiente |
| 3 | Denegar permiso | Aparece un error con “Abrir ajustes”; la app sigue abierta | Pendiente |
| 4 | Situar el emulador en el punto base | Se ven los cinco marcadores cerca de la posición | Pendiente |
| 5 | Tocar un marcador del mapa | Se muestra nombre y categoría | Pendiente |
| 6 | Revisar la lista | Los cinco lugares aparecen de menor a mayor distancia, en m o km | Pendiente |
| 7 | Seleccionar una tarjeta | La tarjeta queda resaltada y el mapa se mueve al punto | Pendiente |
| 8 | Pulsar “Ir al más cercano” | El mapa enfoca el marcador verde; el botón antes de buscar está deshabilitado | Pendiente |
| 9 | Cambiar la ubicación y volver a buscar | Distancias, orden y marcador verde se actualizan | Pendiente |
| 10 | Apagar ubicación del emulador | Se muestra el mensaje de GPS con “Reintentar” | Pendiente |
| 11 | Rotar la pantalla | La posición y la lista permanecen | Pendiente |
| 12 | Revisar la seguridad del repositorio | No hay clave en Git y `local.properties` está excluido | Pendiente |

## Registro de ejecución

| Fecha | Dispositivo | Compilación | Casos aprobados | Observaciones |
|---|---|---|---:|---|
| Pendiente de configurar Maps y ejecutar | Pixel 8 API 37.2 previsto | Pendiente | 0 de 12 verificados | La clave API todavía no está configurada. |
