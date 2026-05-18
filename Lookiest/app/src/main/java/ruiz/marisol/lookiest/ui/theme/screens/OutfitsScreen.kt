package ruiz.marisol.lookiest.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.ui.theme.Amarillo
import ruiz.marisol.lookiest.ui.theme.Rosa
import ruiz.marisol.lookiest.ui.theme.components.LookiestBottomBar
import ruiz.marisol.lookiest.ui.theme.components.LookiestTopBar
import ruiz.marisol.lookiest.viewModel.ClosetViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.collectAsState


@Composable
fun OutfitsScreen(
    viewModel: ClosetViewModel,
    onOutfitClick: (Outfit) -> Unit = {},
    onNuevoOutfit: () -> Unit = {},
    navController: NavController
) {
    val outfits by viewModel.outfits.collectAsState(initial = emptyList())
    val todasLasPrendas by viewModel.prendas.collectAsState(initial = emptyList())

    var tabSeleccionado by remember { mutableStateOf(0) }
    var busqueda by remember { mutableStateOf("") }

    val misOutfits = outfits.filter { it.creadoPor == viewModel.usuarioActualEmail }
    val explorar = outfits.filter { it.esPublico && it.creadoPor != viewModel.usuarioActualEmail}

    val listaActual = if (tabSeleccionado == 0) misOutfits else explorar

    val listaFiltrada = remember(busqueda, listaActual) {
        if (busqueda.isBlank()) listaActual
        else listaActual.filter { outfit ->
            outfit.nombre.contains(busqueda, ignoreCase = true) ||
                    outfit.etiquetas.contains(busqueda, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(
            selected = 0, navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoOutfit,
                containerColor = Rosa,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo outfit", modifier = Modifier.size(22.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            //banner del coso offline
            val hayInternet by viewModel.hayInternet.collectAsState()
            AnimatedVisibility(
                visible = !hayInternet,
                enter   = slideInVertically() + fadeIn(),
                exit    = slideOutVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF5C5C5C))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Sin conexión · los outfits se guardan local",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            // buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder   = {
                    Text("Buscar...", color = MaterialTheme.colorScheme.primary, fontStyle = FontStyle.Italic)
                },
                trailingIcon  = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.onSurface)
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )

            // Tabs
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutfitTab(
                    texto = "Mis outfits",
                    seleccionado = tabSeleccionado == 0,
                    onClick = { tabSeleccionado = 0 }
                )
                OutfitTab(
                    texto = "Explorar",
                    seleccionado = tabSeleccionado == 1,
                    onClick = { tabSeleccionado = 1 }
                )
            }

            Spacer(Modifier.height(12.dp))

            // Lista o estado vacío
            if (listaFiltrada.isEmpty()) {
                EstadoVacio(
                    mensaje = if (busqueda.isNotBlank()) "Sin resultados para \"$busqueda\""
                    else if (tabSeleccionado == 0) "Aún no tienes outfits.\n¡Crea tu primero con el botón +!"
                    else "No hay outfits públicos por explorar."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(listaFiltrada, key = { it.id }) { outfit ->
                        OutfitRow(
                            outfit = outfit,
                            todasLasPrendas = todasLasPrendas,
                            onClick = { onOutfitClick(outfit) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OutfitRow(
    outfit: Outfit,
    todasLasPrendas: List<PrendaRopa>,
    onClick: () -> Unit = {}
) {
    val idsPrendas = outfit.prendas.split(",").mapNotNull { it.trim().toIntOrNull() }
    val prendasDelOutfit = todasLasPrendas.filter { it.id in idsPrendas }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miniaturas
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                prendasDelOutfit.take(5).forEach { prenda ->
                    PrendaMiniatura(prenda)
                }
                // Placeholder si el outfit no tiene prendas aún
                if (outfit.prendas.isEmpty()) {
                    repeat(5) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Checkroom,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver outfit",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun PrendaMiniatura(prenda: PrendaRopa) {
    Box(
        modifier = Modifier.size(58.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!prenda.imagen.isNullOrEmpty()) {
            AsyncImage(
                model = prenda.imagen,
                contentDescription = prenda.nombre,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Checkroom,
                contentDescription = prenda.nombre,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(38.dp)
            )
        }
    }
}

@Composable
fun OutfitTab(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (seleccionado) Amarillo else MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 7.dp)
    ) {
        Text(
            text = texto,
            fontSize = 14.sp,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal,
            color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EstadoVacio(mensaje: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Checkroom,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = mensaje,
                fontSize  = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}