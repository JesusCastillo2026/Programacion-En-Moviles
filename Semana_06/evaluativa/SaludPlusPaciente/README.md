# Clínica SaludPlus — App Paciente

Aplicación Android con Jetpack Compose para consultar especialidades y médicos, registrar pacientes y reservar citas.

## Estado del proyecto

La carpeta contiene una **versión base asistida**. La rama `mejora-ia` conserva esta aplicación y añade el calendario semanal, imágenes locales ilustrativas, documentación en el código y el registro de prompts en `PROMPTS.md`. El laboratorio de TECSUP Store permanece aparte, en `Semana_06/R1-SinIA`.

## Alcance

- Usuarios, médicos, especialidades y citas se manejan con colecciones en memoria.
- No se conecta a un servidor ni usa Room, SQLite o Firebase.
- Los datos de prueba y las cuentas nuevas se pierden al cerrar o reiniciar la aplicación.
- La app es una demostración académica del flujo de atención de pacientes; no brinda diagnóstico ni consejo médico.

## Abrir y ejecutar

1. Abre esta carpeta en Android Studio con **File → Open**.
2. Espera a que termine la sincronización de Gradle.
3. Selecciona la configuración `app` y un emulador Android.
4. Ejecuta con **Run**.

Cuenta de demostración: `demo@saludplus.pe` / `123456`.

## Pantallas y flujo

La app incluye Splash, Registro, Inicio, Especialidades, Médicos, Fecha y hora, Confirmar cita, Login, Cita exitosa, Mis citas y Perfil. Los cuatro retos extra son Detalle/cancelación, Resultados, Notificaciones y Términos. La navegación de reserva pasa `especialidadId`, `medicoId`, `fecha` y `hora`; al confirmar, elimina el flujo de reserva del historial para que Atrás vuelva a Inicio.

La versión base muestra cinco fechas consecutivas y recalcula los turnos cuando se elige una fecha. En `mejora-ia`, el calendario muestra cinco días hábiles, permite moverse entre semanas sin retroceder antes de la semana actual y reinicia la hora al cambiar de fecha. Los retratos y la portada son recursos locales ilustrativos; no representan profesionales reales.

## Actualización ConIA: registro, reservas y diseño

- El formulario conserva sus datos al consultar Términos y regresar. Login también conserva sus campos.
- Se valida el formato del correo y se normalizan espacios exteriores y mayúsculas. Se admiten dominios institucionales y proveedores habituales; no se verifica la existencia del buzón.
- El celular peruano debe tener nueve dígitos y empezar por 9, sin prefijo +51.
- Las mismas reglas se aplican en el repositorio, además del formulario.
- Al reservar se comprueba la relación médico/especialidad, sesión, fecha laborable desde hoy, horario del catálogo y ausencia de una reserva previa mediante `any`, antes de `add`.
- Bienvenida, registro y login comparten una ilustración a todo el ancho, integrada con un degradado, campos con iconos, visibilidad de contraseña y errores junto a los campos.
- Tema, tipografía, botones, barra inferior y encabezados comparten el estilo renovado. La confirmación admite desplazamiento y textos largos.

La validación de lógica se ejecuta con `gradlew.bat :app:testDebugUnitTest`. No requiere iniciar el emulador. La revisión visual final del nuevo diseño queda pendiente en el dispositivo.

## Mejoras de experiencia de uso

- Inicio muestra la próxima cita futura del paciente, con médico, especialidad, fecha, hora y acceso al detalle.
- Las especialidades destacadas abren directamente sus médicos.
- El agendamiento muestra cuatro pasos. El borrador conserva especialidad, médico, semana, día, hora y motivo al retroceder; cambiar de especialidad o médico invalida las selecciones dependientes. Al completar la reserva, comenzar otra o salir de la cuenta, se limpia.
- Cada rechazo de reserva devuelve una causa concreta y una acción: iniciar sesión, elegir médico/especialidad o cambiar de horario.
- La barra inferior vuelve a Inicio, que permanece en el historial. Login y Registro intercambian sus destinos con estado guardado, sin acumular copias. Al cerrar sesión se eliminan los destinos guardados.
- El calendario utiliza una cuadrícula adaptable y días desplazables horizontalmente. Catálogos, perfil, detalle, confirmación y listas admiten desplazamiento; se reorganizaron textos que podían competir por espacio. Los avisos anuncian cambios al lector de pantalla y las listas vacías ofrecen acciones.

Verificado: APK debug generado y 18 pruebas unitarias aprobadas. Incluyen restauración del borrador, próxima cita, turnos vencidos, pertenencia de citas y motivos de rechazo. Se recorrió en el emulador el registro de una cita, Mis citas y el regreso a Inicio.

## Correcciones de navegación y hora local

- Después de confirmar una cita, la barra inferior vuelve a Inicio sin recuperar una pantalla guardada del flujo anterior.
- La clínica usa `America/Lima` para ofrecer fechas y turnos, aunque el emulador tenga otra zona horaria. En la pantalla de selección se ocultan los turnos cuya hora ya comenzó; el repositorio vuelve a comprobarlo al confirmar.
- La disponibilidad y la próxima cita se refrescan mientras las pantallas están abiertas. Si un turno vence entre la selección y la confirmación, se muestra una causa concreta y se ofrece elegir otro.
- Una cuenta solo puede abrir y cancelar sus propias citas. Las citas que ya comenzaron no pueden cancelarse y se muestran como «Fecha transcurrida», sin considerarlas automáticamente atendidas.

Comprobación manual pendiente: volver desde Términos; alternar varias veces Login/Registro; reservar retrocediendo entre pasos; alternar las cuatro pestañas y pulsar Atrás; revisar 320 dp de ancho, texto ampliado y teclado abierto.

## Preguntas de reflexión

1. **¿Por qué el esqueleto separa modelos, rutas y navegación de las pantallas?** Los modelos describen la información y las rutas conectan destinos; son la base compartida. Las pantallas concentran la práctica visual y funcional que la guía pide completar. Como no se entregó el ZIP, se recreó la estructura indicada en el PDF.
2. **¿Por qué `Repositorio` es un `object`?** Mantiene una sola colección compartida durante la sesión. Si cada pantalla creara su propia lista, la cita reservada en una vista no aparecería en las demás y podrían repetirse horarios.
3. **¿Cómo se actualizan búsqueda y horarios?** El repositorio expone colecciones observables de Compose; los campos guardan su consulta en estado y filtran los datos al recomponerse la pantalla. La lista de horarios consulta las citas existentes para el médico y fecha elegidos.
4. **¿Qué diferencia hay entre `navigate()` y `popUpTo` al confirmar?** La navegación normal conserva el destino anterior para volver atrás. Al confirmar, `popUpTo(Inicio)` retira las pantallas del proceso de reserva para que Atrás no regrese a una cita recién completada.
5. **¿Qué se corrige en el calendario de `mejora-ia`?** La base usa días consecutivos. La mejora calcula fechas con `LocalDate`, excluye días pasados y fines de semana, impide retroceder antes de la semana actual, actualiza el mes mostrado y reinicia la hora al cambiar de día. Los turnos ya ocupados continúan filtrándose por médico y fecha.
6. **¿Cuándo usar NavigationDrawer o NavigationBar?** La barra inferior sirve para cuatro secciones principales que se usan con frecuencia; un drawer conviene cuando hay muchos destinos secundarios o el espacio inferior es limitado.

## Observaciones

1. El ZIP/esqueleto del docente no estaba disponible, por lo que los paquetes y las pantallas se construyeron desde la estructura descrita en la guía, sin reutilizar la app de Semana 05.
2. Registro y citas funcionan solo dentro de la sesión. Al cerrar la app, los datos desaparecen; para probar otra vez se puede usar la cuenta demo.

## Conclusiones

1. La separación entre modelos, repositorio, navegación y vistas hace más sencillo seguir el flujo y compartir datos sin añadir una base de datos que la guía no solicita.
2. Esta carpeta corresponde a una versión base asistida, no a una entrega elaborada sin asistencia. `mejora-ia` conserva la base, documenta sus prompts y añade el calendario dinámico y los recursos visuales.

