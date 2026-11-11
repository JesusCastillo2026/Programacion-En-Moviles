# Registro de indicaciones - Cine Castillo

**Herramienta:** ChatGPT mediante Codex. **Registro consolidado:** 11/11/2026.

Se documentan indicaciones reales del estudiante, resumidas para evitar repetir mensajes extensos. Cuando no se dispone de la fecha exacta de una consulta anterior, se indica expresamente. No se inventan conversaciones ni se alteran fechas de commits. No se incluyen contraseñas o claves.

## 1. Tema, modalidad y servicio remoto

- **Fecha de consulta:** anterior a la consolidación; fecha exacta no registrada.
- **Prompt:** desarrollar el laboratorio con temática de películas, de forma individual y con un proyecto nuevo de Firebase. El docente indicó no utilizar las API básicas del documento.
- **Respuesta resumida:** construir Cine Castillo con TMDB para catálogo, detalle e imágenes; Firebase Authentication para acceso y Firestore para roles.
- **Corrección aplicada:** registrar el paquete Android exacto y utilizar el archivo JSON del nuevo proyecto; el de Semana 11 no corresponde.
- **Aprendizaje:** el identificador del paquete y el proyecto Firebase deben coincidir con la aplicación configurada.

## 2. Repositorio y publicación

- **Fecha de consulta:** anterior a la consolidación; fecha exacta no registrada.
- **Prompt:** completar el trabajo, subir los avances al repositorio del curso y publicar el APK.
- **Respuesta resumida:** mantener el proyecto en Semana_12/CineCastillo, conservar las fases main y mejora-ia y publicar versiones descargables.
- **Corrección aplicada:** excluir de Git la configuración local y la clave de TMDB; conservar los releases anteriores. Se informó que la clave integrada puede extraerse del APK y el estudiante autorizó publicarlo.
- **Aprendizaje:** excluir una clave del código versionado no la convierte en un secreto de servidor cuando está incluida en una app distribuida.

## 3. Pruebas reales y evidencia

- **Fecha de consulta:** anterior o igual al 11/11/2026; se consolida en esta fecha.
- **Prompt:** ejecutar los 14 casos de aceptación en el emulador y documentarlos con capturas; explicar mejor los bloques importantes de código en el PDF.
- **Respuesta resumida:** ejecutar acceso por roles, validaciones, sincronización, detalle, favoritas, modo avión, limpieza de caché, cierre de sesión, rotación y permisos.
- **Corrección aplicada:** diferenciar evidencia visual de verificación interna: se guardó una traza de rutas HTTP para demostrar el segundo endpoint y la ausencia de descarga adicional al girar.
- **Aprendizaje:** una captura de pantalla no demuestra por sí sola que se llamó a un endpoint; necesita complementarse con resultados observables y trazas.

## 4. Completar la entrega sin pruebas parciales

- **Fecha de consulta:** retomada el 11/11/2026.
- **Prompt:** completar las faltas de la entrega y corregir los casos parciales antes de entregar el PDF con evidencias y conclusiones.
- **Respuesta resumida:** revisar la guía y corregir la implementación antes de cambiar el resultado documentado.
- **Corrección aplicada:** separar navegación, autenticación e implementaciones de repositorios; añadir UiState común y ViewModel propios de Favoritos y Admin; mantener las factories con AppContainer y la observación ligada al ciclo de vida.
- **Aprendizaje:** marcar un requisito como terminado requiere comprobar tanto su comportamiento como la responsabilidad de cada capa.

## 5. Continuación y correcciones verificadas

- **Fecha:** 11/11/2026.
- **Prompt:** «puedes continuar en donde te quedaste».
- **Respuesta resumida:** retomar las pruebas del emulador y completar las comprobaciones restantes del informe.
- **Corrección aplicada:** colocar encabezado y tarjetas en una sola lista desplazable para la orientación horizontal; conservar el contenedor en Application; añadir error con Reintentar al abrir sin sesión ni red; cancelar la observación anterior antes de reintentar. Se verificó el flujo sin caché, su recuperación y los contadores del administrador después de sincronizar.
- **Aprendizaje:** el estado de una instalación nueva sin conexión necesita un tratamiento distinto al de un catálogo ya descargado. El reintento debe recuperar el acceso sin duplicar listeners ni saltarse la autenticación.

## Resultado de esta revisión

La compilación y las ocho pruebas unitarias finalizaron correctamente. Los catorce casos de aceptación se ejecutaron en el emulador y están documentados en [PRUEBAS_ACEPTACION.md](PRUEBAS_ACEPTACION.md). El PDF explica los bloques importantes y reúne las capturas reales. Las mejoras de búsqueda y orden actúan sobre el catálogo local; no se describen como consultas remotas que no se utilizan.
