# Pruebas de aceptación

Se recorrieron manualmente en el emulador Pixel 8 los doce casos de aceptación de CampusGPS, en la rama `mejora-ia`. La ubicación del dispositivo se fijó desde los controles del emulador para repetir las distancias; por eso esta evidencia valida el flujo de la app, pero no acredita una lectura GPS física. Las capturas nombradas abajo están guardadas en `docs/evidencias/`.

| N.º | Caso | Resultado observado | Evidencia | Estado |
|---:|---|---|---|---|
| 1 | Abrir la app y solicitar ubicación | Android mostró el diálogo del permiso de ubicación al tocar «Mi ubicación». | `22_permiso_dialogo.png` | Aprobado |
| 2 | Aceptar el permiso | La app continuó abierta, indicó que la ubicación estaba lista y calculó distancias desde la posición elegida en el emulador. | `23_permiso_aceptado.png` | Aprobado |
| 3 | Denegar el permiso | La app siguió abierta, explicó que no podía mostrar la ubicación y ofreció abrir Ajustes. | `24_permiso_denegado.png` | Aprobado |
| 4 | Mostrar los puntos de interés | Se vieron los siete lugares del campus sobre el mapa. | `25_siete_puntos.png` | Aprobado |
| 5 | Tocar un marcador | Al seleccionar Cafetería, la ficha cambió al lugar elegido e incluyó categoría y galería de imágenes. | `29_marcador_seleccionado.png` | Aprobado |
| 6 | Revisar distancias | Desde la coordenada de prueba del campus, la lista quedó ordenada de menor a mayor: Salones de cómputo, Cafetería, Cancha de grass, Auditorio, Biblioteca, Comedor y Estacionamiento. Las distancias se mostraron en metros. | `33_lista_distancias_ubicacion1.png` | Aprobado |
| 7 | Seleccionar un lugar desde su tarjeta | Al tocar la tarjeta de Salones de cómputo, cambió el destino y se recalculó el recorrido; la interfaz mostró 71 m en línea recta y el detalle de la ruta. | `36_tarjeta_seleccionada.png` | Aprobado |
| 8 | Buscar el punto más cercano | El control permaneció deshabilitado sin una ubicación calculada. Después de obtenerla, se habilitó y seleccionó Biblioteca al colocar la posición de prueba en ese punto (0 m). | `31_mas_cercano_sin_ubicacion.png`, `32_mas_cercano_con_ubicacion.png`, `35_punto_mas_cercano_biblioteca.png` | Aprobado |
| 9 | Cambiar la ubicación | Al mover la posición del emulador desde el centro del campus hasta Biblioteca, se actualizaron las distancias; después, la búsqueda de cercanía seleccionó Biblioteca. | `34_ubicacion_cambiada_biblioteca.png`, `35_punto_mas_cercano_biblioteca.png` | Aprobado |
| 10 | Desactivar el GPS | La app informó «Activa el GPS e intenta de nuevo» y ofreció reintentar; no se cerró. | `27_gps_desactivado_actual.png` | Aprobado |
| 11 | Rotar la pantalla | La actividad siguió abierta en horizontal y conservó el destino, la distancia y las instrucciones de ruta. Al terminar se restauró el emulador a vertical. | `37_rotacion_horizontal_actual.png` | Aprobado |
| 12 | Revisar configuración y secreto de Maps | La rama utiliza MapLibre/OpenFreeMap. No se encontró clave de Google Maps en el código del laboratorio y `local.properties` está excluido por `.gitignore`. | Revisión del código y de los archivos seguidos por Git | Aprobado |

## Comprobación del panel del mapa

También se probó el control del panel inferior: tocar su asa y deslizarlo lo contrae y expande, dejando más área de mapa visible. Evidencias: `20_panel_contraido.png` y `21_panel_expandido.png`.

## Observaciones sobre ubicación y rutas

Las pruebas de ubicación se hicieron con coordenadas elegidas en el emulador. En un teléfono, la posición dependerá del permiso, el GPS y la lectura real del dispositivo. La ruta peatonal se presenta como estimada; cuando no se obtiene una geometría externa válida, la app usa un recorrido interno aproximado. Las distancias directas y el orden de cercanía sí se recalculan con la posición disponible.

El mapa usa estilos y teselas públicas de OpenFreeMap/OpenStreetMap a través de MapLibre, por lo que necesita conexión a Internet para descargarlos. No requiere una clave de Google Maps.

## Verificación técnica

| Verificación | Resultado |
|---|---|
| Ensamblado debug | `:app:assembleDebug` finalizó correctamente. |
| Pruebas unitarias | `:app:testDebugUnitTest` finalizó correctamente. |
| Pruebas manuales | 12 de 12 casos aprobados en el emulador Pixel 8. |
| Panel contraíble | Expansión y contracción por toque y gesto comprobadas. |

## Evidencias anteriores

Las capturas `01` a `10` documentan pantallas de una iteración anterior. `09_seleccion_mas_cercano.png` y `11_rotacion.png` no son evidencias válidas de esta revisión; para rotación se debe usar `37_rotacion_horizontal_actual.png`. Las capturas de la versión vigente empiezan en `13_version_actual_mapa.png` y las pruebas manuales recientes están identificadas con los números `19` a `37`.
