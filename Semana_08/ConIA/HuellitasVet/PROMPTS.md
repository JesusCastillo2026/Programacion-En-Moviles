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

## Reflexión (5 líneas)

- La asistencia aceleró la integración de tres funciones entre datos, reglas y pantallas.
- La propuesta inicial de reprogramación permitía escoger la misma hora; la revisión corrigió ese caso.
- Aprendí que la disponibilidad debe verificarse otra vez dentro de una transacción, no solo en la pantalla.
- `Flow` mantiene sincronizados el gráfico y el calendario con los cambios guardados en Room.
- Una compilación correcta no sustituye las pruebas de uso ni las capturas realizadas en un dispositivo.
