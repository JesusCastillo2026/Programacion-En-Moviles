# Evidencias y comprobaciones · MuroCampus

Las capturas se obtienen de la aplicación ejecutándose en el emulador. El PDF solicita estas cinco vistas:

| Archivo | Qué muestra |
|---|---|
| `docs/evidencias/01_lista.png` | Publicaciones cargadas y etiqueta Usuario N |
| `docs/evidencias/02_detalle_comentarios.png` | Publicación y comentarios del elemento seleccionado |
| `docs/evidencias/03_filtro_usuario.png` | Usuario 2 seleccionado y sus publicaciones |
| `docs/evidencias/04_crear_publicacion.png` | Formulario y respuesta `Publicado con id 101` |
| `docs/evidencias/05_error_conexion.png` | Error de conexión y botón Reintentar |

## Casos de comprobación del PDF

| Caso | Resultado esperado | Estado |
|---|---|---|
| Sin Internet | Mensaje de conexión y botón Reintentar | Aprobado: se desactivaron Wi‑Fi y datos en el emulador |
| Reintentar | Al recuperar red, Reintentar carga el muro | Aprobado: volvió a mostrar las publicaciones |
| Publicación inexistente | Error HTTP 404 en el detalle | Pendiente de prueba manual en la app |
| JSON distinto | Ignora campos adicionales; ausencia de campo obligatorio muestra error | Revisado en la configuración del conversor; queda pendiente alterar una respuesta del servicio |
| Cancelación | Salir del detalle durante la carga no muestra error de cancelación | Revisado en el código; pendiente de prueba durante una solicitud lenta |
| Lista y detalle | Cada publicación abierta muestra comentarios | Aprobado: el post 1 cargó cinco comentarios |
| Filtro | Cada usuario muestra diez publicaciones y Todos devuelve cien | Aprobado: Usuario 2 quedó filtrado y la lista general cargó cien |
| Crear | El servicio devuelve `Publicado con id 101` | Aprobado: petición POST real y respuesta 101 en Logcat |
| Capas | El ViewModel no usa Retrofit, PostApi ni HttpException | Revisado en el código |

No marco como aprobadas las pruebas que no se hayan ejecutado realmente en el emulador.
