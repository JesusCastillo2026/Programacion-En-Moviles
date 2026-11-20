# Pruebas de aceptación

La guía del laboratorio define doce recorridos manuales en el emulador. Para la verificación más reciente se hizo una compilación limpia de `mejora-ia` y se ejecutaron las cinco pruebas unitarias. El emulador Pixel 8 no llegó a iniciar: el equipo informó que la aceleración de hardware necesaria para la imagen del sistema no estaba disponible. Por eso, los casos visuales se mantienen con el resultado de su última evidencia y no se marcan como aprobados para la versión actual del mapa.

Las capturas `01`, `02`, `04`, `06`, `07`, `08` y `10` corresponden a una ejecución previa a la migración de Google Maps a MapLibre/OpenFreeMap. Sirven para explicar esos estados y controles, pero no verifican la carga del mapa actual. Los archivos `09_seleccion_mas_cercano.png` y `11_rotacion.png` no se usan como evidencias: muestran otra aplicación, no CampusGPS.

| N.º | Caso | Resultado esperado | Evidencia / resultado más reciente |
|---:|---|---|---|
| 1 | Abrir la app y solicitar ubicación | Android muestra el diálogo de permiso | Observado antes de la migración; repetir en la versión actual |
| 2 | Aceptar el permiso | Se obtiene la posición y aparece el marcador del usuario | No verificado visualmente después de la migración |
| 3 | Denegar el permiso | Mensaje claro; la app sigue abierta y permite ir a Ajustes | Captura previa disponible; volver a comprobar el flujo en la versión actual |
| 4 | Usar el punto base del campus | Se muestran los cinco puntos de interés sobre el mapa | Los marcadores están implementados; no hay captura actual que confirme el mapa cargado |
| 5 | Tocar un marcador | Se muestran el nombre y la categoría | Título y categoría están definidos en el código; falta verificación en pantalla actual |
| 6 | Revisar la lista de lugares | La lista queda ordenada por distancia y muestra m o km | Distancias y orden aparecen en captura previa; repetir con el nuevo proveedor |
| 7 | Seleccionar una tarjeta | Se resalta y la cámara centra el mapa en el punto | Acción implementada; sin verificación visual actual |
| 8 | Ir al lugar más cercano | El botón centra el punto verde; antes de calcular queda deshabilitado | Regla y marcador están implementados; falta verificación en emulador |
| 9 | Cambiar la ubicación y buscar otra vez | Cambian las distancias, el orden y el punto más cercano | Se observó un cambio en evidencia previa; repetir con la versión actual |
| 10 | Apagar la ubicación del dispositivo | Mensaje de GPS desactivado y acción para reintentar | Captura previa disponible; repetir en la versión actual |
| 11 | Rotar la pantalla | Se conservan la posición y los datos de la lista | No hay captura válida de CampusGPS para esta prueba; pendiente de ejecutar |
| 12 | Revisar la configuración y el repositorio | No se publica una clave secreta; la versión mejorada no requiere clave de Maps | La configuración actual de mejora-ia usa MapLibre/OpenFreeMap; `local.properties` no se incluye en Git |

## Verificación automatizada y compilación

| Verificación | Resultado |
|---|---|
| `clean testDebugUnitTest assembleDebug` | Correcto, compilación limpia de la rama `mejora-ia` |
| Pruebas unitarias | 5 aprobadas: filtro completo, filtro por categoría, punto dentro del radio, punto fuera y distancia aún no calculada |
| Emulador Pixel 8 | No inició; el entorno indicó que la aceleración de hardware requerida no está disponible |
| Pruebas manuales del mapa actual | No se consideran aprobadas hasta repetirlas en Android Studio con un emulador o dispositivo funcional |

## Qué falta para cerrar la verificación visual

1. Iniciar un Pixel 8 con aceleración de virtualización habilitada o conectar un teléfono Android.
2. Instalar la compilación de `mejora-ia`, conceder ubicación y confirmar que el estilo de OpenFreeMap carga.
3. Repetir los doce recorridos de la tabla y guardar capturas nuevas, en especial el mapa con los cinco marcadores, el marcador del usuario, la selección del punto más cercano y la rotación.
4. Confirmar con el estudiante las ubicaciones interiores de los cinco sitios. Las coordenadas actuales son datos de demostración derivados del centro del campus, no posiciones verificadas de cada edificio.
