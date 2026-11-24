# Preguntas para estudiar · MuroCampus

## 1. ¿Qué diferencia hay entre GET y POST? ¿Por qué crear lleva @Body y filtrar por usuario lleva @Query?

GET consulta información que ya está en el servicio. POST envía información nueva. Para crear, el título, el contenido y el usuario viajan como un objeto JSON en el cuerpo, por eso Retrofit usa `@Body`. El filtro es un parámetro pequeño de la dirección, como `posts?userId=2`, así que usa `@Query`.

## 2. ¿Por qué la ruta se escribe `posts` y no `/posts`?

La dirección base termina en una barra: `https://jsonplaceholder.typicode.com/`. Retrofit combina esa base con una ruta relativa. Usar `posts` conserva la dirección del servicio y evita que una barra inicial se trate como una ruta absoluta.

## 3. ¿Por qué el Repository devuelve Result?

La conexión puede fallar o el servidor puede responder con un error. El Repository captura esos casos y los entrega como `Result.failure`, para que el ViewModel decida qué estado presentar. La cancelación de una corrutina se relanza para que no se convierta en un error visible.

## 4. ¿Qué ocurre si falta el permiso INTERNET? ¿Y si el JSON trae otro campo?

Sin el permiso `INTERNET`, la app no puede realizar las llamadas aunque compile. `ignoreUnknownKeys = true` permite ignorar campos adicionales que el modelo no usa. Si falta un campo obligatorio que el modelo espera, la conversión puede fallar porque la respuesta no coincide con los datos declarados.

## 5. ¿Por qué la pantalla usa un when sobre UiState?

`UiState` representa un solo estado a la vez: cargando, éxito o error. El `when` muestra el contenido que corresponde y evita combinaciones confusas, como mantener un indicador de carga junto a un error anterior.

## 6. JSONPlaceholder no guarda los POST. ¿Qué cambiaría con un servicio real?

El servidor validaría y guardaría la publicación. Después del envío, esta volvería a aparecer al consultar la lista. También se tendrían que manejar autenticación, permisos, validación y los errores definidos por ese backend.

## Nota sobre el trabajo realizado

El estudiante pidió que Codex implementara esta versión. Por eso, el código incluye asistencia de IA y no representa un desarrollo SinIA, aunque se conserve como versión base para compararla con `mejora-ia`.
