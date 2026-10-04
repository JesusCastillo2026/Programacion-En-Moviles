# Registro de prompts — SaludPlus ConIA

Los textos siguientes resumen las instrucciones visibles dadas para esta entrega y las decisiones tomadas al implementarlas. No se atribuyen acciones ni respuestas que no ocurrieron.

## 1. Encargo de la app Paciente

**Prompt (resumen fiel):** Completar Clínica SaludPlus de Semana 06 con repositorio en memoria, 15 pantallas, navegación con parámetros, componentes, una versión base y una mejora ConIA con calendario dinámico, código documentado y commits.

**Respuesta resumida:** Se organizó una app Android con modelos, repositorio, navegación y pantallas separadas. La mejora usa LocalDate, cinco días hábiles, cambio semanal y fecha de confirmación en español.

**Corrección realizada:** El ZIP del esqueleto citado en el PDF no estaba disponible. Se tomó como referencia la clínica de Semana 05 y se reconstruyó un proyecto independiente, sin afirmar que se completó un ZIP recibido.

## 2. Reutilización del trabajo anterior

**Prompt (resumen fiel):** Usar la estructura de la clínica ya trabajada; dejar la versión base sencilla y sin comentarios, y documentar la versión ConIA por bloques.

**Respuesta resumida:** Se crearon proyectos Android separados bajo SinIA y ConIA; ambos comparten el flujo requerido, mientras ConIA añade un diseño diferenciado y explicaciones en el código.

**Corrección realizada:** Se separaron identificadores de instalación para poder abrir e instalar ambas apps sin que una reemplace a la otra.

## 3. Control de versiones

**Prompt (resumen fiel):** Verificar que el repositorio tenga al menos ocho commits de la fase base en main y tres commits de la mejora en mejora-ia, con push de ambas.

**Respuesta resumida:** La base se construyó en diez commits incrementales. La rama de mejora integra esa base y registra por separado la copia, el calendario y la documentación/diseño.

**Corrección realizada:** Se verificaron compilación, pruebas y conteos de commits antes de la publicación.
