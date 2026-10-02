# Registro de interacción con IA · Semana 06

Este registro corresponde solo a la versión ConIA de TECSUP Store.

## Solicitud inicial

**Prompt:** «Estoy trabajando mediante Android Studio y mi trabajo realizado SINIA está en mi GitHub semana06; solo quiero que termines de realizar el trabajo CONIA». Se adjuntó `GLAB-S06-JLEONS-2026-02_CD.pdf`.

**Respuesta resumida:** Se revisaron los requisitos del laboratorio y el proyecto existente en `R1-SinIA`. La mejora obligatoria es conectar Favoritos del DropdownMenu con un badge reactivo en el NavigationDrawer. Se creó `R2-ConIA` a partir de la base disponible.

**Corrección aplicada:** Se separó el estado de favoritos de cada tarjeta y se elevó a un único `StoreState`, compartido por catálogo, pantalla de Favoritos y drawer. El badge se calcula a partir de identificadores únicos, no de un contador que solo suma.

## Aclaración de alcance

**Prompt:** «No hagas ClinicaPlus, eso es para otro día».

**Respuesta resumida:** Se limitó el trabajo a TECSUP Store. La tarea complementaria de Clínica SaludPlus del mismo PDF no forma parte de esta entrega.

**Corrección aplicada:** No se creó ni modificó ningún proyecto de Clínica SaludPlus en Semana 06.

## Evidencia de la fase SinIA

**Prompt:** Se compartió el documento `Evidencia_Lab06` de Google Docs para mostrar el avance SinIA.

**Respuesta resumida:** La evidencia confirmó la base: `ModalNavigationDrawer`, cuatro destinos, tarjeta con botón de tres puntos y opciones Favoritos, Compartir y Reportar.

**Corrección aplicada:** La versión ConIA conserva esa estructura reconocible y completa las acciones y destinos que en la base estaban como demostración o «en construcción».

## Revisión de una propuesta de badge

**Prompt:** Se propuso pasar `onFavoritoAgregado` desde la tarjeta hasta un `contadorFavoritos` global y se preguntó si era suficiente.

**Respuesta resumida:** Es suficiente para mostrar un badge básico, pero cada pulsación sobre el mismo producto vuelve a sumar y no permite quitarlo ni mostrar una lista real.

**Corrección aplicada:** Se guarda un conjunto de IDs favoritos y el badge se deriva de ese conjunto. Se agregaron pruebas para comprobar que el mismo producto no se contabiliza varias veces y que puede quitarse.
