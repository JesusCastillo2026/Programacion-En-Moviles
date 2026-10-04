# Informe breve — Clínica SaludPlus App Paciente

## Alcance

Se implementaron las 11 pantallas obligatorias y los cuatro retos: Detalle de cita, Resultados, Notificaciones y Términos. La información vive en colecciones en memoria. El ZIP SaludPlusCitas.zip mencionado en la guía no se recibió; la estructura se reconstruyó desde la clínica de Semana 05 siguiendo el PDF.

## Preguntas de reflexión

1. **¿Por qué los modelos y rutas estaban completos en la guía, y las pantallas no?** El ejercicio buscaba practicar colecciones, estado de Compose y construcción de interfaces. En esta entrega no había esqueleto físico, así que también se reconstruyeron los modelos y la navegación.
2. **¿Por qué el repositorio es un object?** Todas las pantallas consultan la misma instancia. Si cada pantalla creara su propia lista, una cita guardada en Confirmar no aparecería en Mis citas.
3. **¿Cómo se actualizan búsqueda y horarios?** La búsqueda depende de un estado de texto que Compose vuelve a leer al cambiar. Las citas están en una lista observable; cuando se agrega o cancela una, se recalculan los horarios disponibles.
4. **¿Qué cambia con popUpTo?** La navegación normal conserva la pantalla anterior para Atrás. Al confirmar, popUpTo quita los pasos de agendamiento; Atrás no devuelve a una confirmación que ya se realizó.
5. **¿Qué se corrigió en el calendario ConIA?** Se aseguró que los cinco días sean hábiles y no pasados, que el título muestre cambios de mes o año y que cambiar día o semana borre la hora anterior. Una prueba también verifica que una reserva ocupa su turno y al cancelarla lo libera.
6. **¿Drawer o NavigationBar?** El Drawer conviene cuando hay muchos destinos secundarios; la barra inferior facilita alternar entre cuatro secciones principales de uso frecuente, como Inicio, Citas, Resultados y Perfil.

## Observaciones

1. Al faltar el ZIP original no fue posible conservar firmas de código que nunca se recibieron. Las funciones se definieron conforme al listado y comportamiento descritos en el PDF.
2. Resultados muestra datos de demostración; no existe conexión clínica ni persistencia. Al cerrar el proceso, usuarios y citas desaparecen.

## Conclusiones

1. Reutilizar la estructura de la clínica anterior aceleró la organización del proyecto, pero fue necesario separar modelos, repositorio, rutas y pantallas para cubrir todo el flujo de la nueva guía.
2. La mejora del calendario aporta fechas reales y navegación semanal sin perder el bloqueo de horas reservadas. Las pruebas de fechas y del repositorio ayudaron a comprobar esa integración.
