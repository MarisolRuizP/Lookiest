package ruiz.marisol.lookiest.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.navigation.Screen
import ruiz.marisol.lookiest.ui.theme.Amarillo
import ruiz.marisol.lookiest.ui.theme.Azul50
import ruiz.marisol.lookiest.ui.theme.Blanco
import ruiz.marisol.lookiest.ui.theme.BlancoFondo
import ruiz.marisol.lookiest.ui.theme.Negro
import ruiz.marisol.lookiest.ui.theme.Rosa
import ruiz.marisol.lookiest.ui.theme.components.LookiestBottomBar
import ruiz.marisol.lookiest.ui.theme.components.LookiestTopBar
import ruiz.marisol.lookiest.viewModel.ClosetViewModel



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetalleOutfitScreen(
    outfit: Outfit,
    viewModel: ClosetViewModel,
    onBack: () -> Unit = {},
    onEliminar: () -> Unit = {},
    navController: NavController
) {
    // Estado local de likes / favorito
    var liked by remember { mutableStateOf(false) }
    var favorito by remember { mutableStateOf(false) }
    var likes by remember { mutableIntStateOf(530) }
    var favCount by remember { mutableIntStateOf(305) }

    Scaffold(
        topBar = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(
            selected = 0, navController) },
        containerColor = BlancoFondo
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // titulo y botones
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Título y creador
                    Column(modifier = Modifier.weight(1f)) {

                        Row(verticalAlignment = Alignment.CenterVertically){
                            IconButton(
                                onClick  = { /* onBack() cuando agregues navegación */ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Volver",
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = "Detalles del Outfit",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.width(20.dp))
                            Text(
                                text = "Creado por ",
                                fontSize = 13.sp,
                                color = Negro,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = outfit.creadoPor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Rosa,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Botón like
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        IconButton(
                            onClick  = {
                                liked = !liked
                                likes = if (liked) likes + 1 else likes - 1
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = Rosa,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "$likes",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    // Botón favorito
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        IconButton(
                            onClick  = {
                                favorito = !favorito
                                favCount = if (favorito) favCount + 1 else favCount - 1
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (favorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favorito",
                                tint = Amarillo,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "$favCount",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // etiquetas
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    outfit.etiquetas.forEach { tag ->
                        EtiquetaChip(tag)
                    }
                }
            }

            // prendas
            items(outfit.prendas, key = { it.id }) { prenda ->
                PrendaDetalleRow(prenda)
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun PrendaDetalleRow(prenda: PrendaRopa) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen o placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Blanco),
                contentAlignment = Alignment.Center
            ) {
                if (prenda.imagen != null) {
                    Image(
                        painter = painterResource(id = prenda.imagen),
                        contentDescription = prenda.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Checkroom,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            // Nombre de la prenda
            Text(
                text = prenda.nombre,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Negro
            )
        }
    }
}

@Composable
fun EtiquetaChip(texto: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Azul50)
            .padding(horizontal = 14.dp, vertical = 5.dp)
    ) {
        Text(
            text = texto,
            fontSize  = 12.sp,
            color = Negro,
            fontWeight = FontWeight.Medium
        )
    }
}
