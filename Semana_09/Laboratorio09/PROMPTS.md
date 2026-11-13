# Registro de prompts · Laboratorio 09 · mejora-ia

Este registro resume las instrucciones usadas para planificar la ampliación y los ajustes aplicados al revisar la implementación.

## Prompt 1 · Ampliación del laboratorio

**Solicitud:** Completar el laboratorio de Semana 09 a partir del PDF en la carpeta del repositorio, manteniendo `Laboratorio09` como ruta única; crear una versión base en `main` y una versión mejorada en `mejora-ia`, implementar las funciones obligatorias, actualizar la tabla general del curso y publicar los cambios.

**Respuesta resumida:** Se implementaron consultas Retrofit a publicaciones y tareas, navegación al detalle mediante ID, favoritos persistidos en Room, estados de carga/error, documentación de apertura, reflexión y evidencias pendientes.

**Ajustes realizados:** Se separó la versión base de la ampliada por ramas, no por carpetas duplicadas. Se añadió una guía para las capturas y el video requeridos por el PDF, dejando claro que deben registrarse desde la aplicación ejecutándose.

## Prompt 2 · Mejora de la experiencia y ejercicio adicional

**Solicitud:** En `mejora-ia`, mejorar la aplicación con funciones y diseño más completos, respetar la estructura del proyecto y añadir el recurso adicional indicado en el laboratorio.

**Respuesta resumida:** Se incorporó búsqueda por título y contenido, una portada visual, un catálogo de anfibios con datos e imágenes desde una segunda API y una sección accesible desde la navegación inferior.

**Ajustes realizados:** Se creó un servicio Retrofit separado para la URL base de anfibios, se modeló `img_src` con `@SerializedName` y se añadió carga de imágenes con Coil. La compilación de Kotlin se ejecutó y se corrigió el import inválido `maxLines` en la tarjeta de publicaciones. Falta que el estudiante tome sus propias capturas y grabe el video solicitado.
