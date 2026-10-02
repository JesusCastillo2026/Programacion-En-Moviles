# Laboratorio 03 Registro de Producto ConIA

**Estudiante:** Jesús Castillo Sumire  
**Curso:** Programación en Móviles  
**Tema:** Jetpack Compose, estado y validación de formularios

## Descripción

Esta versión mejora el registro de productos del laboratorio mediante dos funciones específicas solicitadas en la Parte B del PDF:

- Validación de campos vacíos y de valores numéricos inválidos antes de mostrar la tarjeta.
- Botón **Limpiar formulario** para restablecer todos los campos y ocultar el resultado.

La aplicación mantiene la pantalla base con nombre, precio, cantidad, cálculo del importe total y mensaje de confirmación. El proyecto usa Kotlin, Jetpack Compose, `remember`, `mutableStateOf`, `toDoubleOrNull`, `toIntOrNull` y `String.format("%.2f")`.

## Mejora implementada

| Prompt que usé | Qué generó la IA | Qué acepté o corregí |
|---|---|---|
| Agrega validación de campos vacíos y valores numéricos antes de mostrar la tarjeta. Si existe un error, muestra el mensaje en rojo y no muestres el resumen. | Propuso validar nombre, precio y cantidad dentro del evento del botón. | Se aceptó la regla y se ajustaron los mensajes para que fueran claros y estén en español. |
| Agrega un botón Limpiar que vacíe nombre, precio y cantidad, oculte la tarjeta y borre el error. | Propuso restablecer los estados del formulario desde un segundo botón. | Se aceptó el comportamiento y se colocó como `TextButton` debajo de la acción principal. |

## Casos verificados

- Campos vacíos: aparece un error y no se muestra la tarjeta.
- Precio con letras o menor que cero: aparece un mensaje de validación.
- Cantidad no entera o menor que cero: aparece un mensaje de validación.
- Datos correctos: se muestra la tarjeta con el importe calculado a dos decimales.
- Limpiar formulario: se restablece la pantalla inicial.
