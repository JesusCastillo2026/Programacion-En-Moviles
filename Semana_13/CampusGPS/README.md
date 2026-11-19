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

La guía solicita cinco capturas hechas en el emulador. Se añadirán a `docs/evidencias/` al ejecutar el proyecto con una clave válida de Maps:

1. Solicitud de permiso.
2. Mapa con la ubicación actual.
3. Los cinco marcadores y sus etiquetas.
4. Lista ordenada por distancia.
5. Mensaje de error con la acción correspondiente.

## Reflexión

1. `FusedLocationProviderClient` vive en el repositorio porque es una integración de plataforma; así el ViewModel depende de una interfaz y se puede sustituir la fuente.
2. El permiso autoriza a la app a leer la posición del dispositivo. La clave identifica y autoriza el uso de Maps SDK. Si falta el permiso, no se obtiene ubicación; si falla la clave, el mapa no carga sus mosaicos.
3. `local.properties` evita subir la clave con el código. Si se expusiera, debe revocarse y crearse otra en Google Cloud.
4. La distancia se calcula en el ViewModel mediante una función utilitaria para que la pantalla no contenga lógica de negocio.
5. Si el permiso se deniega, se informa al usuario y se ofrece abrir los ajustes de la app para habilitarlo.

## Pruebas

El estado de ejecución de los doce casos está en [PRUEBAS_ACEPTACION.md](PRUEBAS_ACEPTACION.md). No se consideran aprobados hasta verificarlos en el emulador.
