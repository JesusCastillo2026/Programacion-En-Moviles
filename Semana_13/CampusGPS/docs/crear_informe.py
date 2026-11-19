from pathlib import Path
from datetime import date
from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, Image, KeepTogether
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from PIL import Image as PILImage

root = Path(r"C:\Users\BossC\Documents\Codex\2026-09-23\est\work\Programacion-En-Moviles\Semana_13\CampusGPS")
ev = root / "docs" / "evidencias"
out = root / "docs" / "Informe_Laboratorio_13_CampusGPS.pdf"
pdfmetrics.registerFont(TTFont("Arial", r"C:\Windows\Fonts\arial.ttf"))
pdfmetrics.registerFont(TTFont("Arial-Bold", r"C:\Windows\Fonts\arialbd.ttf"))
navy = colors.HexColor("#183153")
blue = colors.HexColor("#2E6F9E")
soft = colors.HexColor("#EAF1F6")
muted = colors.HexColor("#526170")
styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name="Cover", fontName="Arial-Bold", fontSize=27, leading=33, textColor=navy, alignment=TA_CENTER, spaceAfter=8))
styles.add(ParagraphStyle(name="SubCover", fontName="Arial", fontSize=13, leading=19, textColor=muted, alignment=TA_CENTER, spaceAfter=7))
styles.add(ParagraphStyle(name="SectionX", fontName="Arial-Bold", fontSize=17, leading=21, textColor=navy, spaceBefore=8, spaceAfter=8))
styles.add(ParagraphStyle(name="SubX", fontName="Arial-Bold", fontSize=11, leading=15, textColor=blue, spaceBefore=5, spaceAfter=4))
styles.add(ParagraphStyle(name="BodyX", fontName="Arial", fontSize=9.3, leading=14, textColor=colors.HexColor("#263442"), spaceAfter=6))
styles.add(ParagraphStyle(name="SmallX", fontName="Arial", fontSize=8, leading=11, textColor=muted, spaceAfter=4))
styles.add(ParagraphStyle(name="CellX", fontName="Arial", fontSize=7.2, leading=9.3, textColor=colors.HexColor("#263442")))
styles.add(ParagraphStyle(name="CellHeadX", fontName="Arial-Bold", fontSize=7.4, leading=9.2, textColor=colors.white))
styles.add(ParagraphStyle(name="CaptionX", fontName="Arial", fontSize=8, leading=11, textColor=muted, alignment=TA_CENTER, spaceBefore=4, spaceAfter=8))
P=lambda s,style="BodyX": Paragraph(s,styles[style])
story=[]
story += [Spacer(1,23*mm),P("CAMPUSGPS","Cover"),P("Laboratorio 13 · Localización geográfica","SubCover"),Spacer(1,5*mm),P("Informe de desarrollo, pruebas y mejoras","SubCover"),Spacer(1,16*mm)]
cover_data=[[P("ESTUDIANTE","CellHeadX"),P("CURSO","CellHeadX"),P("DOCENTE","CellHeadX")],[P("Jesús Castillo Sumire","BodyX"),P("Programación en Móviles<br/>Sección C24D","BodyX"),P("Juan León S.","BodyX")]]
t=Table(cover_data,colWidths=[55*mm,65*mm,50*mm],rowHeights=[9*mm,20*mm]); t.setStyle(TableStyle([("BACKGROUND",(0,0),(-1,0),navy),("BACKGROUND",(0,1),(-1,-1),soft),("BOX",(0,0),(-1,-1),.5,colors.HexColor("#C8D5E0")),("INNERGRID",(0,0),(-1,-1),.4,colors.white),("VALIGN",(0,0),(-1,-1),"MIDDLE"),("LEFTPADDING",(0,0),(-1,-1),8)])); story += [t,Spacer(1,12*mm),P("Entrega individual con dos versiones del mismo proyecto: la implementación base en <b>main</b> y la ampliación en <b>mejora-ia</b>.","BodyX"),Spacer(1,30*mm),P("19 de noviembre de 2026","SubCover"),PageBreak()]

story += [P("1. Propósito y alcance","SectionX"),P("CampusGPS obtiene la ubicación del dispositivo, la presenta en un mapa y calcula cuál de cinco puntos ficticios del campus queda más cerca. La interfaz muestra los puntos ordenados por distancia y contempla situaciones como permiso denegado y GPS desactivado.")]
summary=[[P("Versión","CellHeadX"),P("Contenido","CellHeadX"),P("Rama","CellHeadX")],[P("Base del laboratorio","CellX"),P("Permisos, Google Maps, ubicación, cinco lugares, distancias, selección, errores, README y pruebas de aceptación.","CellX"),P("main","CellX")],[P("Ampliación","CellX"),P("Seis mejoras: categorías, radios de cercanía, punto azul, seguimiento en vivo, último lugar seleccionado y tema oscuro del mapa.","CellX"),P("mejora-ia","CellX")]]
t=Table(summary,colWidths=[34*mm,112*mm,24*mm],repeatRows=1); t.setStyle(TableStyle([("BACKGROUND",(0,0),(-1,0),navy),("BACKGROUND",(0,1),(-1,-1),colors.HexColor("#F4F7FA")),("GRID",(0,0),(-1,-1),.4,colors.HexColor("#C8D5E0")),("VALIGN",(0,0),(-1,-1),"TOP"),("LEFTPADDING",(0,0),(-1,-1),6),("RIGHTPADDING",(0,0),(-1,-1),6),("TOPPADDING",(0,0),(-1,-1),6),("BOTTOMPADDING",(0,0),(-1,-1),6)])); story += [t,Spacer(1,4*mm),P("El código usa Kotlin, Jetpack Compose, Material 3 y Maps Compose. La estructura separa la interfaz, el ViewModel, las interfaces de Repository, las implementaciones de ubicación y los datos locales.")]
story += [P("2. Estructura y flujo de la aplicación","SectionX"),P("La pantalla <b>PantallaMapa</b> recoge acciones y observa StateFlow. El <b>MapaViewModel</b> coordina el estado y las operaciones a través de interfaces. <b>UbicacionRepositoryGoogle</b> integra FusedLocationProviderClient; <b>LugaresRepositoryLocal</b> entrega los cinco sitios de prueba. Las funciones utilitarias calculan y formatean distancias fuera de Compose.")]
flow=[[P("Capa","CellHeadX"),P("Responsabilidad","CellHeadX")],[P("Pantalla Compose","CellX"),P("Solicita permiso, dibuja mapa y lista, presenta estados observables.","CellX")],[P("MapaViewModel","CellX"),P("Solicita posición a Repository, calcula y ordena distancias, publica estados.","CellX")],[P("Repositories","CellX"),P("Acceso a ubicación Google, lugares ficticios y, en mejora-ia, preferencia del último lugar.","CellX")],[P("Utilidades","CellX"),P("Cálculo de distancia, formato y reglas puras de filtros/proximidad.","CellX")]]
t=Table(flow,colWidths=[42*mm,128*mm],repeatRows=1);t.setStyle(TableStyle([("BACKGROUND",(0,0),(-1,0),navy),("BACKGROUND",(0,1),(-1,-1),colors.white),("GRID",(0,0),(-1,-1),.4,colors.HexColor("#C8D5E0")),("VALIGN",(0,0),(-1,-1),"TOP"),("LEFTPADDING",(0,0),(-1,-1),6),("TOPPADDING",(0,0),(-1,-1),5),("BOTTOMPADDING",(0,0),(-1,-1),5)])); story += [t,Spacer(1,3*mm),P("Flujo resumido: toque → ViewModel → interfaz de Repository → servicio de ubicación → posición → distancia ordenada → StateFlow → interfaz actualizada.","BodyX")]
story += [P("3. Decisiones técnicas importantes","SectionX"),P("Permisos y clave de Maps", "SubX"),P("Android declara permiso aproximado y preciso y los solicita en tiempo de ejecución. La clave de Maps es distinta: autoriza el SDK a cargar mosaicos. Se lee desde <b>local.properties</b> y llega al Manifest como placeholder; local.properties está excluido de Git. También se declaró permiso de Internet para que el SDK pueda cargar el contenido del mapa."),P("Ubicación y cálculo", "SubX"),P("El ViewModel conserva estados inicial, buscando, lista y error. Cuando llega una Posicion, calcula la distancia respecto de cada Lugar, ordena el resultado de menor a mayor y expone el lugar más cercano. Las pantallas no calculan distancias ni llaman directamente a FusedLocationProviderClient."),P("Manejo de fallos", "SubX"),P("La app presenta acciones útiles: abrir ajustes cuando el permiso se deniega y reintentar si el GPS está apagado. También detecta la falta de Google Play Services antes de iniciar la búsqueda."),PageBreak()]

story += [P("4. Pruebas de aceptación","SectionX"),P("Se verificó la compilación y el conjunto de cinco pruebas unitarias del proyecto. Los doce casos pedidos por la guía se ejecutaron o se clasificaron según la evidencia disponible; los que dependen de una clave real de Maps no se contabilizan como aprobados.")]
rows=[[P("Nº","CellHeadX"),P("Caso","CellHeadX"),P("Resultado","CellHeadX")]]
checks=[
("1","Solicitud de permiso del sistema","Aprobado"),
("2","Aceptar permiso y mostrar ubicación","Parcial: coordenadas visibles; mapa/marker bloqueados por clave"),
("3","Denegar permiso","Parcial: error y botón visibles; ajustes no pulsado"),
("4","Cinco marcadores cerca del punto base","Pendiente: requiere clave Maps"),
("5","Detalle del marcador tocado","Pendiente: requiere clave Maps"),
("6","Lista ordenada con distancias","Aprobado"),
("7","Seleccionar tarjeta y mover mapa","Parcial: cámara requiere clave Maps"),
("8","Ir al punto más cercano","Parcial: destino calculado; cámara requiere clave"),
("9","Cambiar ubicación y actualizar distancias","Parcial: coordenadas y orden actualizados"),
("10","GPS desactivado","Aprobado"),
("11","Rotar y conservar posición/lista","Parcial: se rotó; el estado se reinició"),
("12","Clave fuera de Git y local.properties ignorado","Aprobado")]
for n,case,result in checks: rows.append([P(n,"CellX"),P(case,"CellX"),P(result,"CellX")])
t=Table(rows,colWidths=[10*mm,78*mm,82*mm],repeatRows=1);t.setStyle(TableStyle([("BACKGROUND",(0,0),(-1,0),navy),("GRID",(0,0),(-1,-1),.35,colors.HexColor("#CBD5DE")),("ROWBACKGROUNDS",(0,1),(-1,-1),[colors.white,colors.HexColor("#F4F7FA")]),("VALIGN",(0,0),(-1,-1),"TOP"),("LEFTPADDING",(0,0),(-1,-1),5),("RIGHTPADDING",(0,0),(-1,-1),5),("TOPPADDING",(0,0),(-1,-1),4),("BOTTOMPADDING",(0,0),(-1,-1),4)]));story += [t,Spacer(1,3*mm),P("Estado resumido: 3 aprobados, 7 parciales y 2 pendientes. Las limitaciones vienen de no tener configurada MAPS_API_KEY y de no persistir posición/lista al rotar. La guía solicita que los doce casos pasen antes de cerrar la fase; este registro no declara completa esa condición.","SmallX"),P("Validación automatizada", "SubX"),P("La ejecución final de `:app:testDebugUnitTest :app:assembleDebug` terminó correctamente en Gradle. Pasaron cinco pruebas unitarias de filtrado por categoría y pertenencia al radio. El emulador Pixel 8 API 37.2 permitió observar permiso, coordenadas, distancias, filtros, seguimiento, GPS apagado y rotación.")]
story += [P("5. Evidencias del emulador","SectionX"),P("Capturas tomadas en el emulador Pixel 8 API 37.2. Las imágenes de mapa muestran mosaicos en blanco porque no se configuró una clave de Google Maps; se conserva la captura tal cual, sin sustituir ni inventar marcadores.")]
imgs=[("01_permiso.png","Solicitud de permiso nativa de Android."),("02_ubicacion_mapa_sin_clave.png","Ubicación obtenida y distancias ordenadas; mosaicos no disponibles."),("04_error_permiso.png","Permiso denegado con acción para abrir ajustes."),("06_filtro_comida.png","Filtro de categoría Comida en la lista."),("07_radio_100m.png","Radio de 100 m con elementos dentro y fuera."),("08_seguimiento_en_vivo.png","Seguimiento en vivo con coordenadas actualizadas."),("10_gps_desactivado.png","GPS apagado con mensaje y acción Reintentar.")]
for i,(file,cap) in enumerate(imgs):
    imgpath=ev/file
    if not imgpath.exists(): continue
    with PILImage.open(imgpath) as im: w,h=im.size
    maxw=76*mm; maxh=110*mm
    scale=min(maxw/w,maxh/h,1.0)
    img=Image(str(imgpath),width=w*scale,height=h*scale)
    story.append(KeepTogether([img,P(cap,"CaptionX")]))
    if i in (1,3,5): story.append(PageBreak())
story += [PageBreak(),P("6. Rama mejora-ia: seis ampliaciones","SectionX")]
features=[("Filtro por categoría","El ViewModel filtra puntos por Estudio, Comida, Eventos o Servicios; Compose representa la selección."),("Radios de cercanía","Círculo en el mapa y marca Dentro/Fuera para radios de 100, 200 o 500 m."),("Punto azul nativo","La capa y el control de ubicación se activan únicamente si el usuario concedió permiso."),("Seguimiento en vivo","El Repository convierte actualizaciones a Flow; el botón inicia/detiene y se limpia el callback al cancelar."),("Último lugar seleccionado","DataStore conserva el identificador del lugar y lo recupera mediante Repository."),("Mapa oscuro","El mapa aplica un estilo local seleccionable sin cambiar los colores globales de la aplicación.")]
for title,body in features: story.append(KeepTogether([P(title,"SubX"),P(body)]))
story += [P("Las reglas del filtro y el radio tienen cinco pruebas unitarias: lista completa sin filtro, coincidencia por categoría, punto dentro, punto fuera y distancia todavía no calculada. Se documentaron los prompts, ajustes y criterios en PROMPTS.md.","BodyX"),P("7. Commits y repositorio","SectionX"),P("La rama main contiene los diez commits de los pasos de la guía, además de un ajuste de compilación y documentación de la semana. La rama mejora-ia deriva de esa base y agrega commits separados para las seis ampliaciones, la verificación visual y la documentación. El repositorio conserva las dos ramas para poder cambiar entre la entrega base y la ampliada."),P("La clave MAPS_API_KEY no se incluyó en Git. La persona que ejecute el proyecto debe crear local.properties en la carpeta CampusGPS y agregar allí su clave restringida para com.tecsup.campusgps y la huella SHA-1 de su firma de depuración.")]
story += [P("8. Respuestas de reflexión","SectionX"),P("¿Por qué FusedLocationProviderClient está en Repository y no en ViewModel?","SubX"),P("Porque es una integración externa de Android. El Repository la encapsula tras una interfaz y permite que el ViewModel coordine sin depender de Context o de clases concretas del SDK."),P("¿Qué diferencia existe entre permiso y clave?","SubX"),P("El permiso autoriza acceso a la posición del dispositivo. La clave autoriza el uso del servicio de mapas. Sin permiso no se obtiene la ubicación; sin una clave válida el mapa no carga mosaicos."),P("¿Por qué guardar la clave localmente?","SubX"),P("Así no se publica con el código fuente. Si se expone, debe revocarse y reemplazarse desde Google Cloud."),P("¿Dónde se calcula la distancia?","SubX"),P("En la capa de lógica del ViewModel mediante una función utilitaria; así la interfaz solo presenta información."),P("¿Qué ocurre si el usuario niega el permiso?","SubX"),P("La app sigue abierta, publica un estado de error y ofrece abrir los ajustes para que la persona pueda concederlo después."),PageBreak()]
story += [P("9. Conclusión","SectionX"),P("CampusGPS reúne el flujo principal de ubicación, el cálculo de cercanía, la lista ordenada y la atención de varios errores dentro de la arquitectura MVVM + Repository indicada. La rama mejora-ia amplía la interacción con filtros, radios de proximidad, seguimiento y preferencias sin mover el acceso a servicios directamente a la pantalla."),P("La compilación y las cinco pruebas unitarias pasan. La entrega conserva la evaluación de aceptación como evidencia real: tres casos están aprobados, siete parciales y dos pendientes. Para completar los casos visuales del mapa se debe configurar una clave restringida de Maps en local.properties y volver a ejecutar las pruebas pendientes, además de decidir si se implementará persistencia de estado ante la rotación.","BodyX"),Spacer(1,8*mm),P("Documentación del trabajo individual · Semana 13 · TECSUP","SubCover")]

def footer(canvas,doc):
    canvas.saveState(); w,h=A4
    canvas.setStrokeColor(colors.HexColor("#D6E0E8"));canvas.line(19*mm,15*mm,w-19*mm,15*mm)
    canvas.setFont("Arial",8);canvas.setFillColor(muted);canvas.drawString(19*mm,10*mm,"CampusGPS · Laboratorio 13")
    canvas.drawRightString(w-19*mm,10*mm,f"Página {doc.page}")
    canvas.restoreState()
doc=SimpleDocTemplate(str(out),pagesize=A4,rightMargin=19*mm,leftMargin=19*mm,topMargin=17*mm,bottomMargin=21*mm,title="CampusGPS - Laboratorio 13",author="Jesús Castillo Sumire")
doc.build(story,onFirstPage=footer,onLaterPages=footer)
print(out)





