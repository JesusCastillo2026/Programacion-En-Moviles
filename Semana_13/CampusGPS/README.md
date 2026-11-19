# CampusGPS

Aplicación Android para consultar la ubicación del dispositivo en un mapa y ordenar cinco puntos ficticios del campus por cercanía. Está desarrollada con Kotlin, Jetpack Compose y Material 3.

## Preparar el proyecto

1. Abre esta carpeta en Android Studio y sincroniza Gradle.
2. No necesitas crear una clave de Google Maps ni editar `local.properties` para mostrar el mapa.
3. El mapa usa MapLibre Native para Android y estilos de OpenFreeMap basados en datos de OpenStreetMap. La carga del estilo y los mosaicos necesita conexión a Internet.
4. Para obtener la ubicación del dispositivo, usa un emulador con Google Play Services y establece una ubicación de prueba, por ejemplo `-12.0464, -77.0428`.

OpenFreeMap ofrece el servicio público “tal cual”; puede cambiar o dejar de estar disponible. Para distribuir la aplicación a gran escala, conviene evaluar un proveedor con condiciones de servicio y disponibilidad adecuadas.

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

La pantalla representa el estado observable. El ViewModel usa interfaces de repositorio y calcula las distancias; no recibe `Context`. El repositorio encapsula Google Play Services para obtener el GPS. MapLibre Native dibuja el mapa y carga los estilos públicos de OpenFreeMap. La ubicación y el proveedor visual del mapa son piezas independientes.

## Puntos de prueba

| ID | Lugar | Categoría | Latitud | Longitud |
|---:|---|---|---:|---:|
| 1 | Biblioteca Central | Estudio | -12.0460 | -77.0435 |
| 2 | Cafetería Campus | Comida | -12.0470 | -77.0420 |
| 3 | Laboratorio de Cómputo | Estudio | -12.0455 | -77.0415 |
| 4 | Auditorio Principal | Eventos | -12.0480 | -77.0440 |
| 5 | Estacionamiento Norte | Servicios | -12.0440 | -77.0430 |

## Evidencias visuales

Las capturas reales están en `docs/evidencias/`. Las imágenes de mapa que se tomaron antes de cambiar el proveedor se conservan como historial y todavía no demuestran que OpenFreeMap cargue correctamente. Debe guardarse una captura nueva después de validar la pantalla en el emulador.

## Reflexión

1. `FusedLocationProviderClient` vive en el repositorio porque es una integración de plataforma; así el ViewModel depende de una interfaz y se puede sustituir la fuente.
2. El permiso de ubicación autoriza el acceso a la posición del dispositivo. MapLibre y OpenFreeMap se encargan de representar el mapa y requieren conexión a Internet, pero no una clave de Google Maps.
3. La aplicación no almacena claves del proveedor del mapa. La disponibilidad del servicio público no está garantizada.
4. La distancia se calcula en el ViewModel mediante una función utilitaria para que la pantalla no contenga lógica de negocio.
5. Si el permiso se deniega, se informa al usuario y se ofrece abrir los ajustes de la aplicación para habilitarlo.

## Pruebas

El resultado de los doce casos está en [PRUEBAS_ACEPTACION.md](PRUEBAS_ACEPTACION.md). Los casos que necesitan ver el mapa, sus marcadores o la cámara se deben volver a ejecutar con la implementación nueva antes de marcarlos como aprobados.

## Mejoras de la rama `mejora-ia`

La segunda versión conserva la base del laboratorio y añade seis opciones: filtro por categoría, círculo y marca de radio de 100/200/500 metros, punto azul con control para volver a la ubicación, seguimiento que se puede iniciar y detener, recuerdo del último punto seleccionado mediante DataStore y estilo oscuro del mapa. La vista usa estilos claro y oscuro de OpenFreeMap a través de MapLibre Native.

Las preferencias y la ubicación se consultan desde repositorios; el ViewModel publica el estado y la pantalla representa controles y resultados. La selección de categoría y la regla de proximidad tienen pruebas unitarias en `app/src/test/`.

El registro de prompts de la fase de mejora está en [PROMPTS.md](PROMPTS.md).
