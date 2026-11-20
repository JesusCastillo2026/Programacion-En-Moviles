# Pruebas de aceptación

La guía del laboratorio define doce recorridos manuales en el emulador. Para esta revisión inicié la versión actual de `mejora-ia` en Pixel 8, comprobé que cargara el mapa, concedí ubicación y tomé capturas de los filtros, el radio, el estilo claro y el seguimiento. También se ejecutaron las cinco pruebas unitarias. La tabla identifica lo comprobado y lo que falta recorrer; las capturas actuales están en `docs/evidencias/13` a `18`.

Las capturas `01` a `10` corresponden a la interfaz anterior a la migración de Google Maps a MapLibre/OpenFreeMap. Los archivos `09_seleccion_mas_cercano.png` y `11_rotacion.png` muestran otra aplicación y no se consideran evidencia de CampusGPS.

| N.º | Caso | Resultado esperado | Evidencia / resultado más reciente |
|---:|---|---|---|
| 1 | Abrir la app y solicitar ubicación | Android muestra el diálogo de permiso | Observado antes de la migración; repetir en la versión actual |
| 2 | Aceptar el permiso | Se obtiene la posición y aparece el marcador del usuario | Comprobado en Pixel 8; captura 17 muestra posición y distancia |
| 3 | Denegar el permiso | Mensaje claro; la app sigue abierta y permite ir a Ajustes | Captura previa disponible; volver a comprobar el flujo en la versión actual |
| 4 | Usar el punto base del campus | Se muestran los siete puntos de interés sobre el mapa | Los siete marcadores tienen coordenadas extraídas de los enlaces de Maps; falta comprobar en pantalla el encuadre completo |
| 5 | Tocar un marcador | Se muestran el nombre, la categoría y la ficha de fotos | Verificado en Pixel 8: tocar Cancha de grass abre su foto correcta y actualiza la ficha |
| 6 | Revisar la lista de lugares | La lista queda ordenada por distancia y muestra m o km | Regla existente; falta repetir el orden con las siete ubicaciones actuales |
| 7 | Seleccionar una tarjeta | Se resalta, abre su galería y centra el mapa en el punto | Acción y ficha implementadas; sin verificación visual posterior al cambio |
| 8 | Ir al lugar más cercano | El botón centra el punto verde; antes de calcular queda deshabilitado | Regla y marcador están implementados; falta verificación en emulador |
| 9 | Cambiar la ubicación y buscar otra vez | Cambian las distancias, el orden y el punto más cercano | Se obtuvo una posición; falta repetir con una segunda ubicación controlada |
| 10 | Apagar la ubicación del dispositivo | Mensaje de GPS desactivado y acción para reintentar | Captura previa disponible; repetir en la versión actual |
| 11 | Rotar la pantalla | Se conservan la posición y los datos de la lista | No hay captura válida de CampusGPS para esta prueba; pendiente de ejecutar |
| 12 | Revisar la configuración y el repositorio | No se publica una clave secreta; la versión mejorada no requiere clave de Maps | La configuración actual de mejora-ia usa MapLibre/OpenFreeMap; `local.properties` no se incluye en Git |

## Comprobaciones de las mejoras recientes

| Función | Comportamiento esperado | Estado |
|---|---|---|
| Buscar lugares | El texto filtra las tarjetas por nombre o categoría y permite limpiar la consulta | Implementado; falta recorrido manual en emulador |
| Resaltar destino | El marcador seleccionado se muestra rojo y los demás quedan atenuados | Implementado; falta recorrido manual en emulador |
| Ruta peatonal | El mapa solo muestra una ruta cuyo origen y recorrido están dentro del campus; incluye pasos, distancia y tiempo estimado | Validación de perímetro y trazado interno implementados; falta validación manual en el emulador |

La ruta intenta usar `valhalla1.openstreetmap.de`, lo que requiere Internet y transmite las coordenadas de origen y destino. La app descarta las geometrías que cruzan el límite del campus y utiliza un trazado interno estimado como alternativa. Si el usuario está fuera del campus, informa que debe ingresar antes de calcularla. El trazado de respaldo es aproximado y queda pendiente contrastarlo con los senderos reales en el emulador/campus.

## Verificación automatizada y compilación

| Verificación | Resultado |
|---|---|
| `clean testDebugUnitTest assembleDebug` | Correcto, compilación limpia de la rama `mejora-ia` |
| Pruebas unitarias | 5 aprobadas: filtro completo, filtro por categoría, punto dentro del radio, punto fuera y distancia aún no calculada |
| Emulador Pixel 8 | Versión actual abierta; mapa cargado y capturas nuevas guardadas |
| Pruebas visuales del mapa actual | Comprobados mapa, ubicación, filtro, radio, tema claro y seguimiento activo; quedan casos manuales pendientes |

## Qué falta para cerrar la verificación visual

1. Repetir los casos de permiso denegado, GPS apagado y rotación en la versión actual.
2. Comprobar que los cinco marcadores aparezcan, seleccionar cada uno y revisar el destino más cercano.
3. Durante esta revisión, al detener el seguimiento apareció `StandaloneCoroutine was cancelled`; verificar y corregir esa salida antes de marcar como probado el apagado del seguimiento.
4. Repetir el recorrido de fotos en las ubicaciones restantes y revisar la ficha en distintos tamaños de pantalla.

## Mejora adicional: fotos por ubicación

Se agregaron dos fotos locales para cada uno de los siete sitios y dos para el marcador principal de TECSUP. Al tocar un pin aparece la galería con nombre, contador, flechas y desplazamiento horizontal. En Pixel 8 se comprobó que tocar un marcador selecciona el sitio correcto, que la flecha cambia a la segunda foto y que el gesto lateral también avanza. `assembleDebug` terminó correctamente con esta función.
