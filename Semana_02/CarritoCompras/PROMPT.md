# PROMPT UTILIZADO


Actúa como un arquitecto de software senior y asesor académico experto en Kotlin. Necesito que diseñes la estructura y el código completo para un sistema de Gestión de Carrito de Compras para una Tienda Tecnológica, adaptado a las especificaciones de mi laboratorio en Tecsup para el alumno Jesús José Castillo Sumire.

El proyecto debe dividirse estrictamente en dos fases o ramas:

Rama 1: Enfoque Estructurado (Sin clases)

Desarrolla el sistema utilizando únicamente programación procedimental con listas o arrays paralelos.

Crea funciones independientes para calcular el subtotal, el IGV (18%) y el total.

Aplica una estructura when para calcular un descuento escalonado (5% para compras mayores a 3000.00 y 10% para compras mayores a 5000.00).

Imprime el reporte detallado en consola con las columnas alineadas.

Rama 2: Enfoque Orientado a Objetos (POO estricta)

Refactoriza el sistema evidenciando obligatoriamente los 4 pilares: Abstracción (clase base abstracta Producto), Encapsulamiento (atributos privados y métodos de acceso), Herencia (subclases ProductoFisico con costo de envío y ProductoDigital con link de descarga) y Polimorfismo (método de cálculo de subtotal sobreescrito según el tipo de producto).

Incluye la lógica de stock (reducción al agregar y reposición al eliminar).

Implementa la lógica para encontrar e imprimir el producto más caro del carrito.

Reto Adicional: Crea una función de búsqueda usando find y un método de eliminación dinámica en el carrito usando removeIf.

Restricciones Técnicas Globales:

Toda salida numérica en consola debe formatearse obligatoriamente a dos cifras decimales separadas por punto (por ejemplo, 15.32), nunca por coma. Utiliza Locale.US en los String.format.

El código no debe contener advertencias de compilación.

Entregables Adicionales:

Diseña un plan exacto de 6 commits distribuidos equitativamente (3 para la Rama 1 y 3 para la Rama 2) con descripciones lógicas para simular un desarrollo progresivo entre lunes y jueves.

Genera el código Markdown para el archivo README.md del proyecto, incluyendo el título, mis datos, la descripción del programa y la respuesta teórica a: "¿Por qué nombre y precio usan val mientras que cantidad usa var?".
