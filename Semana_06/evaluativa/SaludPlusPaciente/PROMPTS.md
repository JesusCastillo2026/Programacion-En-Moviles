# Registro de instrucciones y prompts — mejora-ia

Este registro corresponde a la implementación de la rama `mejora-ia`. Las solicitudes se transcriben en extractos fieles cuando se indica expresamente; las instrucciones técnicas derivadas de la guía se resumen para mostrar qué se pidió, qué se produjo y qué se verificó.

## 1. Completar la app paciente según la guía

**Prompt del encargo:** «Vamos a crear un nuevo trabajo de Química Plus, pero en esta ocasión vamos a hacerlo desde cero»; el resto del encargo aclara que se refiere a Clínica SaludPlus, pide cumplir la estructura, las pantallas, los cuatro retos y la metodología del PDF, y crear las versiones en `main` y `mejora-ia`.

**Respuesta resumida:** Se reconstruyó una aplicación de paciente con registro e inicio de sesión local, catálogo de especialidades y médicos, selección de fecha y hora, confirmación de cita, navegación inferior y pantallas adicionales de citas, perfil, resultados, notificaciones, términos y detalle.

**Correcciones y decisiones aplicadas:** Se respetó el nombre Clínica SaludPlus (no “Química Plus”), se trabajó dentro de `Semana_06/evaluativa`, sin reutilizar Semana 05 ni cambiar el laboratorio. Como no estaba el ZIP del docente, los paquetes y pantallas se reconstruyeron a partir de la descripción del PDF. La información queda en memoria y se reinicia al cerrar la app.

## 2. Mejora solicitada para `mejora-ia`

**Prompt del encargo:** «En mejora con IA vas a añadir imágenes de lo que son el perfil de los profesores, en el cual como no tenemos imágenes vas a crear imágenes con inteligencia artificial». También se pidió que la versión mejorada incluya mejoras funcionales y documentación comprensible dentro del código.

**Respuesta resumida:** Se generaron recursos gráficos locales para la portada de bienvenida y cuatro retratos ilustrativos. Se integró un calendario de cinco días hábiles que avanza por semanas, excluye fechas pasadas y fines de semana, muestra el mes y reinicia la hora elegida cuando se cambia la fecha.

**Correcciones y decisiones aplicadas:** Los retratos son ficticios y se identifican como ilustrativos, no como fotos de personas reales. La cuadrícula de retratos es de 1254 × 1254 píxeles y se divide en cuatro recortes de 627 × 627. El calendario desactiva la navegación hacia semanas anteriores a la actual, conserva el filtrado de horarios reservados y permite avanzar a semanas futuras.

## 3. Registro y verificación

**Prompt del encargo:** «documenta cada prompt usando un archivo prompts.md, con el prompt, la respuesta resumida y que tuviste que corregir».

**Respuesta resumida:** Este archivo reúne las instrucciones que guiaron esta versión, un resumen de la solución y las decisiones de corrección relevantes.

**Correcciones aplicadas:** Se evita presentar los retratos generados como fotografías auténticas. Se añadieron pruebas unitarias para la regla de cinco días hábiles y compilación de Kotlin. La generación final del APK y la ejecución completa de pruebas no pudieron verificarse en este equipo porque AAPT2 se cae con una excepción nativa al procesar recursos de AndroidX.

## 4. Conservar el registro al consultar términos

**Prompt del encargo (extracto):** «cuando entramos a términos y condiciones [...] se borra todo el formulario [...] quiero que corrijas eso».

**Respuesta resumida:** Se cambió el estado del formulario a `rememberSaveable` para restaurar sus datos al volver desde Términos. La mejora visual posterior conserva esta solución y la extiende a Login.

**Qué se corrigió:** La explicación inicial atribuía el reinicio solo a abrir una nueva ruta. En realidad, `remember` no garantiza conservar los campos cuando el destino sale de la composición. Se utiliza el estado guardable asociado a la entrada de navegación.

## 5. Validaciones y diseño integrado de ConIA

**Prompt del encargo (extractos):** «quiero que apliques todos estos cambios que hemos visto en correo, teléfono, médico, especialidad y cita»; «quiero un formato más moderno y que la imagen esté bien integrada con lo que es el registro y login y no solo sea un cuadro de la imagen».

**Respuesta resumida:** Se añadieron reglas compartidas para correo y celular peruano, validación defensiva de reservas y controles de errores. La portada existente se integra a todo el ancho con un degradado hacia el formulario. Registro, Login y bienvenida comparten componentes; se renovaron tipografía, colores, botones, encabezados y navegación inferior. La confirmación permite desplazarse con el teclado visible.

**Qué se corrigió:** No se restringe el correo a Gmail porque también debe aceptar direcciones institucionales. Se usa normalización con `Locale.ROOT`. El repositorio vuelve a comprobar especialidad, médico, fecha y duplicados mediante `any` antes de `add`. Al compilar se ajustó la alineación de imagen a `BiasAlignment`. Las pruebas anteriores con fechas fijas se cambiaron por días futuros para que sigan siendo válidas.

**Verificación:** Se ejecutaron las pruebas unitarias con procesamiento normal de recursos. En esta revisión AAPT2 sí completó ese procesamiento. La comprobación visual en un dispositivo sigue pendiente; no se sustituye por la compilación. El usuario realizará la integración, el commit y el push.

## 6. Mejoras de experiencia, navegación y reserva

**Prompt del encargo (extracto):** «Perfecto, entonces aplica todo eso es lo que me acabas de mencionar. y lo mismo aplica este un commit, pero en este caso que diga este mejoras con ChatGPT Astra».

**Contexto de la petición:** Las seis mejoras propuestas fueron: próxima cita en Inicio, acceso directo a médicos desde especialidades destacadas, progreso y conservación de selecciones en la reserva, errores con acciones de recuperación, navegación sin pantallas repetidas y adaptación a pantallas pequeñas, texto grande, teclado y listas vacías.

**Respuesta resumida:** Se añadió un borrador guardable del flujo y errores diferenciados de reserva, se conectó Inicio con especialidades y detalle de próxima cita y se ajustaron las pilas de navegación de pestañas y autenticación. Las pantallas ahora ofrecen progreso, búsqueda conservada, acciones en estados vacíos y contenedores adaptables.

**Qué se corrigió:** La barra inferior apuntaba a Splash aunque ya no estaba en el historial; ahora usa Inicio. Solo se restaura una pila de pestaña al cambiar entre pestañas, para evitar restaurar una confirmación terminada desde Cita exitosa. Los destinos guardados y el borrador se limpian al salir de la cuenta. El repositorio conserva una única validación para crear citas y explicar rechazos.

**Verificación:** APK generado con `assembleDebug` y 16 pruebas aprobadas con `testDebugUnitTest`. Queda pendiente la prueba manual de navegación, teclado y apariencia en emulador. El mensaje de commit fue elegido por el usuario.

## 7. Turnos vencidos y regreso a Inicio

**Prompt del encargo (extracto):** «cuando se crea esa cita ya no puedo retornar al punto de inicio»; «este horario ya pasó»; «aparte de las dos [...] quiero ver si encuentras tú otras más con esa misma lógica».

**Respuesta resumida:** Se simplificó el cambio entre pestañas para volver a Inicio desde Mis citas después de confirmar. Los turnos de hoy se comparan con la hora real de Lima y se ocultan al vencer. La confirmación repite esa validación y explica el rechazo.

**Qué se corrigió:** El emulador estaba configurado en GMT y la clínica opera en hora de Lima. Se centralizó la zona `America/Lima` para calendario, disponibilidad y próxima cita. También se restringió el detalle y la cancelación a la cuenta actual, se impidió cancelar turnos ya comenzados y se diferenciaron las fechas transcurridas de las atenciones completadas.

**Verificación:** APK generado, 18 pruebas unitarias aprobadas y recorrido en el emulador desde la selección de horario hasta Mis citas y el regreso a Inicio. En el emulador se comprobó que a las 11:10 de Lima se ofrecía 11:30, pero no 11:00.

## 8. Registro con Login y reserva por sede

**Prompt del encargo (extracto):** «al momento de registrarme no debe mandarme [...] home [...] que me pida logearme»; «en el inicio [...] SEDES»; «un apartado que diga doctores [...] por categoria [...] con sus respectivas fotos»; «elegir un local, para poder pedir una cita».

**Respuesta resumida:** Crear cuenta ya no inicia sesión automáticamente; después del registro se abre Login con aviso de éxito. Inicio presenta las sedes y un acceso a Doctores. El directorio filtra por especialidad y nombre. La reserva comienza por Sedes y conserva el local en el borrador y la cita.

**Qué se corrigió:** Cada sede ofrece solo las especialidades y médicos asignados allí. El repositorio rechaza reservas sin sede o con un médico que no atiende en ella. La sede se muestra en la confirmación, comprobante, lista y detalle de citas. Se mantuvo la barra inferior de cuatro destinos; Mis citas sigue allí.

**Prompts visuales (herramienta integrada de generación de imágenes):** Se solicitaron tres retratos fotográficos cuadrados de profesionales ficticios peruanos para el directorio: una cardióloga de unos 40 años con cabello oscuro rizado y uniforme verde azulado; un traumatólogo de unos 45 años con cabello corto y barba; una oftalmóloga de unos 50 años con cabello recogido y blusa lavanda. Los tres prompts pidieron cabeza y hombros, bata blanca, consultorio desenfocado, luz natural, una sola persona y ausencia de texto o logotipos. Se guardaron como `doctor_m5.png`, `doctor_m6.png` y `doctor_m7.png` junto a los cuatro retratos preexistentes.
