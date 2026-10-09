# Clínica SaludPlus — App Paciente

Aplicación Android con Jetpack Compose para consultar especialidades y médicos, registrar pacientes y reservar citas.

## Estado del proyecto

La carpeta contiene una **versión base asistida** que se ampliará en la rama `mejora-ia`. Esta entrega no se presenta como una fase desarrollada sin asistencia. El laboratorio de TECSUP Store permanece aparte, en `Semana_06/R1-SinIA`.

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

La versión base muestra cinco fechas consecutivas y recalcula los turnos cuando se elige una fecha. La rama `mejora-ia` agrega calendario semanal con días hábiles, imágenes locales de médicos ficticios, mejoras visuales y `PROMPTS.md`.

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

