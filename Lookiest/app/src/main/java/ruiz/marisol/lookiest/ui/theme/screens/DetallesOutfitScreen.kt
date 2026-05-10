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
import ruiz.marisol.lookiest.R
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
            mensaje     = "¿Deseas eliminar este outfit?",
            onCancelar  = { mostrarDialogoEliminar = false },
            onConfirmar = {
                mostrarDialogoEliminar = false
                viewModel.eliminarOutfit(outfit.id)
                onEliminarConfirmado()
            }
        )
    }

    Scaffold(
        topBar    = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(selected = 1) },
        containerColor = BlancoFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = 70.dp)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
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
                    IconButton(onClick = { /* TODO: toggle favorito outfit */ }) {
                        Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorito", tint = Rosa)
                    }
                    IconButton(onClick = { /* TODO: compartir outfit */ }) {
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
                    bgColor = Azul50,
                    textColor = Negro
                )
                outfit.etiquetas.forEach { tag ->
                    InfoChip(texto = tag, bgColor = Azul50, textColor = Negro)
                }
            }

            // Estadística de uso
            Spacer(Modifier.height(14.dp))
            EstadisticaRow(label = "Total de usos", valor = outfit.totalUsos)
            Spacer(Modifier.height(14.dp))

            // Lista de prendas
            if (outfit.prendas.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxWidth().height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin prendas asignadas", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                outfit.prendas.forEach { prenda ->
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
        colors   = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
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
                        painter            = painterResource(id = prenda.imagen),
                        contentDescription = prenda.nombre,
                        modifier           = Modifier.fillMaxSize().padding(4.dp),
                        contentScale       = ContentScale.Fit
                    )
                } else {
                    Icon(
                        Icons.Default.Checkroom,
                        contentDescription = null,
                        tint     = Color.LightGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(prenda.nombre,    fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(prenda.categoria, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewDetallesOutfit() {
    val prendasMock = listOf(
        PrendaRopa(id = 1, nombre = "Chaqueta roja de vinipiel", tienda = "Zara",     talla = "M",  color = "Rojo", estampado = false, categoria = "OuterWear", formalidad = "Casual", imagen = R.drawable.chaqueta_roja),
        PrendaRopa(id = 2, nombre = "Falda roja con patoles",    tienda = "",          talla = "XS", color = "Rojo", estampado = true,  categoria = "Bottom",    formalidad = "Casual", imagen = R.drawable.falda_roja)
    )
    val outfitMock = Outfit(
        id        = 1,
        nombre    = "Look Rojo Otoñal",
        prendas   = prendasMock,
        esPublico = false,
        etiquetas = listOf("Casual", "Otoño", "Rojo", "Inspo", "2026"),
        creadoPor = "Mi (Marisol_Ruiz)",
        totalUsos = 3
    )
    LookiestTheme {
        DetallesOutfitScreen(
            outfit   = outfitMock,
            onEditar = {}
        )
    }
}