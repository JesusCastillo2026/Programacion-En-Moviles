# Registro de instrucciones y prompts — mejora-ia

Este registro corresponde a la implementación de la rama `mejora-ia`. Las solicitudes se transcriben en extractos fieles cuando se indica expresamente; las instrucciones técnicas derivadas de la guía se resumen para mostrar qué se pidió, qué se produjo y qué se verificó.

## 1. Completar la app paciente según la guía

**Prompt del encargo:** «Vamos a crear un nuevo trabajo de Química Plus, pero en esta ocasión vamos a hacerlo desde cero»; el resto del encargo aclara que se refiere a Clínica SaludPlus, pide cumplir la estructura, las pantallas, los cuatro retos y la metodología del PDF, y crear las versiones en `main` y `mejora-ia`.

**Respuesta resumida:** Se reconstruyó una aplicación de paciente con registro e inicio de sesión local, catálogo de especialidades y médicos, selección de fecha y hora, confirmación de cita, navegación inferior y pantallas adicionales de citas, perfil, resultados, notificaciones, términos y detalle.

**Correcciones y decisiones aplicadas:** Se respetó el nombre Clínica SaludPlus (no “Química Plus”), se trabajó dentro de `Semana_06/evaluativa`, sin reutilizar Semana 05 ni cambiar el laboratorio. Como no estaba el ZIP del docente, los paquetes y pantallas se reconstruyeron a partir de la descripción del PDF. La información queda en memoria y se reinicia al cerrar la app.

## 2. Mejora solicitada para `mejora-ia`

**Prompt del encargo:** «En mejora con IA vas a añadir imágenes de lo que son el perfil de los profesores, en el cual como no tenemos imágenes vas a crear imágenes con inteligencia artificial». También se pidió que la versión mejorada incluya mejoras funcionales y documentación comprensible dentro del código.

**Respuesta resumida:** Se generaron recursos gráficos locales para la portada de bienvenida y cuatro retratos ilustrativos. Se integró un calendario de cinco días hábiles que avanza por semanas, excluye fechas pasadas y fines de semana, muestra el mes y reinicia la hora elegida cuando se cambia la fecha.

**Correcciones y decisiones aplicadas:** Los retratos son ficticios y se identifican como ilustrativos, no como fotos de personas reales. La cuadrícula de retratos es de 1254 × 1254 píxeles y se divide en cuatro recortes de 627 × 627. El calendario desactiva la navegación hacia semanas anteriores a la actual, conserva el filtrado de horarios reservados y permite avanzar a semanas futuras.

## 3. Registro y verificación

**Prompt del encargo:** «documenta cada prompt usando un archivo prompts.md, con el prompt, la respuesta resumida y que tuviste que corregir».

**Respuesta resumida:** Este archivo reúne las instrucciones que guiaron esta versión, un resumen de la solución y las decisiones de corrección relevantes.

**Correcciones aplicadas:** Se evita presentar los retratos generados como fotografías auténticas. Se añadieron pruebas unitarias para la regla de cinco días hábiles y compilación de Kotlin. La generación final del APK y la ejecución completa de pruebas no pudieron verificarse en este equipo porque AAPT2 se cae con una excepción nativa al procesar recursos de AndroidX.
