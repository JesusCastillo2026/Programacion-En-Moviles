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

## Requisitos de la guía

La app cubre el flujo de splash, registro/inicio de sesión, inicio, especialidades, médicos, fecha y hora, confirmación, cita exitosa, citas y perfil. También incorpora como extras detalle/cancelación de cita, resultados, notificaciones y términos. `mejora-ia` añade calendario semanal con `LocalDate`, retratos ficticios locales y `PROMPTS.md`.

