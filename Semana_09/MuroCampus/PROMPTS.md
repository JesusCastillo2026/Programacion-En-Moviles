# Registro de instrucciones de mejora-ia

Este archivo resume el alcance solicitado para las ampliaciones de la fase ConIA.

## Instrucción principal

Partiendo del MuroCampus del Laboratorio 09, conserva las funciones solicitadas por el profesor y añade mejoras pequeñas, explicables y separadas en commits. No reemplaces el proyecto anterior de Semana 09. Mantén Compose como capa de presentación, ViewModel como coordinador, Repository para las operaciones y Retrofit para la API. Documenta qué comportamiento es real y cuál es una simulación de JSONPlaceholder.

## Mejoras realizadas

1. Agrega una acción `DELETE` desde el detalle, propagándola por Retrofit, Repository y ViewModel. Informa que JSONPlaceholder no persiste la eliminación.
2. Agrega una búsqueda local de títulos al muro, sin una llamada de red por cada letra escrita.
3. Añade el encabezado `Accept: application/json` con un interceptor de OkHttp.

## Criterio de revisión

Conserva el color azul, las tarjetas y el orden visual de la referencia del PDF en la versión base. La fase mejorada conserva las mismas pantallas requeridas y añade las funciones descritas. No declares persistentes las operaciones de creación o eliminación mientras se use JSONPlaceholder.
