# Evidencias y comprobaciones · MuroCampus

Las capturas se obtienen de la aplicación ejecutándose en el emulador. El PDF solicita estas cinco vistas:

| Archivo | Qué muestra |
|---|---|
| `docs/evidencias/01_lista.png` | Publicaciones cargadas y etiqueta Usuario N |
| `docs/evidencias/02_detalle_comentarios.png` | Publicación y comentarios del elemento seleccionado |
| `docs/evidencias/03_filtro_usuario.png` | Usuario 2 seleccionado y sus publicaciones |
| `docs/evidencias/04_crear_publicacion.png` | Formulario y respuesta `Publicado con id 101` |
| `docs/evidencias/05_error_conexion.png` | Error de conexión y botón Reintentar |
| `docs/evidencias/06_mejora_ia_busqueda.png` | Búsqueda local por el texto «qui» en la rama mejora-ia |

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

## Ajuste visual pendiente de captura

Se aplicó una etiqueta de color estable por userId en las tarjetas de ambas ramas. La compilación de main y mejora-ia finalizó correctamente. No se pudo instalar el APK ni guardar la captura posterior porque ADB no detectó conectado el emulador Pixel 8. Las capturas existentes muestran la ejecución anterior a este ajuste.

El informe de explicación está en [docs/Informe_Laboratorio_09_MuroCampus.pdf](docs/Informe_Laboratorio_09_MuroCampus.pdf).
