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
import ruiz.marisol.lookiest.ui.theme.Azul
import ruiz.marisol.lookiest.ui.theme.Azul50
import ruiz.marisol.lookiest.ui.theme.Blanco
import ruiz.marisol.lookiest.ui.theme.BlancoFondo
import ruiz.marisol.lookiest.ui.theme.LookiestTheme
import ruiz.marisol.lookiest.ui.theme.Negro
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
        bottomBar = { LookiestBottomBar(
            selected = 2, navController) },
        containerColor = BlancoFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = 70.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Detalles de la Prenda",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal=20.dp, vertical = 10.dp)
            )


            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),

            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Usamos AsyncImage para leer el String (URI) de la base de datos
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
                                .background(Color(0xFFF5F5F7))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checkroom,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = Color.LightGray
                            )
                        }
                    }

                    IconButton(
                        // Pasamos el objeto completo para cambiar el favorito en Room
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
                    color = Negro
                )
            }

            Spacer(Modifier.height(5.dp))


            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(texto = prenda.talla, bgColor = Blanco, textColor = Rosa)
                InfoChip(
                    texto = if (prenda.estampado) "Estampado" else "Sin Estampado",
                    bgColor = Blanco,
                    textColor = Azul
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
                    color = Negro
                )

                Spacer(Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            color = parseColor(prenda.color), // Función para convertir el String a Color
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
                InfoChip(prenda.categoria, Azul50, Negro)
                prenda.temporada.forEach { t -> InfoChip(t, Azul50, Negro) }
                InfoChip(prenda.formalidad, Azul50, Negro)
                prenda.tags.forEach { tag -> InfoChip(tag, Azul50, Negro) }
            }

            Spacer(Modifier.height(30.dp))

       
            EstadisticaRow(label = "Total de usos", valor = 15)
            Spacer(Modifier.height(6.dp))
            EstadisticaRow(label = "Promedio mensual", valor = 3)

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
        else -> Color.Gray // Color por defecto si no lo encuentra
    }
}

