# CampusGPS

Aplicación Android para consultar la ubicación del usuario en un mapa y ordenar cinco puntos ficticios del campus por cercanía. Está desarrollada con Kotlin, Jetpack Compose y Material 3.

## Preparar el proyecto

1. Abre esta carpeta en Android Studio y sincroniza Gradle.
2. En Google Cloud habilita Maps SDK for Android y crea una clave restringida al paquete `com.tecsup.campusgps` y a la huella SHA-1 del equipo.
3. Guarda la clave solo en el archivo local `local.properties`, con el formato `MAPS_API_KEY=tu_clave`. Ese archivo está excluido de Git.
4. Usa un emulador que incluya Google Play y establece la ubicación de prueba en `-12.0464, -77.0428`.

## Estructura

```text
app/src/main/java/com/tecsup/campusgps/
├── data/        Interfaces y acceso a ubicación y lugares
├── model/       Posición, lugar y estados de pantalla
├── ui/          Pantalla, mapa, lista y mensajes
├── util/        Distancia y formato de unidades
└── viewmodel/   Estado y coordinación de acciones
```

## Flujo de datos

```text
PantallaMapa -> MapaViewModel -> UbicacionRepository -> FusedLocationProviderClient
                           -> LugaresRepository -> cinco puntos locales
PantallaMapa <- StateFlow <- MapaViewModel <- distancias ordenadas
```

La pantalla solo representa el estado observable. El ViewModel usa interfaces de repositorio y calcula las distancias; no recibe `Context`. La implementación de ubicación encapsula Google Play Services.

## Puntos de prueba

| ID | Lugar | Categoría | Latitud | Longitud |
|---:|---|---|---:|---:|
| 1 | Biblioteca Central | Estudio | -12.0460 | -77.0435 |
| 2 | Cafetería Campus | Comida | -12.0470 | -77.0420 |
| 3 | Laboratorio de Cómputo | Estudio | -12.0455 | -77.0415 |
| 4 | Auditorio Principal | Eventos | -12.0480 | -77.0440 |
| 5 | Estacionamiento Norte | Servicios | -12.0440 | -77.0430 |

## Evidencias visuales

Las capturas reales disponibles están en `docs/evidencias/`. Como todavía falta una clave de Maps, las imágenes identifican esa limitación y no simulan el mapa con marcadores:

1. Solicitud de permiso (`01_permiso.png`).
2. Ubicación y lista calculada; mosaicos pendientes de la clave (`02_ubicacion_mapa_sin_clave.png`).
3. Filtro por categoría (`06_filtro_comida.png`).
4. Radio de proximidad (`07_radio_100m.png`).
5. Denegación de permiso (`04_error_permiso.png`); también se incluye GPS desactivado (`10_gps_desactivado.png`).

## Reflexión

1. `FusedLocationProviderClient` vive en el repositorio porque es una integración de plataforma; así el ViewModel depende de una interfaz y se puede sustituir la fuente.
2. El permiso autoriza a la app a leer la posición del dispositivo. La clave identifica y autoriza el uso de Maps SDK. Si falta el permiso, no se obtiene ubicación; si falla la clave, el mapa no carga sus mosaicos.
3. `local.properties` evita subir la clave con el código. Si se expusiera, debe revocarse y crearse otra en Google Cloud.
4. La distancia se calcula en el ViewModel mediante una función utilitaria para que la pantalla no contenga lógica de negocio.
5. Si el permiso se deniega, se informa al usuario y se ofrece abrir los ajustes de la app para habilitarlo.

## Pruebas

El estado individual de los doce casos está en [PRUEBAS_ACEPTACION.md](PRUEBAS_ACEPTACION.md). No se presentan como aprobados los pasos que dependen de una clave de Maps o de una comprobación que todavía no se ejecutó.

## Mejoras de la rama `mejora-ia`

La segunda versión conserva la base del laboratorio y añade seis opciones: filtro por categoría, círculo y marca de radio de 100/200/500 metros, punto azul nativo con control para volver a la ubicación, seguimiento que se puede iniciar y detener, recuerdo del último punto seleccionado mediante DataStore y estilo oscuro del mapa.

Las mejoras se mantienen dentro de las capas previstas: las preferencias y la ubicación se consultan desde repositorios; el ViewModel publica el estado; la pantalla solo presenta controles y resultados. La selección de categoría y la regla de proximidad tienen pruebas unitarias en `app/src/test/`.

El registro de prompts de la fase de mejora está en [PROMPTS.md](PROMPTS.md). Incluye el resumen de lo propuesto, la adaptación realizada y el criterio aplicado.

