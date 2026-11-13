# Pruebas de aceptación - Cine Castillo

**Fecha:** 11/11/2026 · **Versión:** 1.0.2 · **Dispositivo:** Pixel 8, Android API 37.2 con Google Play.

Resultado: **14 casos aprobados**, con capturas reales. Compilación `:app:assembleDebug` y **8 pruebas unitarias sin fallos** mediante `:app:testDebugUnitTest`.

| Caso | Estado | Comprobación | Evidencia |
|---|---|---|---|
| PA01 | Aprobado | Cuenta usuario accede al catálogo y no tiene opción Admin. | [Captura](docs/evidencias/PA01.png) |
| PA02 | Aprobado | La cuenta admin tiene acceso al catálogo y al panel administrativo. | [Captura](docs/evidencias/PA02.png) |
| PA03 | Aprobado | Contraseña incorrecta rechazada con mensaje claro. | [Captura](docs/evidencias/PA03.png) |
| PA04 | Aprobado | Campos vacíos rechazados antes de acceder a Firebase. | [Captura](docs/evidencias/PA04.png) |
| PA05 | Aprobado | Primera sesión con Room vacío: carga, catálogo de 20 películas y notificación. Al pulsar el aviso se abre la app. | [Captura](docs/evidencias/PA05.png) |
| PA06 | Aprobado | Detalle visible y llamada real al segundo endpoint registrada sin credenciales en la traza. | [Captura](docs/evidencias/PA06.png) |
| PA07 | Aprobado | La película favorita sigue guardada después de detener y reabrir la app. | [Captura](docs/evidencias/PA07.png) |
| PA08 | Aprobado | En modo avión se ven las películas de Room y el aviso sin conexión. | [Captura](docs/evidencias/PA08.png) |
| PA09 | Aprobado | Sin datos ni sesión (datos de app limpiados), en modo avión: error con Reintentar. Al activar Internet vuelve al login. También se verificó error y recuperación del catálogo con Room vacío. | [Captura](docs/evidencias/PA09.png) |
| PA10 | Aprobado | El favorito permanece después de sincronizar el catálogo. | [Captura](docs/evidencias/PA10.png) |
| PA11 | Aprobado | Vaciar deja 0 películas y 0 favoritas; reintentar con red recupera 20 películas y el panel muestra los nuevos totales. | [Captura](docs/evidencias/PA11_recuperado.png) |
| PA12 | Aprobado | Al cerrar sesión y reabrir se exige autenticación. | [Captura](docs/evidencias/PA12.png) |
| PA13 | Aprobado | Filtro y contenido conservados al girar. La tarjeta es accesible mediante scroll. Traza HTTP confirma cero descargas nuevas por rotación. | [Captura](docs/evidencias/PA13_horizontal.png) |
| PA14 | Aprobado | Permiso denegado en el diálogo: la sincronización y el catálogo funcionan, sin aviso de Cine Castillo en el panel de notificaciones. | [Captura](docs/evidencias/PA14.png) |

## Condiciones de las pruebas

- PA-05 se ejecutó con los datos de la aplicación limpiados: [carga inicial](docs/evidencias/CargaInicial4.png), lista y aviso; se tocó el aviso y se abrió la app.
- PA-09 incluye dos escenarios: caché Room vacía con sesión válida y [arranque sin datos ni sesión](docs/evidencias/PA09_instalacion.png), equivalente al estado de una instalación nueva. Sin red aparece Reintentar; al recuperarla se [regresa al login](docs/evidencias/PA09_recuperacion.png). No se omite la autenticación.
- PA-11 comprobó [cero películas y favoritas](docs/evidencias/PA11_vacio.png), lista vacía y recuperación de datos y totales.
- PA-13 conservó el filtro Coco y la tarjeta en horizontal. [Traza](docs/rotacion_verificada.txt) confirma que girar no aumentó las solicitudes al catálogo.
- PA-14 se denegó el permiso en el diálogo del sistema. El catálogo se actualizó y [no apareció el aviso](docs/evidencias/PA14_sin_aviso.png).
- La traza de [endpoints](docs/endpoints_verificados.txt) guarda solo rutas, nunca parámetros ni claves.

## Pruebas unitarias

`MovieListViewModelTest` (7): carga correcta, fallo sin caché, fallo con caché, búsqueda por título, orden y favoritas, observación sin descarga adicional, notificación solo tras éxito.

`AuthViewModelTest` (1): recuperar red y reintentar vuelve al login sin duplicar el observador de sesión.

Las pruebas del dispositivo y las unitarias se complementan. Las primeras verifican Firebase/TMDB/Room/Android reales; las segundas aíslan decisiones del ViewModel con repositorios falsos. Los totales y títulos de películas corresponden a la fecha de ejecución.
