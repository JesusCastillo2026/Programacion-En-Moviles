# HuellitasVet

Aplicación Android de práctica para la gestión local de una veterinaria. Un dueño registra mascotas, solicita y cancela citas y consulta su historial clínico. El veterinario revisa su agenda, confirma citas, registra atenciones y administra servicios. Ambos perfiles usan la misma base Room del dispositivo.

## Abrir el proyecto

Abre esta carpeta `HuellitasVet` desde Android Studio, espera la sincronización de Gradle y ejecuta el módulo `app` en un dispositivo con API 26 o superior. No abras únicamente `MainActivity.kt`: Android Studio necesita la carpeta que contiene `settings.gradle.kts`.

Datos ficticios iniciales:

| Perfil | Correo | Contraseña |
| --- | --- | --- |
| Dueño | `ana@correo.test` | `1234` |
| Dueño | `luis@correo.test` | `1234` |
| Veterinario | `vet@huellitas.test` | `admin` |

Al iniciarse por primera vez, Room guarda cuatro servicios, tres usuarios, cuatro mascotas y diez citas de demostración. Los registros posteriores quedan solo en ese dispositivo. DataStore conserva el perfil activo, el modo oscuro y los ajustes. No se debe usar información real de pacientes.

## Estructura principal

```text
com.tecsup.huellitasvet
├── MainActivity.kt
├── VetApp.kt
├── data
│   ├── local
│   │   ├── entity: Usuario, Servicio, Mascota, Cita
│   │   ├── dao: UsuarioDao, ServicioDao, MascotaDao, CitaDao
│   │   ├── relation: MascotaConDueno, CitaConDetalle
│   │   ├── VetDatabase.kt
│   │   └── DatosIniciales.kt
│   ├── repository: VetRepository, VetRepositoryRoom
│   └── prefs: PreferenciasStore
├── model: Rol, EstadoCita, Especie, Sexo, Horarios
├── viewmodel: sesión, inicio, mascotas, citas, panel, agenda,
│              atención, pacientes, servicios y perfil
└── ui
    ├── theme: VetTheme
    ├── components: avatar Canvas, tarjetas, chips, gráficos y campos
    ├── navigation: Rutas, NavVet
    ├── dueno: Splash, Login, Inicio, Mascotas, Formulario,
    │          Ficha, PedirCita, MisCitas, Perfil
    └── veterinario: Panel, Agenda, AtenderCita, Pacientes,
                      Servicios, FormularioServicio, Ajustes
```

## Arquitectura MVVM

```text
Vista Compose → ViewModel → VetRepository (interface)
                                  ↓
                           VetRepositoryRoom
                                  ↓
                             DAO → Room

DataStore → ViewModel → Vista Compose
```

Las lecturas observables emplean `Flow` para actualizar las pantallas cuando cambian las tablas; las operaciones de escritura son `suspend`. La atención clínica actualiza cita y peso de la mascota dentro de una sola transacción. El precio de cada cita se conserva como valor histórico aunque luego cambie el precio del servicio.

## Reglas principales

- Solo aparecen al dueño sus mascotas y citas; el veterinario puede ver la agenda y todos los pacientes.
- Las fechas de reserva van desde hoy hasta siete días después. Hay 18 turnos de media hora entre las 09:00 y las 17:30. Una hora ocupada o ya pasada no se puede elegir.
- Una mascota con citas no se elimina. Los servicios con citas pueden desactivarse, pero no eliminarse.
- El dueño cancela únicamente citas pendientes o confirmadas cuando faltan más de dos horas.
- Las citas atendidas y canceladas son finales; registrar una atención exige diagnóstico y peso válidos.

## Capturas de los dos perfiles

Espacios para insertar capturas reales tomadas del emulador:

| N.º | Perfil | Pantalla | Archivo de evidencia |
| --- | --- | --- | --- |
| 1 | Dueño | Inicio | `capturas/dueno-inicio.png` |
| 2 | Dueño | Ficha e historial | `capturas/dueno-ficha.png` |
| 3 | Dueño | Pedir cita | `capturas/dueno-pedir-cita.png` |
| 4 | Veterinario | Panel | `capturas/vet-panel.png` |
| 5 | Veterinario | Agenda | `capturas/vet-agenda.png` |
| 6 | Veterinario | Atención | `capturas/vet-atencion.png` |

## Pruebas de aceptación

La compilación del APK de depuración terminó correctamente. Las doce pruebas funcionales de la página 26 del laboratorio y las seis capturas requieren ejecutarse en un emulador o teléfono; no se marcan como superadas hasta comprobarlas allí.

1. Clave incorrecta: mostrar error sin entrar.
2. Mascota sin nombre: mostrar error y no guardar.
3. Mascota con citas: no permitir eliminar.
4. Nueva cita: figurar pendiente y conservar el precio.
5. Hora ocupada: mostrarla deshabilitada.
6. Agenda: confirmar cita y reflejarla al dueño.
7. Atención: actualizar historial y peso.
8. Cancelación a menos de dos horas: rechazar.
9. Ingresos: coincidir con la suma de citas atendidas del mes.
10. Servicio inactivo: no ofrecerlo al reservar.
11. Servicio con citas: impedir eliminarlo.
12. Reiniciar app: conservar citas, perfil y modo oscuro.

## Para pensar

**¿Por qué usar `Flow` para leer y `suspend` para escribir?** Una lista de citas debe refrescarse al confirmarse o atenderse una cita; `Flow` emite el nuevo estado sin recargar manualmente. Guardar o cancelar es una acción puntual que se espera una vez, por eso se expresa como función suspendida.

**¿Para qué sirve la transacción al atender?** Evita que el historial diga un peso nuevo mientras la ficha de mascota conserva el anterior si ocurre un fallo a mitad de la operación. Se guardan ambos cambios o ninguno.

**¿Qué guarda Room y qué guarda DataStore?** Room almacena relaciones y datos de trabajo —usuarios ficticios, mascotas, servicios y citas—. DataStore guarda preferencias pequeñas como perfil, modo oscuro y notificaciones. Separarlos facilita consultas y persistencia coherente.

**¿Por qué conservar el precio en la cita?** Si se modifica un servicio, las atenciones pasadas deben mantener el importe que se cobró cuando se reservaron; así el total histórico no cambia retroactivamente.

**¿Qué limitación tiene esta práctica?** La base es local: otro teléfono no comparte automáticamente las citas. Para sincronizar varios dispositivos sería necesario un servicio remoto manteniendo el contrato del repositorio.
