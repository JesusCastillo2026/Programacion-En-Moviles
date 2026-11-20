from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    Image,
    PageBreak,
    Paragraph,
    Preformatted,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)


ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / "docs"
EVIDENCE = DOCS / "evidencias"
OUTPUT = DOCS / "Informe_Laboratorio_13_CampusGPS.pdf"

pdfmetrics.registerFont(TTFont("Arial", r"C:\Windows\Fonts\arial.ttf"))
pdfmetrics.registerFont(TTFont("Arial-Bold", r"C:\Windows\Fonts\arialbd.ttf"))

NAVY = colors.HexColor("#183153")
BLUE = colors.HexColor("#2E6F9E")
SOFT = colors.HexColor("#EAF1F6")
INK = colors.HexColor("#263442")
MUTED = colors.HexColor("#526170")
GREEN = colors.HexColor("#2E6F55")
AMBER = colors.HexColor("#8A5C10")
RED = colors.HexColor("#934343")

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(
    name="Cover", fontName="Arial-Bold", fontSize=27, leading=33,
    textColor=NAVY, alignment=TA_CENTER, spaceAfter=8,
))
styles.add(ParagraphStyle(
    name="SubCover", fontName="Arial", fontSize=12, leading=18,
    textColor=MUTED, alignment=TA_CENTER, spaceAfter=6,
))
styles.add(ParagraphStyle(
    name="SectionX", fontName="Arial-Bold", fontSize=17, leading=21,
    textColor=NAVY, spaceBefore=7, spaceAfter=8,
))
styles.add(ParagraphStyle(
    name="SubX", fontName="Arial-Bold", fontSize=11, leading=15,
    textColor=BLUE, spaceBefore=5, spaceAfter=4,
))
styles.add(ParagraphStyle(
    name="BodyX", fontName="Arial", fontSize=9.2, leading=13.7,
    textColor=INK, spaceAfter=6,
))
styles.add(ParagraphStyle(
    name="SmallX", fontName="Arial", fontSize=7.7, leading=10.5,
    textColor=MUTED, spaceAfter=4,
))
styles.add(ParagraphStyle(
    name="CellX", fontName="Arial", fontSize=7.2, leading=9.2,
    textColor=INK,
))
styles.add(ParagraphStyle(
    name="CellHeadX", fontName="Arial-Bold", fontSize=7.4, leading=9.2,
    textColor=colors.white,
))
styles.add(ParagraphStyle(
    name="CaptionX", fontName="Arial", fontSize=8, leading=11,
    textColor=MUTED, alignment=TA_CENTER, spaceBefore=4, spaceAfter=6,
))
styles.add(ParagraphStyle(
    name="CodeX", fontName="Courier", fontSize=7.1, leading=9.3,
    textColor=INK, leftIndent=4, rightIndent=4,
))

P = lambda text, style="BodyX": Paragraph(text, styles[style])


def make_table(rows, widths, header=True, font_size=None):
    table = Table(rows, colWidths=widths, repeatRows=1 if header else 0, hAlign="LEFT")
    commands = [
        ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#C8D5E0")),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 6),
        ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 5),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
        ("ROWBACKGROUNDS", (0, 1 if header else 0), (-1, -1),
         [colors.white, colors.HexColor("#F4F7FA")]),
    ]
    if header:
        commands.append(("BACKGROUND", (0, 0), (-1, 0), NAVY))
    table.setStyle(TableStyle(commands))
    return table


def code_block(code):
    content = Preformatted(code.strip("\n"), styles["CodeX"], maxLineLength=110)
    table = Table([[content]], colWidths=[172 * mm])
    table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), SOFT),
        ("BOX", (0, 0), (-1, -1), 0.45, colors.HexColor("#C8D5E0")),
        ("LEFTPADDING", (0, 0), (-1, -1), 7),
        ("RIGHTPADDING", (0, 0), (-1, -1), 7),
        ("TOPPADDING", (0, 0), (-1, -1), 7),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 7),
    ]))
    return table


def evidence_image(filename, caption, max_width=75 * mm, max_height=105 * mm):
    path = EVIDENCE / filename
    if not path.exists():
        return (P(f"No se encontró la captura {filename}.", "SmallX"), P(caption, "CaptionX"))
    from PIL import Image as PILImage
    with PILImage.open(path) as opened:
        width, height = opened.size
    scale = min(max_width / width, max_height / height, 1)
    visual = Image(str(path), width=width * scale, height=height * scale)
    return (visual, P(caption, "CaptionX"))


story = []

# Portada
story += [
    Spacer(1, 22 * mm),
    P("CAMPUSGPS", "Cover"),
    P("Laboratorio 13 · Localización geográfica", "SubCover"),
    Spacer(1, 4 * mm),
    P("Desarrollo, estructura, pruebas y reflexión", "SubCover"),
    Spacer(1, 14 * mm),
]
cover = [
    [P("ESTUDIANTE", "CellHeadX"), P("CURSO", "CellHeadX"), P("DOCENTE", "CellHeadX")],
    [P("Jesús Castillo Sumire"), P("Programación en Móviles<br/>Sección C24D"), P("Juan León S.")],
]
story += [make_table(cover, [55 * mm, 65 * mm, 50 * mm]), Spacer(1, 10 * mm)]
story += [
    P("Preparé la versión base del laboratorio en <b>main</b> y las ampliaciones en <b>mejora-ia</b>. Este informe explica cómo organicé la app, qué hace cada parte importante y qué pude comprobar en esta revisión.", "BodyX"),
    Spacer(1, 25 * mm),
    P("19 de noviembre de 2026", "SubCover"),
    PageBreak(),
]

# 1 alcance y versiones
story += [
    P("1. Qué resuelve CampusGPS", "SectionX"),
    P("La app toma la ubicación del dispositivo, la representa en el mapa y calcula qué punto de interés queda más cerca. La lista se ordena por distancia y muestra metros o kilómetros. Si falta permiso, el GPS está apagado o no hay Google Play Services, la app presenta un mensaje con una acción acorde al problema."),
    P("Los cinco sitios son datos ficticios de laboratorio. En la versión actual sus coordenadas se calculan como desplazamientos respecto al centro de TECSUP Santa Anita; por eso, los nombres sirven para probar el flujo, pero la ubicación de cada edificio todavía debe confirmarse con el plano que el estudiante marque.", "BodyX"),
    P("Dos versiones en el repositorio", "SubX"),
]
versions = [
    [P("Rama", "CellHeadX"), P("Qué contiene", "CellHeadX"), P("Verificación", "CellHeadX")],
    [P("main", "CellX"), P("Versión base: permiso, mapa, cinco puntos, distancias, selección, errores, README y reflexión.", "CellX"), P("Diez commits de los pasos del laboratorio; configuración de Google Maps fuera del repositorio.", "CellX")],
    [P("mejora-ia", "CellX"), P("Filtros, radios, ubicación actual, seguimiento, DataStore y estilo de mapa MapLibre/OpenFreeMap.", "CellX"), P("Compilación limpia correcta y cinco pruebas unitarias aprobadas. Falta repetir los recorridos visuales en un emulador funcional.", "CellX")],
]
story += [make_table(versions, [29 * mm, 82 * mm, 59 * mm]), Spacer(1, 3 * mm)]
story += [
    P("En mejora-ia, MapLibre dibuja el mapa y OpenFreeMap entrega estilos y mosaicos por Internet. La ubicación sigue viniendo de Fused Location Provider. Son servicios diferentes: cambiar el proveedor visual no reemplaza el permiso ni el GPS.", "BodyX"),
    P("La compilación limpia <b>clean testDebugUnitTest assembleDebug</b> terminó correctamente el 19 de noviembre. Pasaron cinco pruebas unitarias. El emulador Pixel 8 no llegó a iniciar porque el entorno informó que no estaba disponible la aceleración de hardware que necesita; por eso este documento no presenta los doce casos visuales como aprobados.", "BodyX"),
    PageBreak(),
]

# 2 estructura y flujo
story += [
    P("2. Estructura del proyecto", "SectionX"),
    P("Organicé el código por responsabilidad para poder seguir el recorrido desde la pantalla hasta el GPS y volver con un resultado. Estos son los paquetes principales que se encuentran dentro de app/src/main/java/com/tecsup/campusgps:")
]
tree = """com.tecsup.campusgps/
├── MainActivity.kt                 prepara Compose y MapLibre
├── model/Modelos.kt                posición, puntos y estados de pantalla
├── data/                           contratos y repositorios
│   ├── UbicacionRepositoryGoogle.kt GPS y actualizaciones
│   ├── LugaresRepositoryLocal.kt   cinco puntos de demostración
│   └── PreferenciasLugarRepositoryDataStore.kt último punto elegido
├── util/                           distancia y reglas sin interfaz
├── viewmodel/MapaViewModel.kt      búsqueda, orden, selección y estado
└── ui/                             pantalla, mapa y componentes visuales"""
story += [code_block(tree), Spacer(1, 3 * mm)]
story += [
    P("El orden de los datos es directo: la persona toca un control en PantallaMapa, el ViewModel llama a una interfaz de Repository, recibe el resultado y actualiza StateFlow. Compose observa ese estado y vuelve a dibujar lo necesario. Así el mapa y la lista no necesitan abrir el GPS por cuenta propia.", "BodyX"),
    P("Responsabilidad de cada capa", "SubX"),
]
layers = [
    [P("Capa", "CellHeadX"), P("Lo que hace en este proyecto", "CellHeadX")],
    [P("UI en Compose", "CellX"), P("Pide permisos, muestra el mapa y la lista, y envía acciones al ViewModel.", "CellX")],
    [P("MapaViewModel", "CellX"), P("Controla búsqueda, ubicación, errores, orden por distancia, filtros y selección.", "CellX")],
    [P("Repositories", "CellX"), P("Aíslan Google Play Services, los cinco datos locales y la preferencia DataStore.", "CellX")],
    [P("Utilidades", "CellX"), P("Calculan distancias y aplican reglas de categoría o radio sin dibujar vistas.", "CellX")],
]
story += [make_table(layers, [39 * mm, 131 * mm]), PageBreak()]

# 3 código ViewModel
story += [
    P("3. Cómo se obtiene la ubicación y se actualiza la lista", "SectionX"),
    P("El ViewModel consulta primero si el dispositivo tiene Google Play Services y si su ubicación está activa. Si algo falta, publica un estado de error. Si todo está listo, cambia a Buscando y lanza la petición a través de la interfaz del Repository.")
]
story += [code_block("""fun buscar() {
    if (!ubicacionRepo.playServicesDisponible()) {
        _estado.value = UbicacionUi.Error(
            \"Este dispositivo no tiene Google Play Services.\",
            TipoError.SIN_PLAY_SERVICES
        )
        return
    }

    _estado.value = UbicacionUi.Buscando
    viewModelScope.launch {
        ubicacionRepo.obtenerPosicion()
            .onSuccess {
                actualizarDistancias(it)
                _estado.value = UbicacionUi.Lista(it)
            }
            .onFailure { manejarError(it) }
    }
}""")]
story += [
    Spacer(1, 3 * mm),
    P("El bloque evita que la pantalla tenga que esperar directamente al proveedor de ubicación. Cuando recibe una posición, llama a actualizarDistancias y luego publica Lista. Si el proceso falla, el error se convierte en un estado que la interfaz puede explicar.", "BodyX"),
    P("Los estados hacen explícito qué está pasando", "SubX"),
]
story += [code_block("""sealed interface UbicacionUi {
    data object Inicial : UbicacionUi
    data object Buscando : UbicacionUi
    data class Lista(val posicion: Posicion) : UbicacionUi
    data class Error(val mensaje: String, val tipo: TipoError) : UbicacionUi
}""")]
story += [
    Spacer(1, 3 * mm),
    P("Modelos.kt define también Posicion, Lugar y LugarConDistancia. En la lista, metros puede estar vacío antes de la primera búsqueda; por eso la app puede mostrar Sin calcular sin inventar un valor.", "BodyX"),
    P("La distancia se calcula fuera de Compose", "SubX"),
]
story += [code_block("""fun distanciaMetros(desde: Posicion, hasta: Lugar): Float {
    val resultado = FloatArray(1)
    Location.distanceBetween(
        desde.latitud, desde.longitud,
        hasta.latitud, hasta.longitud,
        resultado
    )
    return resultado[0]
}""")]
story += [P("Location.distanceBetween recibe latitud y longitud en ese orden y deja la distancia en metros en el arreglo. El ViewModel usa el resultado para ordenar; la pantalla solo le da formato y lo muestra.", "BodyX"), PageBreak()]

# 4 Repository y seguimiento
story += [
    P("4. Repository de ubicación y seguimiento", "SectionX"),
    P("UbicacionRepositoryGoogle encapsula FusedLocationProviderClient. El ViewModel depende de UbicacionRepository, no de Context ni de las clases del SDK. Esta separación permite cambiar la fuente de datos o simularla en una prueba sin rehacer la pantalla.")
]
story += [code_block("""override suspend fun obtenerPosicion(): Result<Posicion> = runCatching {
    val location = client.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        CancellationTokenSource().token
    ).await() ?: error(\"No se pudo determinar la ubicación\")

    Posicion(location.latitude, location.longitude)
}""")]
story += [
    Spacer(1, 3 * mm),
    P("Para el seguimiento continuo, el Repository convierte las actualizaciones del SDK en un Flow. Cuando se cancela el flujo, awaitClose elimina el callback y deja de solicitar posiciones.", "BodyX"),
]
story += [code_block("""override fun seguirUbicacion(): Flow<Posicion> = callbackFlow {
    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let {
                trySend(Posicion(it.latitude, it.longitude))
            }
        }
    }
    client.requestLocationUpdates(request, callback, Looper.getMainLooper())
    awaitClose { client.removeLocationUpdates(callback) }
}""")]
story += [
    Spacer(1, 3 * mm),
    P("MapaViewModel recoge el Flow mientras el seguimiento está activo. El botón Iniciar/Detener controla el Job; cuando se detiene, se cancela la colección y se limpia la actualización asociada.", "BodyX"),
    P("Preferencia local", "SubX"),
    P("PreferenciasLugarRepositoryDataStore guarda el identificador del último lugar seleccionado. No guarda la ubicación actual del dispositivo ni el objeto de la tarjeta; solo recuerda qué punto del campus se eligió.", "BodyX"),
    PageBreak(),
]

# 5 mapa
story += [
    P("5. Qué dibuja el mapa", "SectionX"),
    P("MapaUbicacion crea MapView de MapLibre dentro de Compose. Al cargar el estilo de OpenFreeMap, dibuja el pin de TECSUP y los cinco puntos de prueba. Si el usuario ya obtuvo su ubicación, agrega el marcador azul. El lugar más cercano se distingue en verde; los demás quedan azules.", "BodyX"),
    P("Cuando hay una posición y una selección, el código construye una línea recta entre los dos puntos. No es una ruta por veredas ni indica por dónde caminar. Al elegir un radio, la app dibuja un círculo aproximado alrededor de la ubicación recibida.", "BodyX"),
]
story += [code_block("""currentMap.addMarker(
    MarkerOptions()
        .position(LatLng(item.lugar.latitud, item.lugar.longitud))
        .title(if (nearest) \"Más cercano · ${item.lugar.nombre}\"
               else item.lugar.nombre)
        .snippet(item.lugar.categoria)
        .icon(createMarkerIcon(context, markerColor))
)""")]
story += [
    Spacer(1, 3 * mm),
    P("El estilo claro usa https://tiles.openfreemap.org/styles/positron y el oscuro usa https://tiles.openfreemap.org/styles/dark. El mapa necesita Internet para descargarlos. El servicio público puede cambiar sus condiciones o disponibilidad; no se añadió una clave de Google Maps a esta rama.", "BodyX"),
    P("Punto central y ubicaciones de prueba", "SubX"),
    P("El centro del mapa usa las coordenadas de referencia de TECSUP Lima: -12.04398, -76.95291. Los cinco sitios usan valores próximos calculados a partir de ese centro. Para ubicar realmente Biblioteca Central, Cafetería Campus, Laboratorio de Cómputo, Auditorio Principal y Estacionamiento Norte, hace falta contrastarlos con la imagen satelital anotada; no asumí que los desplazamientos actuales coincidan con cada edificio.", "BodyX"),
    P("Ruta de código: app/src/main/java/com/tecsup/campusgps/ui/MapaUbicacion.kt. La inicialización de MapLibre se realiza en MainActivity.kt; las coordenadas y los modelos están en model/Modelos.kt.", "SmallX"),
    PageBreak(),
]

# 6 features and tests
story += [
    P("6. Ampliaciones de mejora-ia", "SectionX"),
]
features = [
    [P("Ampliación", "CellHeadX"), P("Qué hace", "CellHeadX"), P("Dónde se conecta", "CellHeadX")],
    [P("Categorías", "CellX"), P("Muestra todos los lugares o filtra por Estudio, Comida, Eventos y Servicios.", "CellX"), P("ViewModel + chips Compose", "CellX")],
    [P("Radio", "CellX"), P("Permite elegir 100, 200 o 500 m; distingue puntos dentro o fuera.", "CellX"), P("Regla utilitaria + capa de mapa", "CellX")],
    [P("Ubicación actual", "CellX"), P("Añade el marcador del dispositivo y permite recentrar la cámara.", "CellX"), P("Repository GPS + UI", "CellX")],
    [P("Seguimiento", "CellX"), P("Actualiza la lista mientras el usuario se mueve y se puede detener.", "CellX"), P("Flow + ViewModel", "CellX")],
    [P("Último punto", "CellX"), P("Recupera el identificador del punto seleccionado.", "CellX"), P("DataStore + Repository", "CellX")],
    [P("Mapa oscuro", "CellX"), P("Alterna los estilos claro y oscuro del proveedor elegido.", "CellX"), P("MapLibre + OpenFreeMap", "CellX")],
]
story += [make_table(features, [32 * mm, 89 * mm, 49 * mm]), Spacer(1, 4 * mm)]
story += [
    P("Pruebas unitarias", "SubX"),
    P("ReglasCampusTest comprueba cinco situaciones: sin filtro devuelve todo; la categoría devuelve coincidencias; una distancia dentro del límite se marca correctamente; una distancia fuera no se marca; una distancia aún vacía no se considera dentro. Las cinco pasaron en la compilación limpia.", "BodyX"),
    P("Commits de la fase base", "SubX"),
    P("La rama main conserva los diez commits destinados a los pasos de la guía, desde el proyecto inicial hasta README y pruebas. Además hubo commits de ajuste de compilación y cierre de la base. La rama mejora-ia parte de esa versión y mantiene por separado las ampliaciones y la migración del mapa.", "BodyX"),
    P("La clave de Maps no está versionada. En main la configuración original se apoya en local.properties, que queda fuera de Git; en mejora-ia se usa MapLibre/OpenFreeMap y no se configura una clave de Maps.", "BodyX"),
    PageBreak(),
]

# 7 evidence
story += [
    P("7. Capturas de la aplicación", "SectionX"),
    P("Las imágenes siguientes sí son capturas de CampusGPS, pero se tomaron antes de cambiar a MapLibre. Las incluyo para explicar los controles y estados que se observaron. En la captura del mapa no se ven mosaicos; no la presento como prueba de carga del proveedor actual.", "BodyX"),
]
image_pairs = [
    (("01_permiso.png", "1. Android solicita permiso de ubicación. El diálogo pertenece al sistema; la app solo inicia la solicitud."),
     ("02_ubicacion_mapa_sin_clave.png", "2. La app muestra la posición y distancias calculadas. El mapa no cargó mosaicos en esta ejecución anterior.")),
    (("04_error_permiso.png", "3. Al negar el permiso, la app sigue abierta y ofrece abrir los ajustes."),
     ("06_filtro_comida.png", "4. El filtro Comida conserva en la lista los lugares de esa categoría.")),
    (("07_radio_100m.png", "5. El selector de proximidad permite consultar el radio de 100 metros."),
     ("08_seguimiento_en_vivo.png", "6. Se observó el seguimiento activado y el cambio de distancias con una ubicación distinta.")),
    (("10_gps_desactivado.png", "7. Con la ubicación desactivada, la app muestra un aviso y permite reintentar."), None),
]
for pair in image_pairs:
    cells = []
    captions = []
    for item in pair:
        if item:
            visual, caption = evidence_image(item[0], item[1], 55 * mm, 88 * mm)
            cells.append(visual)
            captions.append(caption)
        else:
            cells.append(Spacer(1, 1))
            captions.append(Spacer(1, 1))
    t = Table([cells, captions], colWidths=[82 * mm, 82 * mm], hAlign="CENTER")
    t.setStyle(TableStyle([
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 3),
        ("RIGHTPADDING", (0, 0), (-1, -1), 3),
        ("TOPPADDING", (0, 0), (-1, -1), 3),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
    ]))
    story += [t, Spacer(1, 3 * mm), PageBreak()]

# 8 acceptance tests
story += [
    P("8. Doce pruebas de aceptación", "SectionX"),
    P("La guía pide recorrer estos casos en el emulador. Las capturas previas aportan evidencia histórica de algunas pantallas; los casos visuales deben repetirse después de la migración. El estado distingue lo visto, lo revisado en código y lo que no se pudo comprobar en este equipo.", "BodyX"),
]
acceptance = [
    ("1", "Solicitar permiso", "Captura previa; repetir tras migración"),
    ("2", "Aceptar y mostrar ubicación", "No verificado en mapa actual"),
    ("3", "Negar permiso y abrir ajustes", "Captura previa; repetir tras migración"),
    ("4", "Mostrar cinco puntos en mapa", "Código presente; sin validación visual actual"),
    ("5", "Tocar marcador y ver nombre/categoría", "Código presente; sin validación visual actual"),
    ("6", "Lista ordenada con distancias", "Captura previa; repetir tras migración"),
    ("7", "Seleccionar tarjeta y centrar cámara", "Código presente; sin validación visual actual"),
    ("8", "Centrar en el lugar más cercano", "Código presente; sin validación visual actual"),
    ("9", "Cambiar ubicación y recalcular", "Captura previa; repetir tras migración"),
    ("10", "Apagar GPS y reintentar", "Captura previa; repetir tras migración"),
    ("11", "Rotar y conservar estado", "Sin evidencia válida; pendiente"),
    ("12", "Revisar que no se publique una clave", "Verificado por inspección de Git")
]
rows = [[P("N.º", "CellHeadX"), P("Caso", "CellHeadX"), P("Resultado registrado", "CellHeadX")]]
for number, case, result in acceptance:
    rows.append([P(number, "CellX"), P(case, "CellX"), P(result, "CellX")])
story += [make_table(rows, [12 * mm, 69 * mm, 89 * mm]), Spacer(1, 3 * mm)]
story += [
    P("No se registran los doce recorridos como aprobados. La compilación y las reglas unitarias sí están comprobadas; para cerrar las pruebas manuales hay que iniciar el emulador con aceleración habilitada o conectar un teléfono, repetir los doce casos y reemplazar las capturas históricas por imágenes actuales.", "SmallX"),
    P("Resultado del emulador", "SubX"),
    P("Al intentar iniciar Pixel 8, Android Emulator informó: \"x86_64 emulation currently requires hardware acceleration\". El intento con aceleración de software tampoco dejó el dispositivo en estado disponible para ADB. Por eso no instalé ni ejecuté la versión actual en una sesión funcional de emulador.", "BodyX"),
    PageBreak(),
]

# 9 preguntas
story += [P("9. Respuestas para la sustentación", "SectionX")]
qa = [
    ("¿Por qué FusedLocationProviderClient está en Repository y no en ViewModel?", "Porque es una integración concreta de Android que necesita Context y permisos. El Repository la encapsula y el ViewModel trabaja con una interfaz. Así puedo explicar y probar la lógica sin acoplarla al SDK."),
    ("¿Qué diferencia hay entre el permiso de ubicación y el mapa?", "El permiso permite leer la posición del dispositivo. El proveedor del mapa dibuja el fondo y sus mosaicos. En esta versión MapLibre y OpenFreeMap hacen esa parte; la posición todavía la entrega Google Play Services."),
    ("¿Por qué una clave no debe ir en el código? ¿Qué haría si se filtrara?", "Una clave en un repositorio público puede copiarse y usarse desde otra app. La revocaría, generaría una nueva y comprobaría el historial y las restricciones. La rama mejora-ia no usa clave de Google Maps."),
    ("¿Dónde calculo la distancia y por qué no en un Composable?", "La función distanciaMetros está en util y el ViewModel la usa para construir la lista ordenada. Un Composable debe presentar el estado, no calcular reglas, para que la pantalla siga siendo fácil de cambiar y la regla se pueda probar sola."),
    ("¿Qué ocurre si se niega el permiso más de una vez?", "Android puede dejar de mostrar la solicitud. La app representa el error y ofrece abrir los ajustes de CampusGPS; la persona decide si quiere conceder el permiso allí."),
]
for question, answer in qa:
    story += [P(question, "SubX"), P(answer, "BodyX")]
story += [PageBreak()]

# 10 conclusión
story += [
    P("10. Conclusión", "SectionX"),
    P("Al desarrollar CampusGPS entendí que una app de ubicación no consiste solo en colocar un pin sobre un mapa. Primero hay que pedir permiso, esperar una respuesta del sistema y contemplar que el usuario puede negarlo o tener el GPS apagado. Después, el dato obtenido tiene que recorrer la arquitectura sin mezclar la interfaz con los servicios de Android.", "BodyX"),
    P("En la versión base separé la pantalla, el ViewModel y los repositorios. El ViewModel transforma una posición en distancias ordenadas y estados que Compose puede mostrar. En mejora-ia amplié ese recorrido con filtros, radios de proximidad, seguimiento en vivo y una preferencia local. También cambié la representación del mapa a MapLibre con estilos de OpenFreeMap, manteniendo Fused Location Provider para obtener la posición.", "BodyX"),
    P("La compilación limpia de la rama mejorada terminó y pasaron las cinco pruebas unitarias de filtro y radio. Eso confirma la construcción del APK y las reglas de cálculo que cubren esas pruebas. No confirma todavía que el mapa migrado cargue correctamente en pantalla, que sus marcadores respondan al toque o que la rotación conserve todos los datos: el emulador no arrancó por la falta de aceleración de hardware y las capturas disponibles son anteriores al cambio de proveedor.", "BodyX"),
    P("También debo distinguir entre el centro real del campus y los puntos de prueba. El mapa se centra en TECSUP Santa Anita, pero la biblioteca, la cafetería, el laboratorio, el auditorio y el estacionamiento usan coordenadas de demostración. Antes de presentarlos como ubicaciones exactas, tengo que contrastarlos con el plano satelital y corregir sus coordenadas según las marcas del campus.", "BodyX"),
    P("Con esas comprobaciones pendientes, la entrega deja una base que compila, una separación clara de responsabilidades y pruebas unitarias que protegen las reglas principales. El siguiente paso para cerrar el laboratorio es repetir los doce casos en un emulador con aceleración habilitada o en un teléfono Android, guardar capturas nuevas del mapa actual y confirmar las coordenadas interiores. Así podré sustentar no solo cómo está organizada la app, sino también qué resultados observé al ejecutarla.", "BodyX"),
    Spacer(1, 7 * mm),
    P("Informe individual · Semana 13 · TECSUP", "SubCover"),
]


def footer(canvas, doc):
    canvas.saveState()
    page_width, _ = A4
    canvas.setStrokeColor(colors.HexColor("#D6E0E8"))
    canvas.line(19 * mm, 15 * mm, page_width - 19 * mm, 15 * mm)
    canvas.setFont("Arial", 8)
    canvas.setFillColor(MUTED)
    canvas.drawString(19 * mm, 10 * mm, "CampusGPS · Laboratorio 13")
    canvas.drawRightString(page_width - 19 * mm, 10 * mm, f"Página {doc.page}")
    canvas.restoreState()


document = SimpleDocTemplate(
    str(OUTPUT),
    pagesize=A4,
    rightMargin=19 * mm,
    leftMargin=19 * mm,
    topMargin=17 * mm,
    bottomMargin=21 * mm,
    title="CampusGPS - Laboratorio 13",
    author="Jesús Castillo Sumire",
)
document.build(story, onFirstPage=footer, onLaterPages=footer)
print(OUTPUT)
