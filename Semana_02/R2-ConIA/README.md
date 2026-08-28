# Laboratorio 02: Sistema de Carrito de Compras en Kotlin

**Estudiante:** Jesús José Castillo Sumire  
**Institución:** Tecsup

## 1. Descripción del Proyecto
Este programa simula la gestión y facturación de un carrito de compras para una tienda tecnológica.

**Funciones implementadas:**
* **Gestión de Stock:** Reducción y reposición dinámica de inventario.
* **Cálculos Financieros:** Funciones para calcular el Subtotal, el IGV (18%) y aplicar descuentos escalonados (5% o 10%) usando la estructura `when`.
* **Programación Orientada a Objetos (Rama 2):** Implementación de una clase base abstracta (`Producto`) con clases hijas (`ProductoFisico` y `ProductoDigital`) demostrando herencia y polimorfismo.
* **Búsqueda y Eliminación:** Uso de las funciones de colección `find` para buscar productos y `removeIf` para eliminarlos dinámicamente del carrito.

## 2. Respuesta a la pregunta de la Parte 2
**¿Por qué `nombre` y `precio` son inmutables (`val`) mientras que `stock` o `cantidad` son mutables (`var`)?**
* Se usa **`val`** en `nombre` y `precio` porque son características fijas del producto. No deben cambiar accidentalmente mientras el cliente realiza la compra.
* Se usa **`var`** en `stock` y `cantidad` porque son valores dinámicos que aumentan o disminuyen constantemente a medida que el usuario agrega o quita productos de su carrito.

## 3. Captura de la Consola

![Captura del resultado final](![img.png](img.png))

![Captura del resultado final](![img_1.png](img_1.png))

![Captura del resultado final](![img_2.png](img_2.png))
