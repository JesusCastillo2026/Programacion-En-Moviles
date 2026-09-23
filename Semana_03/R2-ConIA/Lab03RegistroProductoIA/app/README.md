# Registro de Producto
**Desarrollado por:** Jesús José Castillo Sumire

Esta aplicación permite registrar un producto ingresando su nombre, precio y cantidad, mostrando el importe total calculado en una tarjeta interactiva.

## Capturas de pantalla
![Pantalla vacía](https://ibb.co/YThMrs9w)

![Producto registrado](https://ibb.co/39KtKMgd)

## Pregunta de reflexión
**¿Qué pasaría si declaras las variables de los campos SIN remember?**
Si no se usa `remember`, el estado de las variables no sobrevive a la recomposición de la interfaz. Cada vez que se escriba una letra en el campo de texto, la pantalla se redibuja y la variable vuelve a su valor inicial en blanco, haciendo imposible registrar datos.