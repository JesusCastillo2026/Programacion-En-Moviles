PROMPT UTILIZADO
Resumen de los Requisitos del Laboratorio

PROMPT N1: INGRESO DE DATOS
Datos requeridos: Placa, tipo de vehículo (Moto, Auto, Camioneta
), nombre del cliente, horas de estadía y validación de cliente frecuente.

Tarifas base: Moto (2.00), Auto (4.00), Camioneta (10.00).

Condiciones de tiempo: El registro mínimo es de 1 hora.

Reglas de cobro:

Las primeras 2 horas cobran la tarifa base normal.

La tercera y cuarta hora tienen un recargo del 20% sobre la tarifa base.

A partir de la quinta hora, el recargo es del 50% sobre la tarifa base.

Descuentos: Los clientes frecuentes reciben un 10% de descuento sobre el importe total.

Flujo del programa: Se debe preguntar al inicio cuántos vehículos se van a registrar (con un límite máximo de 30).

Control de versiones: El desarrollo debe dividirse para realizar tres commits exactos (1. Ingreso de datos, 2. Operaciones, 3. Resultados).



PROMPT N2: REGLAS Y LOGICA

Actúa como un desarrollador experto en Android Studio (Kotlin) para macOS y ayúdame a resolver este laboratorio paso a paso. Necesito que el código sea muy básico y no incluyas ningún comentario dentro del código.

Crea un programa de consola en Kotlin con las siguientes reglas de negocio para un estacionamiento:

Variables y Tarifas



Entradas: Placa, Tipo de vehículo (Moto, Auto, Camioneta), Nombre del cliente, Horas y si es Cliente frecuente (booleano).

Tarifas por hora: Moto = 2.00, Auto = 4.00, Camioneta = 10.00.

Lógica de Negocio



Pregunta al inicio cuántos vehículos se van a registrar (máximo 30). Usa un ciclo para procesarlos.

El tiempo mínimo de registro es de 1 hora.

Para las horas 1 y 2: se cobra la tarifa normal.

Para las horas 3 y 4: se cobra la tarifa normal más un 20% de recargo por cada una de esas horas.

Para la hora 5 en adelante: se cobra la tarifa normal más un 50% de recargo por cada hora adicional.

Si el cliente es frecuente, aplica un descuento del 10% al monto final calculado. Todos los decimales deben usar el formato de punto, por ejemplo 15.32.



PROMPT N3: PARA LOS COMMITS

Estructura de Entrega (Commits)
Para poder subir esto a mi repositorio, necesito que me entregues el código dividido estrictamente en tres partes, simulando mis commits:



Primera parte: Solo el código del ciclo principal y la lectura de datos por teclado.

Segunda parte: Agrega a lo anterior únicamente la lógica matemática y cálculo de tarifas.

Tercera parte: El código final completo que incluye la impresión de los resultados y totales. 



RESULTADOS: SE ADJUNTA 2 IMAGENES

IMAGEN N1:
![Captura de pantalla 2026-08-28 a las 3.15.52 p. m..png](../Desktop/Captura%20de%20pantalla%202026-08-28%20a%20las%203.15.52%E2%80%AFp.%E2%80%AFm..png)


IMAGEN N2:
![Captura de pantalla 2026-08-28 a las 3.16.36 p. m..png](../Desktop/Captura%20de%20pantalla%202026-08-28%20a%20las%203.16.36%E2%80%AFp.%E2%80%AFm..png)