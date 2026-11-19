# Semana 13 - Localización geográfica

Proyecto **CampusGPS**, desarrollado con Kotlin y Jetpack Compose para consultar la ubicación actual y encontrar puntos de interés del campus. `main` conserva Google Maps; `mejora-ia` usa MapLibre Native con estilos públicos de OpenFreeMap y no requiere clave de Google Maps.

## Versiones del laboratorio

- `main`: implementación base guiada del laboratorio.
- `mejora-ia`: versión ampliada con filtros, proximidad, seguimiento y preferencias.

Abre el proyecto [CampusGPS](CampusGPS/) en Android Studio. Para ejecutar `mejora-ia`, no hace falta configurar una clave de mapas; sí se necesita Internet para cargar OpenFreeMap y Google Play Services para obtener la ubicación. Los pasos, la arquitectura y las diferencias entre versiones están en el [README del proyecto](CampusGPS/README.md).
