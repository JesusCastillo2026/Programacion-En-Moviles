# Registro de prompts - GymTracker Pro ConIA

## Implementación y mejora del Laboratorio 07

**Prompt:** «Desarrolla el laboratorio 07 en Android Studio con Kotlin, dentro de Semana07/SinIA, y prepara en Semana07/ConIA una versión mejorada a tu criterio, con funciones o diseño y, si encaja, imágenes generadas con IA».

**Resumen de respuesta:** Se implementó GymTracker Pro como dos aplicaciones Android independientes. Ambas incluyen persistencia Room/SQLite offline, registro e inicio de sesión, menú con Drawer, alta y listado de rutinas, edición, eliminación confirmada, perfil y navegación con IDs de usuario y rutina. ConIA añade búsqueda, filtros por grupo muscular, estadísticas SQL reactivas, validaciones más completas, tema Material 3 y una imagen de portada original.

**Correcciones y decisiones:** El PDF enumera siete pantallas pese a titular la sección «Las 6 vistas»; se desarrollaron las siete descritas. Se añadió una clave foránea desde Rutina a Usuario y restricciones únicas para nombre de usuario y correo. La contraseña se conserva en formato de texto para seguir el esquema académico simplificado del documento, por lo que el proyecto se limita a demostración local y no está listo para producción. Las estadísticas se calculan con las consultas agregadas indicadas en el PDF.

## Imagen de portada

**Prompt visual:** «Fotografía editorial deportiva panorámica para el dashboard móvil GymTracker Pro, gimnasio moderno con fondo suavemente desenfocado, atleta adulta ficticia terminando una rutina con mancuernas situada a la derecha, espacio oscuro vacío a la izquierda para texto superpuesto, luz natural direccional, tonos navy y carbón con acentos azules, anatomía natural, sin texto, logotipos ni marcas».

**Uso:** Imagen generada con la herramienta integrada y guardada en `app/src/main/res/drawable/gym_hero.png`.
