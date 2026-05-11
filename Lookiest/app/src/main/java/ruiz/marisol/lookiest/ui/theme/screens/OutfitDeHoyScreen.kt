package ruiz.marisol.lookiest.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ruiz.marisol.lookiest.ui.theme.*
import ruiz.marisol.lookiest.ui.theme.components.*
import ruiz.marisol.lookiest.viewModel.ClosetViewModel


@Composable
fun OutfitDeHoyScreen(
    viewModel: ClosetViewModel,
    navController: NavController,
    onVolver: () -> Unit = {}
) {
    val todasLasPrendas  by viewModel.prendas.collectAsState()
    val prendasUsadasHoy by viewModel.prendasUsadasHoy.collectAsState()
    var busqueda         by remember { mutableStateOf("") }

    val prendasFiltradas = remember(busqueda, todasLasPrendas) {
        if (busqueda.isBlank()) todasLasPrendas
        else todasLasPrendas.filter {
            it.nombre.contains(busqueda, ignoreCase = true) ||
                    it.categoria.contains(busqueda, ignoreCase = true)
        }
    }

    Scaffold(
        topBar    = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(selected = 1, navController = navController) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.guardarUsoDiario()    // guarda en el calendario
                    viewModel.resetUsadasHoy()     // limpia los toggles
                    onVolver()
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

            // Buscador
            OutlinedTextField(
                value         = busqueda,
                onValueChange = { busqueda = it },
                placeholder = {
                    Text(
                        "Buscar...",
                        color      = MaterialTheme.colorScheme.primary,
                        fontStyle  = FontStyle.Italic,
                        fontFamily = FontFamily.Monospace
                    )
                },
                trailingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor   = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor    = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Text(
                text       = "Prendas usadas hoy",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier   = Modifier.padding(bottom = 10.dp)
            )

            if (todasLasPrendas.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Tu clóset está vacío.\n¡Agrega prendas primero!",
                        color    = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns               = GridCells.Fixed(2),
                    verticalArrangement   = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding        = PaddingValues(bottom = 80.dp),
                    modifier              = Modifier.fillMaxSize()
                ) {
                    items(prendasFiltradas, key = { it.id }) { prenda ->
                        val usadaHoy = prendasUsadasHoy.any { it.id == prenda.id }
                        PrendaSeleccionableCard(
                            prenda       = prenda,
                            seleccionada = usadaHoy,
                            onClick      = {
                                viewModel.toggleUsadaHoy(prenda.id, !usadaHoy)
                            }
                        )
                    }
                }
            }
        }
    }
}