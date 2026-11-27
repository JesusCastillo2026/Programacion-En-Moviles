# Laboratorio 15 · AvisosCampus

## Descripción

AvisosCampus es una aplicación Android de práctica para crear avisos locales, pedir el permiso de notificaciones en Android 13 o superior, programar un recordatorio breve y reaccionar a cambios del modo avión. La interfaz se construyó con Kotlin, Jetpack Compose y Material 3.

La implementación está organizada en dos ramas del repositorio. La rama `main` contiene la versión base del laboratorio; `mejora-ia` añade funciones y ajustes visuales. El trabajo de esta entrega se preparó con asistencia de Codex. Por ese motivo, `main` no debe presentarse como una implementación realizada sin IA.

## Estructura

```text
AvisosCampus/
├── app/src/main/java/com/tecsup/avisoscampus/
│   ├── AvisosApp.kt
│   ├── MainActivity.kt
│   ├── data/AvisosRepository.kt
│   ├── model/Modelos.kt
│   ├── notificacion/Notificador.kt
│   ├── receiver/ModoAvionReceiver.kt
│   ├── ui/PantallaAvisos.kt
│   ├── ui/HistorialAvisos.kt
│   ├── ui/theme/Theme.kt
│   └── viewmodel/
│       ├── AvisosViewModel.kt
│       └── AvisosViewModelFactory.kt
├── app/src/main/res/drawable/ic_notificacion.xml
└── capturas/
```

## Arquitectura y flujo

```text
Persona
  ↓ toca un control
Pantalla Compose
  ↓ llama una acción y observa StateFlow / SharedFlow
AvisosViewModel
  ↓ registra el aviso
AvisosRepository
  ↓ entrega el evento único
Pantalla Compose
  ↓ solicita mostrarlo
Notificador → sistema Android
```

El ViewModel coordina los avisos y publica eventos; no accede al sistema Android. `Notificador` gestiona el canal, el permiso y la presentación. El receptor de modo avión solo lee el estado del broadcast. La pantalla lo registra al activarse la opción y lo retira al salir de esa composición. El historial de esta versión se mantiene en memoria durante la sesión.

## Cómo abrir

Abre `Semana_15/AvisosCampus` como proyecto en Android Studio, espera la sincronización de Gradle y ejecuta la configuración `app` en un emulador Android. En Android 13 o superior, acepta el permiso para probar las notificaciones. El proyecto usa Gradle 8.13, Android Gradle Plugin 8.13.2 y Java 17.

## Capturas de pantalla

Guarda las evidencias en `capturas/` después de ejecutar la app en el emulador. Los nombres previstos son:

- `capturas/01_permiso_notificaciones.png` — estado y solicitud del permiso.
- `capturas/02_aviso_en_panel.png` — notificación visible en el panel del sistema.
- `capturas/03_recordatorio.png` — cuenta regresiva y aviso recibido.
- `capturas/04_modo_avion.png` — opción del receptor e historial del cambio.
- `capturas/05_permiso_denegado.png` — aviso guardado y acceso a ajustes.

Las imágenes todavía no se han capturado en un emulador en esta copia del proyecto.

## Preguntas para pensar

### ¿Por qué una notificación necesita un canal y un permiso?

Desde Android 8, el canal agrupa las notificaciones y permite que la persona controle su comportamiento. Desde Android 13, la app debe pedir permiso para mostrarlas. Si falta el canal, Android no puede clasificarlas correctamente; si falta el permiso, no se deben publicar, aunque el aviso puede conservarse en el historial.

### ¿Por qué el ViewModel emite un evento y no muestra la notificación directamente?

El ViewModel debe coordinar estado y reglas de la aplicación sin depender de `Context` ni de servicios del sistema. La pantalla recibe el evento y llama a `Notificador`, que es la pieza encargada de interactuar con Android.

### ¿Qué ocurre si dos notificaciones usan el mismo identificador?

Android trata el identificador como la referencia de actualización. Si se vuelve a usar para el mismo canal, la nueva notificación reemplaza la anterior en vez de aparecer como una entrada independiente.

### ¿Por qué `onReceive` debe ser corto y por qué hay que retirar el receptor?

El sistema espera que `onReceive` termine pronto; una operación larga puede afectar la respuesta del dispositivo o detener el proceso. Un receptor dinámico se desregistra cuando deja de ser necesario para evitar registros duplicados y referencias a pantallas que ya no existen.

### ¿Cuándo usarías una notificación local y cuándo una notificación push de Firebase?

Usaría una notificación local para un recordatorio que depende de una acción o un horario guardado en el dispositivo. Usaría Firebase Cloud Messaging para avisos que se originan en un servidor y deben llegar al dispositivo aunque la app no esté abierta.

## Comprobación

La compilación debe verificarse con `gradlew assembleDebug` desde esta carpeta. En el entorno de preparación, Gradle no pudo descargar su distribución porque el acceso a `services.gradle.org` no estaba disponible; por eso este README no afirma que la compilación haya sido aprobada.
