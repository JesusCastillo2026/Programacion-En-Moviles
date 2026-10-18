# Prompts utilizados

## Prompt 1

**Texto recibido del usuario:** «ahora desarrolla la parte de mejorado con IA implementado todo lo que dice le pDF».

**Contexto aplicado:** Laboratorio GLAB-S08-JLEONS-2026_D.pdf, páginas 28–29; proyecto HuellitasVet de `Semana_08/SinIA`; rama `mejora-ia`.

**Qué generó la IA:** Una copia independiente del proyecto en `ConIA` y tres mejoras completas: línea de peso por atención, reprogramación de cita y calendario mensual en la agenda. Se añadieron consultas Room, métodos en la interfaz y la implementación del repositorio, estados en ViewModels y pantallas Compose. También se actualizaron la guía de uso y este registro.

**Qué tuve que corregir al revisar:** La reprogramación no debe presentar la hora original como una nueva opción válida, aunque sea la propia cita. Se bloqueó esa selección en el ViewModel y se conserva una segunda validación en la transacción del repositorio. La versión `ConIA` recibió un `applicationId` diferente para no sustituir la instalación de `SinIA`. Se verificó la compilación; la interacción en emulador queda pendiente de prueba en Android Studio.

**¿Respeta MVVM?:** Sí. Las vistas solo conocen sus ViewModels, los ViewModels solo conocen `VetRepository` (interfaz), y Room queda detrás de `VetRepositoryRoom`. No se añadieron entidades ni se modificó el esquema de la base, así que no corresponde aumentar su versión.

## Reflexión (5 líneas)

- La asistencia aceleró la integración de tres funciones entre datos, reglas y pantallas.
- La propuesta inicial de reprogramación permitía escoger la misma hora; la revisión corrigió ese caso.
- Aprendí que la disponibilidad debe verificarse otra vez dentro de una transacción, no solo en la pantalla.
- `Flow` mantiene sincronizados el gráfico y el calendario con los cambios guardados en Room.
- Una compilación correcta no sustituye las pruebas de uso ni las capturas realizadas en un dispositivo.
