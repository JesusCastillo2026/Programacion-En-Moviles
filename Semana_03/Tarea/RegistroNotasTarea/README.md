# Tarea Registro de Notas

**Estudiante:** Jesús Castillo Sumire  
**Curso:** Programación en Móviles  
**Semana:** 03

## Descripción

Aplicación desarrollada con Kotlin y Jetpack Compose para registrar las notas de cuatro cursos y calcular un promedio ponderado. Cada curso utiliza un peso fijo y una barra `Slider` con valores enteros entre 0 y 20.

## Funcionalidades

- Slider independiente para cada curso.
- Badge que muestra la nota seleccionada en vivo.
- Switch para redondear el promedio final.
- Checkbox de confirmación de notas.
- Botón de cálculo deshabilitado hasta confirmar las notas.
- Promedio ponderado mostrado con dos decimales.
- Observación con `when`: EXCELENTE, APROBADO, EN RECUPERACIÓN o DESAPROBADO.
- Chip de color según el resultado.
- Botón LIMPIAR como mejora adicional.
- Pie fijo con el nombre del estudiante.

## Pesos

| Curso | Peso |
|---|---:|
| Fundamentos de Programación | 20% |
| Programación Orientada a Objetos | 25% |
| Programación en Móviles | 30% |
| Base de Datos | 25% |

## Fórmula

`nota1 × 0.20 + nota2 × 0.25 + nota3 × 0.30 + nota4 × 0.25`

El proyecto conserva el patrón de estado de Compose mediante `remember`, `mutableStateOf`, `value` y `onValueChange`.
