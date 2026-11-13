# Prompts utilizados

## Prompt 1

**Texto recibido del usuario:** «ahora desarrolla la parte de mejorado con IA implementado todo lo que dice le pDF».

**Contexto aplicado:** Laboratorio GLAB-S08-JLEONS-2026_D.pdf, páginas 28–29; proyecto HuellitasVet de `Semana_08/SinIA`; rama `mejora-ia`.

**Qué generó la IA:** Una copia independiente del proyecto en `ConIA` y tres mejoras completas: línea de peso por atención, reprogramación de cita y calendario mensual en la agenda. Se añadieron consultas Room, métodos en la interfaz y la implementación del repositorio, estados en ViewModels y pantallas Compose. También se actualizaron la guía de uso y este registro.

**Qué tuve que corregir al revisar:** La reprogramación no debe presentar la hora original como una nueva opción válida, aunque sea la propia cita. Se bloqueó esa selección en el ViewModel y se conserva una segunda validación en la transacción del repositorio. La versión `ConIA` recibió un `applicationId` diferente para no sustituir la instalación de `SinIA`. Se verificó la compilación; la interacción en emulador queda pendiente de prueba en Android Studio.

**¿Respeta MVVM?:** Sí. Las vistas solo conocen sus ViewModels, los ViewModels solo conocen `VetRepository` (interfaz), y Room queda detrás de `VetRepositoryRoom`. No se añadieron entidades ni se modificó el esquema de la base, así que no corresponde aumentar su versión.

## Prompt 2 — Mejora profesional del área de cliente

**Texto solicitado por el usuario (redacción profesional):** «Toma como referencia la imagen del área de cliente de HuellitasVet y adapta sus pantallas para que mantengan una presentación coherente y cuidada. Revisa Inicio, Mascotas, Ficha de mascota, Pedir cita, Mis citas y Perfil; conserva las funciones existentes y aplica el mismo estilo visual entre ellas. Organiza la información para que sea clara en el móvil, evita repetir encabezados, muestra correctamente los datos de las mascotas y sus citas, y asegura que el botón Cerrar sesión limpie la navegación y regrese al inicio de sesión sin permitir volver a pantallas privadas con Atrás».

**Respuesta resumida:** Se reorganizaron las pantallas del cliente con componentes, tamaños de texto, tarjetas, chips y navegación coherentes con la referencia. El perfil reúne identidad, resumen de mascotas y citas, preferencias y cierre de sesión. La salida de sesión limpia la pila privada y conduce al login.

**Qué se corrigió al integrar:** Se quitaron títulos duplicados en pantallas que ya usan la barra superior, se ajustó la ficha para separar historial y datos, y se evitó mostrar una indicación de vacunación que no estaba respaldada por datos clínicos. Se mantuvo el Panel veterinario fuera de este cambio. La compilación se verificó en Android Studio.

## Prompts complementarios de revisión

Los siguientes prompts se redactaron para dejar por escrito criterios de revisión derivados del trabajo. Se incluyen como guías de apoyo, no como transcripciones literales de mensajes enviados.

### Prompt 3 — Coherencia entre pantallas

**Prompt de apoyo:** «Revisa las pantallas del cliente de HuellitasVet como una experiencia móvil completa. Comprueba que Inicio, Mascotas, Ficha, Pedir cita, Mis citas y Perfil compartan jerarquía visual, espaciado y componentes; que cada acción lleve a una pantalla relacionada; y que los estados vacíos indiquen claramente qué puede hacer la persona».

**Respuesta resumida:** Se alinearon jerarquías tipográficas, tarjetas y acciones entre las pantallas, y se conectó el acceso a la lista completa de mascotas desde Inicio.

**Qué se debe validar:** Confirmar en el emulador el recorrido completo entre pantallas, con listas vacías y con datos, y revisar que la navegación inferior y el botón Atrás mantengan el destino esperado.

### Prompt 4 — Revisión segura del cierre de sesión

**Prompt de apoyo:** «Inspecciona el cierre de sesión de la aplicación. Al cerrar la sesión, elimina las pantallas privadas de la pila de navegación, muestra el login y evita que el botón Atrás permita regresar al perfil, las mascotas o las citas de la sesión anterior».

**Respuesta resumida:** La navegación observa el estado de sesión y redirige al login cuando el usuario deja de estar autenticado; la pila anterior se limpia para impedir el retorno a rutas privadas.

**Qué se debe validar:** Ejecutar en el dispositivo el cierre de sesión desde Perfil y probar Atrás y el inicio de sesión posterior para confirmar que cada cuenta ve únicamente su propia sesión.

### Prompt 5 — Registro y requisitos de contraseña

**Texto solicitado por el usuario (redacción profesional):** «Mejora el inicio de sesión para que los clientes puedan acceder también a un formulario de registro. Al enfocar el campo de contraseña durante el registro, muestra una ventana con los requisitos y su estado: mínimo ocho caracteres, mayúscula, minúscula, número, símbolo y sin espacios. Valida que las contraseñas coincidan y que el correo no esté registrado. Al completar el registro, guarda la cuenta localmente, no inicies sesión automáticamente y devuelve al usuario al formulario de inicio de sesión con un mensaje para que acceda de forma explícita».

**Respuesta resumida:** Se añadió el formulario de registro de cliente desde la pantalla de acceso, el diálogo con validación visible de contraseña, confirmación de clave y control de correo duplicado. Tras registrarse, se vuelve al login sin crear una sesión automáticamente.

**Qué se corrigió al integrar:** Se normalizó el correo a minúsculas tanto al registrar como al iniciar sesión, y se agregó el alta a la interfaz del repositorio y a Room. El registro conserva la sesión cerrada y muestra el mensaje de confirmación en el login.

### Prompt 6 — Datos de cliente, tipos de servicio y paleta por rol

**Texto solicitado por el usuario (redacción profesional):** «Mantén la identidad visual de HuellitasVet: verde para clientes y celeste para veterinarios, sin usar grises como color de acento. En el formulario de registro agrega teléfono, correo electrónico y distrito. En el formulario de servicios incorpora un selector desplegable con selección múltiple de categorías, como consulta, vacunación, baño, corte de pelo, desparasitación y emergencia. Añade un segundo usuario administrador para probar el perfil veterinario».

**Respuesta resumida:** El registro ahora solicita y guarda teléfono y distrito junto con el correo; el perfil del cliente muestra esos datos. El formulario de servicios permite seleccionar varias categorías, que se guardan y se muestran en el catálogo y al pedir una cita. El tema mantiene verde para clientes y celeste para veterinarios. Se agregó una segunda cuenta veterinaria de demostración.

**Qué se corrigió al integrar:** Se añadió una migración Room para conservar los datos de instalaciones existentes al agregar teléfono, distrito, categorías de servicio y el usuario administrador de demostración. La cuenta nueva es exclusiva de prueba; el registro público continúa creando cuentas de cliente.

### Prompt 7 — Requisitos no modales y catálogo para clientes

**Texto solicitado por el usuario (redacción profesional):** «En el registro, muestra los requisitos de contraseña al enfocar el campo, pero sin abrir una ventana modal ni quitar el foco. Mantén la lista visible mientras se escribe y actualiza cada requisito con un indicador verde cuando se cumpla y rojo cuando falte. Añade “Servicios” a la navegación inferior del cliente: permite elegir una mascota registrada, filtrar el catálogo por varias categorías y pasar al agendamiento con la mascota y el servicio elegidos. El usuario veterinario debe usar el mismo flujo y funciones de veterinario, sin crear un perfil administrativo distinto».

**Respuesta resumida:** El diálogo bloqueante de contraseña se reemplaza por una tarjeta informativa que permanece en el formulario y actualiza sus indicadores en tiempo real. Se añade el destino Servicios al menú inferior del cliente, con selector de mascota, filtros de categorías múltiples, catálogo y acceso al formulario de cita preseleccionado.

**Qué se corrigió al integrar:** Se pasan la mascota y los servicios elegidos mediante argumentos de navegación. La cita permite una o varias atenciones; sus nombres, precio y duración se reflejan en el resumen y se guardan vinculados.

### Prompt 8 — Horarios de atención y selección múltiple de servicios

**Texto solicitado por el usuario (redacción profesional):** «En el agendamiento de citas, usa horarios reales: de lunes a sábado de 10:00 a. m. a 6:00 p. m. y los domingos de 10:00 a. m. a 1:00 p. m. Muestra algunos horarios como ocupados y evita ofrecerlos. En el apartado Servicios y en el formulario de cita, permite elegir más de un servicio para la misma mascota, ya que una visita puede requerir varias atenciones. Calcula el precio y el tiempo total, adapta las horas disponibles a la duración y evita cruces con otras citas».

**Respuesta resumida:** Los horarios se generan según el día de la semana y la duración total elegida. Las horas ocupadas y los intervalos que se cruzan con una reserva existente quedan deshabilitados; el repositorio vuelve a validar disponibilidad antes de guardar. El cliente puede seleccionar varios servicios desde el catálogo o la pantalla de cita, y la reserva guarda los servicios asociados, el precio total y la duración combinada.

**Qué se corrigió al integrar:** Se conservó `servicioId` como referencia principal para que el historial existente siga funcionando y se agregó una relación Room `cita_servicios` para asociar varios servicios a una cita. La migración enlaza las reservas existentes con su servicio actual. Los datos de demostración también quedan enlazados para que se reflejen como horas ocupadas. Se protegió la validación del botón de cita para que una hora aún vacía no se intente convertir a `LocalTime`, evitando una excepción durante la selección.

### Prompt 9 — Cuentas de veterinarios administradas por ADMIN

**Texto solicitado por el usuario (redacción profesional):** «El administrador debe tener en Ajustes un apartado privado para registrar veterinarios con nombre, correo, teléfono, distrito y contraseña. El registro público seguirá siendo para dueños. Conserva el mismo diseño general de la aplicación y diferencia los perfiles con verde para clientes, celeste para veterinarios y amarillo para administrador. En el acceso, deja la opción de veterinario y permite completar por separado las credenciales demo de Veterinario y Administrador».

**Respuesta resumida:** Se separaron los roles `CLIENTE`, `VETERINARIO` y `ADMIN`. Los perfiles veterinarios y de administración comparten la navegación clínica, pero solo ADMIN ve «Gestionar veterinarios» en Ajustes y puede crear cuentas del equipo. El login público mantiene Dueño/Veterinario y muestra botones de demo distintos. El tema usa verde, celeste y amarillo según el perfil, también en modo oscuro.

**Qué se corrigió al integrar:** La actualización de Room conserva los datos existentes y convierte la cuenta veterinaria demo a rol `VETERINARIO`, manteniendo la cuenta de Camila Rojas como `ADMIN`. Las nuevas cuentas creadas desde Ajustes reciben solo el rol veterinario. La preferencia de modo oscuro se guarda por ID de cuenta y cambia al cambiar de perfil.

## Reflexión (5 líneas)

- La asistencia aceleró la integración de tres funciones entre datos, reglas y pantallas.
- La propuesta inicial de reprogramación permitía escoger la misma hora; la revisión corrigió ese caso.
- Aprendí que la disponibilidad debe verificarse otra vez dentro de una transacción, no solo en la pantalla.
- `Flow` mantiene sincronizados el gráfico y el calendario con los cambios guardados en Room.
- Una compilación correcta no sustituye las pruebas de uso ni las capturas realizadas en un dispositivo.
