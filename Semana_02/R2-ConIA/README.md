# Laboratorio 02: Carrito de Compras en Kotlin

**Estudiante:** Jesús José Castillo Sumire  
**Carrera:** Diseño y Desarrollo de Software - Tecsup  
**Curso:** Programación en Móviles  
**Docente:** Juan José León Suiyon

---

## 1. Descripción del Proyecto
Este proyecto implementa la lógica de un sistema de carrito de compras por consola utilizando el lenguaje Kotlin. Se aplicaron conceptos fundamentales como variables inmutables y mutables, `data class` para modelado de datos, colecciones dinámicas (`mutableListOf`), funciones con tipos de retorno, formateo de texto con `String.format` y estructuras de control avanzadas como `when`.

### Funciones Implementadas:
- `calcularSubtotal(productos: List<Producto>): Double`: Calcula la suma acumulada de los importes (`precio * cantidad`) de cada producto.
- `calcularIGV(subtotal: Double): Double`: Calcula el 18% del subtotal correspondiente al impuesto general a las ventas.
- `calcularTotal(subtotal: Double, igv: Double): Double`: Obtiene el monto final a pagar sumando el subtotal y el IGV.
- `mostrarDetalle(productos: List<Producto>)`: Imprime la lista de productos adquiridos con columnas tabuladas y alineadas.
- `calcularDescuento(total: Double): Double`: Evalúa con la estructura `when` si el total supera S/ 3000 (5%) o S/ 5000 (10%) para aplicar el descuento respectivo.

---

## 2. Preguntas de Reflexión (Defensa Oral)

### ¿Por qué `nombre` y `precio` son `val` pero `cantidad` es `var`?
- **`val` (inmutable):** Se asigna a `nombre` y `precio` porque las características de identidad y costo base del producto no deben alterarse accidentalmente durante el flujo de compra. Si intentas reasignar un valor a una propiedad declarada con `val`, el compilador de Kotlin genera un error de compilación.
- **`var` (mutable):** Se asigna a `cantidad` debido a que es un valor dinámico que puede incrementarse o decrementarse según la interacción del cliente con el carrito.

### ¿Qué ventajas tiene una `data class` frente a variables sueltas?
Una `data class` permite encapsular y transportar de forma limpia múltiples propiedades relacionadas bajo un solo tipo de dato coherente. Además, Kotlin genera automáticamente métodos utilitarios esenciales como `toString()`, `equals()`, `hashCode()` y `copy()`.

### ¿Por qué usamos `mutableListOf` y no `listOf` para el carrito?
Porque el carrito requiere operaciones de modificación dinámica en tiempo de ejecución (como agregar elementos con `.add()` o removerlos). `listOf()` genera una lista de solo lectura (inmutable) que no permite alterar sus elementos una vez creada.

---

## 3. Salida por Consola
*<img width="515" height="722" alt="image" src="https://github.com/user-attachments/assets/e9980502-f314-4e02-899b-468ff680bae7" />
*
