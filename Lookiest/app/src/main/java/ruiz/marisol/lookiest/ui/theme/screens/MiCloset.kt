package ruiz.marisol.lookiest.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.navigation.Screen
import ruiz.marisol.lookiest.ui.theme.Amarillo
import ruiz.marisol.lookiest.ui.theme.Azul
import ruiz.marisol.lookiest.ui.theme.BlancoFondo
import ruiz.marisol.lookiest.ui.theme.LookiestTheme
import ruiz.marisol.lookiest.ui.theme.Rosa
import ruiz.marisol.lookiest.ui.theme.components.LookiestBottomBar
import ruiz.marisol.lookiest.ui.theme.components.LookiestTopBar
import ruiz.marisol.lookiest.ui.theme.components.PrendaCard
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

@Composable
fun ClosetScreen(
    viewModel: ClosetViewModel,
    navController: NavController,
    onPrendaClick: (PrendaRopa) -> Unit = {},
) {
    val prendas by viewModel.prendas.collectAsState(initial = emptyList())
    var busqueda by remember { mutableStateOf("") }

    val listaFiltrada = remember(busqueda, prendas) {
        if (busqueda.isBlank()) prendas
        else prendas.filter {
            it.nombre.contains(busqueda, ignoreCase = true) ||
                    it.categoria.contains(busqueda, ignoreCase = true) ||
                    it.tienda.contains(busqueda, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(selected = 2, navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AgregarPrenda.route) },
                containerColor = Amarillo,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar prenda",
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        containerColor = BlancoFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 26.dp)
        ) {

            Spacer(Modifier.height(8.dp))

            // Buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = {
                    Text(
                        "Buscar...",
                        color = Azul,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Monospace
                    )
                },
                trailingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.DarkGray)
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(Modifier.height(4.dp))

            //Título + Ordenar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mi Clóset",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(Modifier.width(90.dp))
                TextButton(onClick = { }) {
                    Text(
                        text = "Ordenar por",
                        color = Rosa,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton({}) {
                        Icon(
                            imageVector        = Icons.Default.SwapVert,
                            contentDescription = "Ordenar",
                            tint               = Rosa
                        )
                    }
                }

            }

            Spacer(Modifier.height(8.dp))

            // ── Grid de prendas
            if (listaFiltrada.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (busqueda.isNotBlank()) "Sin resultados para \"$busqueda\""
                        else "Tu clóset está vacío.\n¡Agrega tu primera prenda!",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement  = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(listaFiltrada, key = { it.id }) { item ->
                        PrendaCard(
                            prenda = item,
                            onFavoriteClick = { viewModel.favorito(item) },
                            onMoreClick = { onPrendaClick(item) }
                        )
                    }
                }
            }
        }
    }
}

