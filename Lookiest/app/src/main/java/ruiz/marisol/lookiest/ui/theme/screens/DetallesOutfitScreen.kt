package ruiz.marisol.lookiest.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.ui.theme.*
import ruiz.marisol.lookiest.ui.theme.components.*
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

@Composable
fun DetallesOutfitScreen(
    outfit: Outfit,
    viewModel: ClosetViewModel,
    navController: NavController,
    onEditar: () -> Unit = {},
    onEliminarConfirmado: () -> Unit = {}
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    val etiquetasLista = outfit.etiquetas.split(",").filter { it.isNotBlank() }
    val todasLasPrendas by viewModel.prendas.collectAsState(initial = emptyList())
    val idsPrendas = outfit.prendas.split(",").mapNotNull { it.trim().toIntOrNull() }
    val prendasDelOutfit = todasLasPrendas.filter { it.id in idsPrendas }

    if (mostrarDialogoEliminar) {
        ConfirmacionDialog(
            mensaje     = "¿Deseas eliminar este outfit?",
            onCancelar  = { mostrarDialogoEliminar = false },
            onConfirmar = {
                mostrarDialogoEliminar = false
                viewModel.eliminarOutfit(outfit)
                onEliminarConfirmado()
            }
        )
    }

    Scaffold(
        topBar    = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(selected = 1, navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = 70.dp)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))
            // Encabezado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Detalles del Outfit", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorito", tint = Rosa)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir", tint = Amarillo)
                    }
                }
            }

            Text(
                text      = "Creado por ${outfit.creadoPor}",
                fontSize  = 12.sp,
                fontStyle = FontStyle.Italic,
                color     = Rosa,
                modifier  = Modifier.padding(bottom = 8.dp)
            )

            // Chips de info
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoChip(
                    texto   = if (outfit.esPublico) "Público" else "Privado",
                    bgColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onPrimary
                )
                etiquetasLista.forEach { tag ->
                    InfoChip(texto = tag.trim(), bgColor = MaterialTheme.colorScheme.primary, textColor = MaterialTheme.colorScheme.onPrimary)
                }
            }

            // Estadística de uso
            Spacer(Modifier.height(14.dp))
//            EstadisticaRow(label = "Total de usos", valor = outfit.totalUsos)
//            Spacer(Modifier.height(14.dp)) idk

            // Lista de prendas
            if (prendasDelOutfit.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxWidth().height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin prendas asignadas", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                }
            } else {
                prendasDelOutfit.forEach { prenda ->
                    PrendaOutfitRow(prenda = prenda)
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick  = { mostrarDialogoEliminar = true },
                    colors   = ButtonDefaults.buttonColors(containerColor = Rosa),
                    shape    = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Eliminar", fontWeight = FontWeight.Bold) }

                Button(
                    onClick  = onEditar,
                    colors   = ButtonDefaults.buttonColors(containerColor = Amarillo),
                    shape    = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Editar", fontWeight = FontWeight.Bold) }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun PrendaOutfitRow(prenda: PrendaRopa) {
    Card(
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!prenda.imagen.isNullOrEmpty()) {
                    AsyncImage(
                        model              = prenda.imagen,
                        contentDescription = prenda.nombre,
                        modifier           = Modifier.fillMaxSize().padding(4.dp),
                        contentScale       = ContentScale.Fit
                    )
                } else {
                    Icon(
                        Icons.Default.Checkroom,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(prenda.nombre,    fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(prenda.categoria, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}