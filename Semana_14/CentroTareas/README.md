# CentroTareas

Aplicación Android del Laboratorio 14 de Programación en Móviles. Sirve para observar cómo Kotlin ejecuta tareas en segundo plano mientras la interfaz sigue respondiendo.

## Recorrido de la aplicación

1. **Tarea larga:** inicia una tarea con avance visible. Mientras corre, el contador continúa respondiendo. Se puede cancelar y volver a iniciar.
2. **Números primos:** calcula los primos hasta un límite elegido, muestra el progreso y permite detener el cálculo.
3. **Cargas secuencial y paralela:** compara la duración de dos estrategias con datos de demostración, muestra errores simulados y ofrece reintento.
4. **Panel de concurrencia:** inicia a la vez cuatro trabajos independientes: progreso, primos hasta 2,000,000, carga de perfil y carga de pedidos. Cada uno tiene estado y progreso propios; se puede cancelar o reintentar uno sin detener los demás.
5. **Actividad reciente:** registra inicios, avances, cancelaciones y resultados. Los resultados recientes se conservan con DataStore.

Cada ejercicio presenta su propio estado y progreso. La comparación explica cuál estrategia terminó antes cuando se han ejecutado ambas.

## Arquitectura

La pantalla Compose recoge estados observables del `TareasViewModel`. El ViewModel coordina las corrutinas, cancela los trabajos y publica progreso y eventos. `TareasRepository` contiene las operaciones demoradas y el conteo de primos; `HistorialRepository` guarda los resultados recientes.

```text
PantallaTareas (Compose)
        ↓ acciones y StateFlow
TareasViewModel (viewModelScope)
        ↓ operaciones
TareasRepository / HistorialRepository
```

Las cargas de perfil y pedidos son ficticias: no consultan una API ni una cuenta real.

## Abrir en Android Studio

Abre la carpeta `CentroTareas`, sincroniza Gradle y ejecuta la configuración `app`. El proyecto usa Gradle 8.13; selecciona JDK 21 en la ventana de importación de Android Studio.

## Variantes

- `main`: versión base del laboratorio, con el orden y la presentación de la guía.
- `mejora-ia`: conserva los ejercicios, agrega el panel que demuestra concurrencia real y actualiza la presentación con tipografía consistente, botones degradados y superficies oscuras con bordes translúcidos.

El panel de concurrencia es la mejora adicional del laboratorio. Las cargas de perfil y pedidos son simuladas para mostrar cómo se comporta cada trabajo cuando termina o falla.
