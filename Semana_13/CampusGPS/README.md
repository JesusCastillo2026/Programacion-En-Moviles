# CampusGPS

CampusGPS es una aplicación Android que muestra siete puntos de interés del campus TECSUP Lima y permite comparar su distancia con la ubicación del dispositivo. La desarrollé para el Laboratorio 13 de Programación en Móviles, usando Kotlin, Jetpack Compose y una organización MVVM con Repository.

La rama `main` contiene la versión base del laboratorio. En `mejora-ia` agregué filtros por categoría, radios de cercanía, seguimiento de ubicación, recuerdo del último punto y un estilo oscuro del mapa. Por indicación del docente, la rama mejorada muestra el mapa con MapLibre y estilos públicos de OpenFreeMap; la ubicación sigue obteniéndose con Google Play Services.

## Abrir y ejecutar

Abre esta carpeta en Android Studio, sincroniza Gradle y ejecuta la configuración `app`. Para la rama `mejora-ia`, el mapa no necesita una clave de Google Maps, pero sí Internet para descargar el estilo y los mosaicos. Para obtener la posición se necesita permiso de ubicación y un dispositivo con Google Play Services.

La cámara inicia en TECSUP Sede Lima, Av. Cascanueces 2221, Santa Anita, aproximadamente en `-12.04398, -76.95291`. En el emulador puedes establecer esa ubicación desde Extended Controls → Location. La ubicación del usuario es real solo cuando el proveedor del dispositivo entrega una posición; el pin del campus es una referencia fija.

## Qué lugares aparecen

Los siete lugares ahora usan las coordenadas de los enlaces de Google Maps que compartió el estudiante. En la app los pines usan el color rojo de CampusGPS; los colores de la imagen satelital solo identificaban cada sitio.

El zoom al seleccionar un lugar queda en 16 para conservar contexto y mantener los destinos cercanos del campus a la vista.

| ID | Punto | Categoría | Estado de ubicación |
|---:|---|---|---|
| 1 | Biblioteca | Estudio | -12.045007, -76.952958 |
| 2 | Cafetería | Comida | -12.044004, -76.952229 |
| 3 | Comedor | Comida | -12.045004, -76.952453 |
| 4 | Auditorio | Eventos | -12.044828, -76.952715 |
| 5 | Estacionamiento | Servicios | -12.044541, -76.951658 |
| 6 | Salones de cómputo | Estudio | -12.044376, -76.952827 |
| 7 | Cancha de grass | Deporte | -12.043732, -76.953629 |

## Ficha fotográfica de cada punto

Al tocar uno de los siete marcadores del mapa se selecciona el sitio y aparece una galería flotante dentro del panel inferior. Cada ficha muestra las dos fotografías de la carpeta del lugar; se puede pasar de una a otra deslizando o con las flechas. También se muestra el nombre del sitio y el número de foto. La misma interacción funciona desde las tarjetas del panel. El marcador de TECSUP abre su propia galería con las imágenes de `Tecsup - Sede Lima`.

Las imágenes se guardan como recursos locales en `app/src/main/res/drawable-nodpi`, así que las galerías no dependen de una descarga de fotos ni de una API externa.

## Estructura de carpetas

```text
CampusGPS/
├── app/src/main/java/com/tecsup/campusgps/
│   ├── data/          Repositorios de ubicación, lugares y preferencias
│   ├── model/         Posición, lugar y estados de pantalla
│   ├── ui/            Pantalla, mapa y componentes visuales
│   ├── util/          Distancia, formato y reglas de filtro/proximidad
│   ├── viewmodel/     Estado y coordinación de acciones
│   └── MainActivity.kt Punto de entrada e inicialización del mapa
├── app/src/test/      Pruebas unitarias de las reglas de filtro y radio
└── docs/
    ├── evidencias/    Capturas registradas durante el desarrollo
    ├── Informe_Laboratorio_13_CampusGPS.pdf
    ├── PRUEBAS_ACEPTACION.md
    └── crear_informe.py
```

## Cómo se conectan las capas

```text
PantallaMapa (Compose)
        ↓ eventos y StateFlow
MapaViewModel
        ↓ interfaces
UbicacionRepositoryGoogle ── FusedLocationProviderClient
LugaresRepositoryLocal ───── siete puntos de Google Maps
PreferenciasLugarRepositoryDataStore ─ último punto seleccionado
        ↓
MapaLibre Native ─────────── estilos públicos de OpenFreeMap
```

La pantalla se ocupa de mostrar controles y estado. El ViewModel coordina la búsqueda, las distancias, los filtros y el seguimiento. Los repositorios esconden los detalles de Google Play Services, los datos locales y DataStore. El mapa es otra pieza: recibe el estado y dibuja los marcadores, la línea directa y el radio, sin consultar el GPS por su cuenta.

## Capturas del laboratorio

Las imágenes siguientes son capturas nuevas hechas desde el emulador Pixel 8 con la rama `mejora-ia`. Muestran la interfaz actual, MapLibre con OpenFreeMap y varios estados interactivos. Las imágenes anteriores se mantienen en la carpeta como historial, pero no se usan aquí como evidencia de la versión moderna.

### Mapa y panel principal

![Versión actual de CampusGPS con el mapa oscuro cargado y el destino seleccionado](docs/evidencias/13_version_actual_mapa.png)

### Filtro por categoría

![Filtro Comida activo en CampusGPS](docs/evidencias/14_filtro_comida_actual.png)

### Proximidad de 100 metros

![Radio de proximidad de 100 metros seleccionado](docs/evidencias/15_radio_100m_actual.png)

### Alternancia del estilo del mapa

![Mapa claro con la interfaz oscura de CampusGPS](docs/evidencias/16_mapa_claro_actual.png)

### Ubicación recibida

![Ubicación actual del emulador y distancia de 142 metros a la cafetería](docs/evidencias/17_mi_ubicacion_actual.png)

### Seguimiento activo

![Seguimiento de ubicación activo en la interfaz actual](docs/evidencias/18_seguimiento_actual.png)

## Pruebas y entrega

La compilación limpia `clean testDebugUnitTest assembleDebug` terminó correctamente y las cinco pruebas unitarias pasaron. También pude ejecutar la versión actual en el emulador y tomar las capturas de esta sección. No todos los doce casos manuales están aprobados; sus resultados y pendientes están detallados en [PRUEBAS_ACEPTACION.md](PRUEBAS_ACEPTACION.md). Durante la revisión, detener el seguimiento mostró un mensaje de cancelación y quedó anotado para corregirlo.

La versión base cuenta con diez commits de laboratorio en `main`. La versión ampliada está en `mejora-ia`, con su registro de decisiones en [PROMPTS.md](PROMPTS.md). La guía completa, con explicación de archivos y bloques importantes, se encuentra en el [informe PDF](docs/Informe_Laboratorio_13_CampusGPS.pdf).

## Reflexión

1. **¿Por qué FusedLocationProviderClient está dentro del Repository?** Porque depende de servicios de Android y permisos de plataforma. Al dejarlo detrás de una interfaz, el ViewModel puede pedir una posición sin conocer el `Context` ni la clase concreta del proveedor.
2. **¿Qué diferencia hay entre el permiso de ubicación y el proveedor del mapa?** El permiso autoriza a la app a leer la ubicación del dispositivo. MapLibre y OpenFreeMap dibujan el mapa y descargan sus mosaicos por Internet. En esta rama no se usa una clave de Google Maps.
3. **¿Por qué no se sube una clave en el código y qué haría si se filtrara?** Una clave publicada puede usarse fuera de la app y consumir el servicio asociado. La revocaría en el proveedor, generaría otra y revisaría el historial y las restricciones. En `mejora-ia` el mapa actual no utiliza esa clave.
4. **¿Dónde calculo la distancia y por qué no en un Composable?** La distancia se calcula en la lógica coordinada por el ViewModel mediante una función utilitaria. Así Compose se limita a presentar el valor, y la regla se puede probar sin dibujar la pantalla.
5. **¿Qué ocurre si se niega el permiso dos veces?** Android puede dejar de mostrar el diálogo. La app presenta el estado de permiso denegado y ofrece abrir los ajustes de la aplicación para que la persona decida si quiere habilitarlo.
