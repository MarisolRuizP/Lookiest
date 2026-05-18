package ruiz.marisol.lookiest.ui.theme.screens
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.navigation.Screen
import ruiz.marisol.lookiest.ui.theme.Amarillo
import ruiz.marisol.lookiest.ui.theme.Rosa
import ruiz.marisol.lookiest.ui.theme.components.ConfirmacionDialog
import ruiz.marisol.lookiest.ui.theme.components.EstadisticaRow
import ruiz.marisol.lookiest.ui.theme.components.InfoChip
import ruiz.marisol.lookiest.ui.theme.components.LookiestBottomBar
import ruiz.marisol.lookiest.ui.theme.components.LookiestTopBar
import ruiz.marisol.lookiest.viewModel.ClosetViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetallesPrendaScreen(
    prenda: PrendaRopa,
    viewModel: ClosetViewModel,
    onEditar: () -> Unit = {},
    onEliminarConfirmado: () -> Unit = {},
    navController: NavController
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    // los cosos de usos pq estaban harcodeadas jej
    val usos by viewModel.usos.collectAsState()
    val usosDeEstaPrenda = usos.filter { it.oufitId == prenda.id }
    val totalUsos = usosDeEstaPrenda.size

    val promedioMensual = if (usosDeEstaPrenda.isEmpty()) 0 else {
        try {
            val fechas = usosDeEstaPrenda.map { java.time.LocalDate.parse(it.fecha) }.sorted()
            val primera = fechas.first()
            val hoy = java.time.LocalDate.now()
            val meses = java.time.temporal.ChronoUnit.MONTHS.between(
                primera.withDayOfMonth(1),
                hoy.withDayOfMonth(1)
            ) + 1
            (totalUsos / meses.toDouble()).toInt().coerceAtLeast(1)
        } catch (e: Exception) {
            1
        }
    }

    if (mostrarDialogoEliminar) {
        ConfirmacionDialog(
            mensaje = "¿Deseas eliminar esta prenda?",
            onCancelar = { mostrarDialogoEliminar = false },
            onConfirmar = {
                mostrarDialogoEliminar = false
                viewModel.eliminarPrenda(prenda)
                onEliminarConfirmado()
            }
        )
    }

    Scaffold(
        topBar = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(selected = 2, navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Detalles de la Prenda",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (!prenda.imagen.isNullOrEmpty()) {
                        AsyncImage(
                            model = prenda.imagen,
                            contentDescription = prenda.nombre,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(30.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checkroom,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.favorito(prenda) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = if (prenda.favorito) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Favorito",
                            tint = Amarillo,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            Text(
                text = prenda.nombre,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(5.dp))
            if (prenda.tienda.isNotBlank()) {
                Text(
                    text = prenda.tienda,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(Modifier.height(5.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(texto = prenda.talla, bgColor = MaterialTheme.colorScheme.surface, textColor = Rosa)
                InfoChip(
                    texto = if (prenda.estampado) "Estampado" else "Sin Estampado",
                    bgColor = MaterialTheme.colorScheme.surface,
                    textColor = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Color",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            color = parseColor(prenda.color),   // Función para convertir el String a Color
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InfoChip(prenda.categoria, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
                prenda.temporada.forEach { t -> InfoChip(t, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary) }
                InfoChip(prenda.formalidad, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
                prenda.tags.forEach { tag -> InfoChip(tag, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary) }
            }

            Spacer(Modifier.height(30.dp))

            EstadisticaRow(label = "Total de usos", valor = totalUsos)
            Spacer(Modifier.height(6.dp))
            EstadisticaRow(label = "Promedio mensual", valor = promedioMensual)

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { mostrarDialogoEliminar = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Rosa),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onEditar,
                    colors = ButtonDefaults.buttonColors(containerColor = Amarillo),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Editar", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun parseColor(colorName: String): Color {
    return when (colorName.lowercase()) {
        "rojo" -> Color(0xFF802626)
        "azul" -> Color(0xFF0C6291)
        "negro" -> Color(0xFF000004)
        "amarillo" -> Color(0xFFD8973C)
        "rosa" -> Color(0xFFA73266)
        "blanco" -> Color.White
        else -> Color.Gray  // Color por defecto si no lo encuentra
    }
}