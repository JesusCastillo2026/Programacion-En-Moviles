# CampusNav - Version con IA

Proyecto Android desarrollado para la actividad de la Semana 05. La aplicacion toma como base el laboratorio de navegacion con Jetpack Compose y lo mejora con una experiencia tipo portal academico.

## Datos del estudiante

- Nombre: Jesus Castillo Sumire
- Curso: Programacion en Moviles
- Semana: 05

## Funciones principales

- Pantalla de ingreso con credenciales demo.
- Navegacion entre login, inicio, directorio, detalle y perfil.
- Directorio de estudiantes con busqueda por nombre, carrera o codigo.
- Filtros por tecnologia y favoritos.
- Marcado de estudiantes favoritos con persistencia local.
- Perfil editable con validacion de nombre, correo, telefono y biografia.
- Uso de la foto del estudiante en el perfil principal.
- Pantallas de error o estado vacio cuando no hay resultados.

## Credenciales demo

- Correo: `demo@campus.test`
- Contrasena: `NavLab2026`

Tambien se puede usar el boton de exploracion demo para ingresar rapidamente.

## Diferencias frente a la version base

La version base replica el flujo solicitado en el laboratorio: inicio, lista, detalle con argumento entero y perfil. Esta version agrega un diseno mas completo, manejo de estado local, validaciones, persistencia ligera y una interfaz mas cercana a una app academica real.

## Estructura

- `data`: modelos, datos de ejemplo, reglas de busqueda, validacion y almacenamiento local.
- `navigation`: rutas y grafo de navegacion.
- `screens`: pantallas principales.
- `ui`: tema y componentes reutilizables.
- `test`: pruebas unitarias de reglas principales.

## Pruebas realizadas

Se verifico la compilacion del proyecto y las pruebas unitarias con Gradle:

- `:app:assembleDebug`
- `:app:testDebugUnitTest`
- `:app:lintDebug`

## Nota

La aplicacion funciona como prototipo local de laboratorio. No se conecta con servidores reales ni guarda contrasenas.
