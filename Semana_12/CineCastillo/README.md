# Cine Castillo

Aplicación Android de películas para el Laboratorio 12 de Programación en Móviles. Autor: **Jesús Castillo Sumire**, sección **C24D**.

## Funciones

- Inicio de sesión con Firebase Authentication y rol `usuario` o `admin` leído de Firestore.
- Catálogo y detalle obtenidos de TMDB con Retrofit. La búsqueda filtra las películas disponibles en la copia local.
- Películas guardadas en Room como fuente de datos para la interfaz. Se pueden consultar sin conexión después de una sincronización correcta.
- Favoritos persistentes en el dispositivo, conservados al actualizar el catálogo.
- Panel de administración para ver estadísticas locales, sincronizar y vaciar la caché con confirmación.
- Notificación después de sincronizar, si el permiso correspondiente está concedido.
- Pantalla de créditos con atribución y logotipo oficial de TMDB.

## Estructura y flujo

`data/` contiene los modelos, la API, el DAO, la base de datos y los repositorios. Los ViewModel convierten esos datos en estados de interfaz. `ui/` contiene las pantallas Compose. `MainActivity.kt` conecta la sesión con el catálogo, detalle, favoritos y panel de administración.

```text
Login → Catálogo → Detalle → Favoritos
                  ↘ Panel de administración (solo admin)
TMDB → Retrofit → Repositorio → Room → ViewModel → Compose
Firebase Auth → usuarios/{uid} en Firestore → rol de la sesión
```

## Configuración local

1. Abre esta carpeta en Android Studio.
2. Registra en Firebase una app Android con paquete `com.tecsup.cinecastillo` y copia su `google-services.json` a `app/`.
3. Habilita el proveedor Correo/contraseña en Firebase Authentication.
4. Crea en Firestore documentos `usuarios/{uid}` con un campo de texto `rol` igual a `admin` o `usuario`. Las reglas de lectura están en [firestore.rules](firestore.rules). El cliente no puede modificar su propio rol.
5. Añade `TMDB_API_KEY=tu_clave` a `local.properties`, junto a la ruta local del Android SDK. La clave y el JSON se excluyen del repositorio.
6. Sincroniza Gradle y ejecuta `app`. Se necesita Internet para iniciar sesión y descargar películas; la copia local funciona después sin red.

Proyecto Firebase usado en el laboratorio: `cine-castillo-semana-12`. Su archivo de configuración debe descargarse de ese proyecto específico; el JSON de otra semana no sirve.

## Verificación y evidencia

Compilación: `./gradlew :app:assembleDebug`. Pruebas de ViewModel: `./gradlew :app:testDebugUnitTest`. La verificación visual y las capturas de inicio de sesión, catálogo, detalle, favoritos y panel admin corresponden a la ejecución en Android Studio y deben agregarse al informe después de probar la app en el emulador.

## Alcance

Las películas y favoritos se guardan en Room **en cada dispositivo**, no en la cuenta de Firebase. El rol sí se consulta en Firestore. La caché permite lectura sin conexión, pero la autenticación inicial y la actualización requieren conectividad. Las imágenes de carteles se cargan desde TMDB y pueden faltar sin conexión.
