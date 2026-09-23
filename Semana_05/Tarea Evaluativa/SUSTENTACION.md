# Guía de sustentación

## Recorrido de los datos

Inicio obtiene un Item del catálogo y navega a detail/{id}. El argumento entero identifica al médico o la clase. Detalle recupera ese elemento y pasa su id a book/{id}. La selección de fecha y hora crea un Booking; la confirmación recibe su identificador en confirmation/{id}. Así se evita depender de un elemento global seleccionado que pueda cambiar durante la navegación.

## Estado sin MVVM

ReservationApp conserva una lista observable con remember y mutableStateListOf. Las pantallas reciben los datos y funciones que notifican las acciones. Las selecciones usan mutableStateOf y rememberSaveable. No hay ViewModel, repositorios remotos ni base de datos. Las reservas duran mientras vive la actividad; no se promete persistencia al reiniciar la aplicación o recrear la actividad.

## Scaffold y navegación secundaria

ModalNavigationDrawer envuelve el Scaffold porque es un contenedor independiente. Su contenido lateral se declara en drawerContent. El Scaffold organiza topBar, bottomBar y content. El padding que entrega se aplica al NavHost para que el contenido no quede detrás de las barras.

Fit observa currentBackStackEntryAsState y compara destination.route con la ruta de cada pestaña. Inicio, Reservas, Rutinas y Perfil usan launchSingleTop y restauración del estado de navegación para evitar duplicar destinos.

## Selección única

Cada grupo guarda un solo String. Al tocar otra opción, reemplaza el anterior. ChoiceRow usa selectableGroup y Role.RadioButton para comunicar esa relación a accesibilidad aunque dibuje tarjetas. Confirmar se habilita cuando existen ambas selecciones y no hay una reserva activa duplicada.

## Pruebas

BookingRulesTest comprueba duplicados activos, disponibilidad tras cancelar, capacidad mínima de cero y aislamiento entre horarios. Ejecutar gradlew.bat :app:testDebugUnitTest desde cada proyecto.

Prueba manual pendiente hasta ejecutar las aplicaciones en un dispositivo:

1. Filtrar la lista y abrir cada médico o clase; comprobar que el detalle corresponde.
2. Elegir una fecha y hora; cambiar la selección y comprobar que solo queda una activa por grupo.
3. Confirmar, revisar el resumen y comprobar que la reserva aparece en el listado.
4. Intentar repetir el mismo horario; comprobar el aviso de duplicado.
5. Visitar todos los destinos secundarios y utilizar Atrás.
6. En ConIA, buscar por nombre, cancelar con el diálogo y volver a reservar el horario liberado.
7. Probar fuente grande, pantalla pequeña, rotación y reinicio; observar la limitación del estado en memoria.

## Cambios asistidos

La rama mejora-ia incorpora búsqueda, una presentación visual enriquecida, estadísticas derivadas de las reservas y cancelación confirmada. PROMPTS.md registra las solicitudes y los ajustes reales. El alumno debe revisar y comprender el código antes de sustentarlo.

## Diferencias frente a las imágenes

Las referencias son esquemas de pantalla. Se conservan textos, paletas y distribución general; no se afirma equivalencia píxel a píxel sin comparar capturas en el mismo tamaño. Se incluyen controles adicionales exigidos por el texto de la rúbrica: selección de horario en Fit y regreso al inicio desde confirmación.
