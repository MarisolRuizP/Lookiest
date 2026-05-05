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
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.ui.theme.*
import ruiz.marisol.lookiest.ui.theme.components.*
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

@Composable
fun DetallesOutfitScreen(
    outfit: Outfit,
    viewModel: ClosetViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onEditar: () -> Unit = {},
    onEliminarConfirmado: () -> Unit = {}
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    if (mostrarDialogoEliminar) {
        ConfirmacionDialog(
            mensaje = "¿Deseas eliminar este outfit?",
            onCancelar = { mostrarDialogoEliminar = false },
            onConfirmar = {
                mostrarDialogoEliminar = false
                viewModel.eliminarOutfit(outfit.id)
                onEliminarConfirmado()
            }
        )
    }

    Scaffold(
        topBar = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(selected = 1) },
        containerColor = BlancoFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = 70.dp)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con título y acciones
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Detalles del Outfit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Row {
                    IconButton(onClick = {// falta lo de favoritos }) {
                        Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorito", tint = Rosa)
                    }
                    IconButton(onClick = { //tambien esto de compartit }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir", tint = Amarillo)
                    }
                }
            }

            // Creado por
            Text(
                text = "Creado por ${outfit.creadoPor}",
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = Rosa,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Chips de info del outfit
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoChip(if (outfit.esPublico) "Público" else "Privado", Azul50, Negro)
                outfit.etiquetas.forEach { tag -> InfoChip(tag, Azul50, Negro) }
            }

            Spacer(Modifier.height(14.dp))

            // Lista de prendas del outfit
            outfit.prendas.forEach { prenda ->
                PrendaOutfitRow(prenda = prenda)
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))

            // Botones Eliminar / Editar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { mostrarDialogoEliminar = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Rosa),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onEditar,
                    colors = ButtonDefaults.buttonColors(containerColor = Amarillo),
                    shape = RoundedCornerShape(50),
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
fun PrendaOutfitRow(prenda: PrendaRopa) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF5F5F7), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (prenda.imagen != null) {
                    Image(
                        painter = painterResource(id = prenda.imagen),
                        contentDescription = prenda.nombre,
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        Icons.Default.Checkroom,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                prenda.nombre,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}