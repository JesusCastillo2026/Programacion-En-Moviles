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

## 6. Rediseño cinematográfico

- **Fecha:** 11/11/2026.
- **Prompt:** mejorar toda la app con acabado profesional, tipografía consistente, catálogo y detalle más cuidados, estados claros y rojo como color principal, sin alterar los requisitos del Laboratorio 12.
- **Respuesta resumida:** tema oscuro cinematográfico, escala tipográfica fija, portada local, película destacada y tarjetas renovadas.
- **Corrección aplicada:** mantener los controles de búsqueda, sincronización, favoritos y administración y conservar los estados de carga, error y falta de conexión.
- **Aprendizaje:** un cambio de tema solo funciona cuando las pantallas comparten jerarquía, espaciado y colores.

## 7. Flujo de reserva de entradas

- **Fecha:** 13/11/2026.
- **Herramienta:** ChatGPT mediante Codex.
- **Prompt:** convertir Cine Castillo en una experiencia de cine inspirada en las aplicaciones de cadenas comerciales, añadiendo cartelera, selección de sede, formatos 2D/3D/IMAX/4DX, horarios, asientos y confirmación únicamente en `mejora-ia`, sin alterar `main`.
- **Qué respondió (resumen):** propuso un flujo de reserva por etapas integrado a la navegación existente, con sedes, horarios y asientos de muestra.
- **Qué usamos / qué cambiamos y por qué:** se añadió la pestaña Entradas y el flujo de selección y resumen. Se presenta como simulación local para no afirmar que hay inventario o pagos conectados a una boletería real.
- **Qué aprendimos:** una reserva real requiere sincronizar funciones y disponibilidad con un proveedor; una confirmación local sirve como prototipo, pero no aparta butacas en un cine.

**Rediseño de Cartelera y animaciones**

- **Fecha:** 13/11/2026.
- **Herramienta:** ChatGPT mediante Codex.
- **Prompt:** aplicar identidad cinematográfica de rojo oscuro y un carrusel Compose con `HorizontalPager`, escala por posición y navegación al detalle.
- **Qué respondió (resumen):** propuso conservar la tipografía y arquitectura del proyecto, y animar escala y opacidad de los pósteres según su distancia al centro.
- **Qué usamos / qué cambiamos y por qué:** se ajustó el color principal del tema y se añadió el carrusel a Cartelera. Tocar un póster abre el detalle TMDB existente.
- **Qué aprendimos:** Pager permite conservar una lista desplazable con selección centrada; escala y opacidad dan jerarquía visual sin sumar una biblioteca de animación.

**Logo y pantalla de acceso**

- **Fecha:** 13/11/2026.
- **Herramienta:** ChatGPT mediante Codex.
- **Prompt:** integrar el logo original de Cine Castillo en el inicio de sesión y modernizar el diseño, centrando los elementos y manteniendo la identidad roja oscura.
- **Qué respondió (resumen):** propuso una composición adaptable con el logo, una bienvenida breve, campos de acceso y botón principal dentro de una tarjeta.
- **Qué usamos / qué cambiamos y por qué:** se añadió el recurso gráfico local y se reorganizó LoginScreen para centrarlo, conservar los errores y el indicador de carga, y adaptarse al teclado.
- **Qué aprendimos:** mantener el formulario en una columna desplazable permite conservar el diseño en pantallas pequeñas y cuando aparece el teclado.

**Registro, perfil y experiencia de reserva**

- **Fecha:** 13/11/2026.
- **Herramienta:** ChatGPT mediante Codex.
- **Prompt:** añadir registro conectado a Firebase con validación visible de contraseña y retorno al login; un carrusel automático de tres películas; cambiar el orden de compra para comenzar por la sede, limitar a cinco asientos y adaptar formatos y horarios; reemplazar Créditos por Perfil y agregar un menú lateral con información y formatos ilustrados.
- **Qué respondió (resumen):** diseñar las ampliaciones dentro de `mejora-ia`, usar Firebase Authentication y Firestore para cuentas de usuario, y mantener como simulación local las funciones de cine que no cuentan con un backend de boletería.
- **Qué usamos / qué cambiamos y por qué:** se añadió el alta de cuenta con rol fijo `usuario`, campos de contacto, controles de contraseña y perfil en Firestore; se creó el carrusel; la reserva ahora recorre sede, película, cantidad y selección de asientos, formato compatible, fecha y horario y confirmación; se añadieron Perfil, panel de formatos con ilustraciones originales y menú lateral. La afiliación, las sedes, los horarios y las reservas permanecen como demostración.
- **Qué aprendimos:** las reglas de Firestore también deben permitir de forma limitada la creación del perfil propio; el rol de administrador nunca debe provenir del formulario. Las franjas horarias y butacas de una aplicación de producción necesitarían un servicio central para evitar reservas duplicadas.

**Acceso directo desde Entradas y Cartelera**

- **Fecha:** 13/11/2026.
- **Prompt:** al tocar una película dentro de Entradas, pasar directamente a elegir cantidad de entradas y asientos; hacer que Cartelera tenga un propósito claro y no repita pasos de reserva ya realizados.
- **Respuesta resumida:** separar la exploración de horarios del flujo guiado de compra y trasladar la sede, la fecha y la película seleccionadas al formulario de reserva.
- **Qué corregimos:** elegir una película en Entradas ahora abre directamente el paso de asientos. Cartelera permite filtrar por fecha y sede; tocar una película abre la reserva con esos valores ya seleccionados. Las fechas se calculan desde el día actual.
- **Qué aprendimos:** dos accesos pueden compartir el proceso de reserva si cada uno conserva el contexto que la persona ya eligió y evita volver a preguntarlo.

**Navegación de regreso y acceso al inicio**

- **Fecha:** 13/11/2026.
- **Prompt:** añadir controles para retroceder o volver al inicio en las pantallas de la app y un logo pequeño en la parte superior.
- **Respuesta resumida:** conservar Salir como cierre de sesión y añadir controles de navegación dentro de la barra superior.
- **Qué cambiamos:** las pantallas secundarias ahora muestran una flecha para volver y un botón Inicio que regresa al catálogo; el catálogo conserva el menú lateral. Se incorporó el logo pequeño junto al nombre Cine Castillo.
- **Qué aprendimos:** volver a una pantalla y cerrar sesión son acciones distintas, así que sus controles deben estar separados y etiquetados claramente.

**Funciones coherentes, reservas e historial de perfil**

- **Fecha:** 13/11/2026.
- **Prompt:** unificar los horarios ofrecidos en Cartelera y en Reserva; conservar la sede, película, fecha y función elegidas; asociar butacas ocupadas a una función; guardar reservas en Firestore y mostrarlas en el perfil; permitir editar teléfono y DNI con validación y confirmación; y aclarar estados de carga, error y catálogo sin conexión.
- **Respuesta resumida:** usar una sola regla de horarios, guardar reservas bajo el UID autenticado, limitar las escrituras del perfil a los campos de contacto y validar de nuevo con reglas de Firestore.
- **Qué corregimos:** Cartelera y Reserva comparten los horarios del día; la función elegida se transfiere al flujo de compra. Se eligen fecha y hora antes de las butacas para que los asientos simulados dependan de esa función. La confirmación se guarda en `usuarios/{uid}/reservas`; Perfil muestra entradas guardadas y permite editar teléfono y DNI con validación local, confirmación y actualización limitada en Firestore. Cartelera y Reserva tienen estados explícitos para carga, falta de conexión, ausencia de películas o funciones y reintento.
- **Qué falta para el entorno Firebase:** publicar `firestore.rules` actualizado en la consola del proyecto para habilitar la edición y lectura/escritura de reservas con las nuevas reglas. La app no realiza pagos ni bloquea butacas en una boletería real.
- **Verificación:** `:app:assembleDebug` finalizó correctamente el 13/11/2026. No se ejecutaron las pruebas unitarias ni una prueba visual en emulador en esta revisión.
- **Qué aprendimos:** pasar toda la información de la función seleccionada evita que Cartelera y Reserva muestren opciones distintas; la persistencia del ticket requiere reglas por usuario además de guardar el documento desde el repositorio.

