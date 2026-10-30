# Semana 06 · TECSUP Store · ConIA

**Estudiante:** Jesús Castillo Sumire
**Curso:** Programación en Móviles  
**Tema:** DropdownMenu contextual, NavigationDrawer y badge reactivo de Favoritos

Esta carpeta contiene la versión ConIA del laboratorio 06. Se creó a partir de la TECSUP Store que está en `Semana_06/R1-SinIA/Lab04CarritoTecsup`; esa carpeta no fue modificada.

## Abrir en Android Studio

1. Abrir la carpeta `Semana_06/R2-ConIA/Lab06TecsupStoreIA` como proyecto.
2. Esperar la sincronización de Gradle y seleccionar un emulador Android.
3. Ejecutar el módulo `app`. El punto de entrada es `MainActivity.kt`.

## Qué se implementó

- Cada tarjeta del catálogo tiene un menú de tres puntos anclado a ella, con iconos para **Guardar/Quitar de Favoritos**, **Compartir** y **Reportar**.
- El drawer tiene **Inicio**, **Mis pedidos**, **Favoritos** y **Perfil**, encabezado de usuario e ítem activo resaltado.
- El badge de Favoritos muestra el número de productos únicos marcados. Se actualiza en el momento desde el mismo `StoreState` que usan las tarjetas y la pantalla de Favoritos; tocar dos veces el mismo producto lo quita y reduce el contador.
- Inicio presenta un catálogo con búsqueda, `LazyRow` de categorías y `LazyColumn` de productos. Conserva la opción de agregar un producto, ahora con validación visible.
- Las otras tres secciones tienen contenido funcional. Los pedidos de demostración descuentan una unidad del stock; Compartir usa el selector de Android y Reportar confirma la acción antes de registrarla localmente.
- El código de ConIA contiene comentarios de intención y pruebas unitarias para las reglas del contador y el formulario.

## Organización

- `MainActivity.kt`: entrada de Android.
- `StoreApp.kt`: tema, `ModalNavigationDrawer`, `Scaffold`, rutas y estado compartido.
- `StoreState.kt`, `StoreRules.kt`, `Producto.kt`: datos, operaciones y reglas comprobables.
- `StoreScreens.kt`: catálogo, favoritos, pedidos, perfil y formulario.
- `ProductCard.kt`: tarjeta, menú contextual y diálogo de reporte.

## Alcance real

Es una **demostración local**: no realiza pagos, no envía reportes a un servidor y no guarda productos, favoritos ni pedidos entre cierres de la aplicación. El menú Compartir sí abre el selector de aplicaciones instalado en el dispositivo.

## Comprobación sugerida

1. En Inicio, abrir `⋮` de un producto y elegir **Guardar en Favoritos**.
2. Abrir el drawer: el badge debe mostrar `1`. Repetir sobre el mismo producto: debe volver a `0`.
3. Guardar dos productos distintos y abrir **Favoritos**: deben aparecer ambos.
4. Registrar un pedido demo y comprobar que aparece en **Mis pedidos** y que el stock del producto baja en uno.
5. Probar búsqueda, categorías, compartir, reportar y validación del formulario.

Las pruebas de reglas están en `app/src/test/java/com/castillo/lab04carritotecsup/StoreRulesTest.kt` y se ejecutan con `gradlew.bat :app:testDebugUnitTest` cuando el SDK de Android y Gradle estén configurados.
