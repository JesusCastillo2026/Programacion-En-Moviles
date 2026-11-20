# Registro de prompts y decisiones

Este registro corresponde a las ampliaciones de `mejora-ia`. Lo usé para dejar claro qué pedí, qué parte se incorporó y qué revisé antes de conservarla. La rama `main` mantiene la entrega base del laboratorio.

| Lo que pedí | Propuesta recibida | Qué incorporé o ajusté | Por qué lo dejé así |
|---|---|---|---|
| Agregar filtros por los tipos de lugar sin llevar la lógica a la pantalla. | Mantener una categoría seleccionada y filtrar la lista desde la lógica del ViewModel. | Añadí filtros para Estudio, Comida, Eventos y Servicios; Compose refleja la categoría activa. | La pantalla no decide qué datos cumplen el filtro y la regla se puede probar por separado. |
| Mostrar qué lugares quedan cerca a 100, 200 o 500 metros. | Calcular la pertenencia al radio y dibujar un círculo sobre la ubicación. | Añadí el selector de radio, el círculo y el estado dentro/fuera en las tarjetas. | Se conserva la lista completa para que el radio no esconda el resto de los puntos. |
| Ver el punto actual y poder volver a centrarlo. | Mostrar la ubicación que entrega el proveedor y agregar un control para enfocar el mapa. | El punto del usuario aparece solo si existe permiso y una posición. | El GPS permanece en el Repository; el mapa solo representa el estado recibido. |
| Seguir la ubicación mientras la persona se desplaza. | Convertir las actualizaciones del proveedor a un `Flow` y permitir iniciar/detener. | El Repository usa `callbackFlow`; al cancelar el seguimiento, elimina el callback de ubicación. | Así el seguimiento no deja consultas activas al salir o detenerlo. |
| Recordar el último punto seleccionado. | Guardar el identificador del destino y recuperarlo al volver a abrir. | Añadí DataStore detrás de una interfaz de preferencias. | Se guarda un ID sencillo, no un objeto de interfaz; el ViewModel sigue separado de Context. |
| Tener un mapa oscuro que combine con la interfaz. | Alternar entre dos estilos del proveedor de mapas. | Incorporé estilos claro y oscuro de OpenFreeMap con MapLibre Native. | El cambio del estilo visual no afecta a la ubicación que entrega Google Play Services. |
| Evitar la configuración de una clave de Google Maps en la rama mejorada. | Reemplazar Maps Compose por MapLibre y un estilo público de OpenFreeMap. | Migré la representación del mapa, retiré la lectura de la clave en `mejora-ia` y actualicé la guía. | El docente indicó que podía emplearse otro proveedor de mapas. El servicio público requiere Internet y su disponibilidad no depende del proyecto. |
| Explicar el funcionamiento para la sustentación. | Describir la estructura, el recorrido de los datos y los bloques principales. | Preparé el README, la tabla de pruebas y el informe con ejemplos de `MapaViewModel`, `UbicacionRepositoryGoogle` y `MapaUbicacion`. | Quería poder explicar por qué cada responsabilidad está en esa capa, no solo enumerar archivos. |

## Revisión personal

La sugerencia de la IA no se incorporó sin revisar. Comprobé que la ubicación continuara entrando por el Repository, que el ViewModel publicara estado observable y que Compose se limitara a representar los resultados. También revisé que el seguimiento quite su callback al detenerse y que las reglas de filtro y radio tuvieran pruebas unitarias.

La compilación limpia de la versión actual terminó y pasaron las cinco pruebas unitarias. No pude repetir la parte visual en el emulador porque la máquina no ofreció la aceleración de virtualización que necesita el Pixel 8. Por ese motivo, las capturas anteriores se identifican como históricas y no las presento como prueba de que el mapa migrado carga correctamente.
