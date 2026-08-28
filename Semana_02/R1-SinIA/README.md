# Carrito de Compras - Versión Estructurada (Rama 1)

**Autor:** Jesús José Castillo Sumire  
**Institución:** Tecsup  
**Carrera:** Diseño y Desarrollo de Software

## Descripción del Proyecto
Esta versión corresponde a la **Rama 1 (R1-SinIA)** del laboratorio. Implementa la gestión de un carrito de compras utilizando exclusivamente **programación estructurada** y colecciones paralelas en Kotlin, sin aplicar Programación Orientada a Objetos.

## Características Principales
* **Listas Paralelas:** Almacenamiento de códigos, nombres, tipos, precios y cantidades en estructuras de listas independientes.
* **Funciones Modulares:** Bloques lógicos independientes para el cálculo de subtotales y operaciones matemáticas.
* **Descuento Escalonado (`when`):** Aplicación automática de descuentos según el monto total bruto (> S/ 3000.00 un 5%, > S/ 5000.00 un 10%).
* **Formato Numérico Estricto:** Uso de `Locale.US` para asegurar que los montos se muestren con dos cifras decimales separadas por punto.

## Historial de Commits Progresivos
1. `feat(r1): lista inicial de productos y precios`
2. `feat(r1): agrega calculo de subtotal e igv con formato decimal`
3. `feat(r1): implementa descuento con when y reporte final estructurado`