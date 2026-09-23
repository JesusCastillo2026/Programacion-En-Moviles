# Verificación de la entrega

Fecha de ejecución: 23 de septiembre de 2026.

| Proyecto | APK debug | Pruebas unitarias |
| --- | --- | --- |
| SinIA / Clínica Salud+ | Generado | 4 aprobadas |
| SinIA / TECSUP Fit | Generado | 4 aprobadas |
| ConIA / Clínica Salud+ | Generado | 4 aprobadas |
| ConIA / TECSUP Fit | Generado | 4 aprobadas |

Las pruebas comprueban duplicados activos, reserva posterior a cancelación, capacidad no negativa y aislamiento por fecha y horario.

Se compilaron las cuatro aplicaciones con Gradle 9.5.0, AGP 9.3.3, Kotlin 2.2.10 y SDK 37.0. Se usó el JDK integrado en Android Studio. La compilación del código y el empaquetado fueron satisfactorios.

El entorno de ejecución bloqueó el SDK instalado y la creación del archivo de bloqueo de la firma debug. Se utilizó un SDK local de trabajo y una clave de depuración generada para estas pruebas. Para generar los APK se omitió la tarea validateSigningDebug, no la compilación ni las pruebas. Las claves y rutas locales están excluidas de Git.

No se ejecutaron pruebas manuales en un emulador: el cliente adb quedó bloqueado al intentar acceder a su directorio de usuario. No se certifica identidad píxel a píxel con las referencias ni el comportamiento visual en todos los dispositivos. Seguir el recorrido de SUSTENTACION.md en Android Studio.

## Cumplimiento que requiere al alumno

- La rama `main` contiene la fase base en `SinIA` y la rama `mejora-ia` contiene las mejoras en `ConIA`.
- La guía exige commits distribuidos en más de un día. Esta sesión registra fechas reales de un solo día.
- La entrega puntual y la sustentación oral dependen de la asignación y del alumno.
- Las reservas permanecen en memoria, tal como se documenta; no hay almacenamiento persistente.

