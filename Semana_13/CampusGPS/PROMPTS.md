# Registro de uso de IA - mejoras de CampusGPS

Este registro resume la asistencia empleada para diseñar y revisar las ampliaciones de la rama `mejora-ia`. Las decisiones finales y las verificaciones se documentan junto a cada cambio.

| Prompt de trabajo | Propuesta recibida | Adaptación incorporada | Criterio aplicado |
|---|---|---|---|
| Añadir filtros por las categorías de los cinco puntos sin mover la lógica a Compose. | Publicar la categoría elegida y filtrar los lugares visibles. | El filtro se implementó en el ViewModel y la pantalla solo muestra chips. | Conserva la separación View/ViewModel y permite probar la regla aparte. |
| Añadir un selector de proximidad de 100, 200 o 500 metros. | Dibujar un círculo y señalar en la lista los lugares que quedan dentro. | El círculo usa la posición actual; la lista muestra dentro/fuera sin ocultar los demás puntos. | Mantiene el contexto de todos los lugares y hace visible el radio elegido. |
| Activar el punto azul y el botón nativo de mi ubicación solo con permiso. | Configurar las propiedades del mapa según el permiso concedido. | La pantalla consulta el permiso y solo entonces habilita la capa de ubicación y su control. | Evita pedirle al mapa una ubicación protegida cuando no hay autorización. |
| Incorporar seguimiento en vivo que pueda iniciarse y detenerse. | Convertir las actualizaciones de ubicación a un Flow en el repositorio. | Se usa `callbackFlow`; al detenerse o cerrarse el flujo se elimina el callback de Google Play Services. | Evita actualizaciones huérfanas y mantiene la API de plataforma fuera del ViewModel. |
| Recordar el último punto seleccionado. | Persistir el identificador localmente y recuperarlo al abrir. | Se guardó el ID con DataStore detrás de una interfaz de repositorio. | Evita guardar objetos de UI y deja al ViewModel independiente de Android Context. |
| Añadir un modo oscuro al mapa. | Aplicar un estilo JSON mediante Maps SDK. | Se incluyó un estilo oscuro local y un interruptor para activarlo. | No agrega servicios adicionales y mantiene el estilo disponible sin cambiar el tema del teléfono. |

La versión generada por la herramienta se revisó contra los contratos del laboratorio; se ajustaron los puntos de entrada de permisos, el manejo del ciclo de vida de la ubicación y la representación de estado para conservar la arquitectura indicada.
