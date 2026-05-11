package ruiz.marisol.lookiest.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.filter
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.navigation.Screen
import ruiz.marisol.lookiest.ui.theme.*
import ruiz.marisol.lookiest.ui.theme.components.*
import ruiz.marisol.lookiest.viewModel.ClosetViewModel


@Composable
fun CrearOutfitScreen(
    viewModel: ClosetViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onGuardar: () -> Unit = {},
    onDescartar: () -> Unit = {},
    navController: NavController
) {
    var busqueda             by remember { mutableStateOf("") }
    var prendasSeleccionadas by remember { mutableStateOf(setOf<Int>()) }
    var nombreOutfit         by remember { mutableStateOf("") }
    var esPublico            by remember { mutableStateOf(false) }
    var etiquetaTexto        by remember { mutableStateOf("") }
    var etiquetas            by remember { mutableStateOf(listOf<String>()) }

    val allPrendas by viewModel.prendas.collectAsState(initial = emptyList())
    val prendasFiltradas = remember(busqueda, allPrendas) {
        if (busqueda.isBlank()) allPrendas
        else allPrendas.filter {
            it.nombre.contains(busqueda, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(
            selected = 0, navController) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val nuevoFit = Outfit(
                        id = 0,
                        nombre = nombreOutfit.ifBlank { "Mi fit idk" },
                        prendas = prendasSeleccionadas.joinToString(","),
                        esPublico = esPublico,
                        etiquetas = etiquetas.joinToString(","),
                        creadoPor = "Mi"
                    )
                    viewModel.agregarOutfit(nuevoFit)
                    onGuardar()
                },
                containerColor = Amarillo,
                contentColor   = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Guardar Outfit", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "Crear Outfit",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(bottom = 10.dp)
            )

            LookiestTextField(
                label         = "Nombre del Outfit",
                value         = nombreOutfit,
                onValueChange = { nombreOutfit = it }
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value         = busqueda,
                onValueChange = { busqueda = it },
                placeholder   = { Text("Buscar...") },
                leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null) },
                shape         = RoundedCornerShape(50),
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor   = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
                    focusedBorderColor      = Rosa
                )
            )

            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns               = GridCells.Fixed(2),
                verticalArrangement   = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier              = Modifier.weight(1f)
            ) {
                items(prendasFiltradas) { prenda ->
                    val seleccionada = prenda.id in prendasSeleccionadas
                    PrendaSeleccionableCard(
                        prenda       = prenda,
                        seleccionada = seleccionada,
                        onClick = {
                            prendasSeleccionadas =
                                if (seleccionada) prendasSeleccionadas - prenda.id
                                else              prendasSeleccionadas + prenda.id
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Toggle público / privado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Outfit Público", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = esPublico,
                        onClick  = { esPublico = true },
                        colors   = RadioButtonDefaults.colors(selectedColor = Rosa)
                    )
                    Text("Sí", fontSize = 14.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !esPublico,
                        onClick  = { esPublico = false },
                        colors   = RadioButtonDefaults.colors(selectedColor = Rosa)
                    )
                    Text("No", fontSize = 14.sp)
                }
            }

            // Etiquetas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                Text("Etiquetas", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = {
                    val nueva = etiquetaTexto.trim()
                    if (nueva.isNotBlank() && nueva !in etiquetas) {
                        etiquetas     = etiquetas + nueva
                        etiquetaTexto = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Rosa)
                }
                OutlinedTextField(
                    value         = etiquetaTexto,
                    onValueChange = { etiquetaTexto = it },
                    placeholder   = { Text("Nueva etiqueta", fontSize = 12.sp) },
                    modifier      = Modifier.weight(1f).height(50.dp),
                    shape         = RoundedCornerShape(50),
                    singleLine    = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor   = Rosa
                    )
                )
            }

            if (etiquetas.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    etiquetas.forEach { tag ->
                        AssistChip(
                            onClick = { etiquetas = etiquetas - tag },
                            label   = { Text(tag, fontSize = 12.sp) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }

            Spacer(Modifier.height(70.dp))
        }
    }
}

// ── Tarjeta de prenda seleccionable (compartida con EditarOutfit) ──────────

@Composable
fun PrendaSeleccionableCard(
    prenda: PrendaRopa,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(2.dp, if (seleccionada) Rosa else Color.Transparent, RoundedCornerShape(14.dp))
    ) {
        Box {
            Column(modifier = Modifier.padding(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    if (!prenda.imagen.isNullOrEmpty()) {
                        AsyncImage(
                            model              = prenda.imagen,
                            contentDescription = prenda.nombre,
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            Icons.Default.Checkroom,
                            contentDescription = null,
                            tint     = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(prenda.nombre,    fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                Text(prenda.categoria, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            if (seleccionada) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(20.dp)
                        .background(Rosa, shape = RoundedCornerShape(50))
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(14.dp).align(Alignment.Center)
                    )
                }
            }
        }
    }
}