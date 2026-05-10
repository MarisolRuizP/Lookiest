package ruiz.marisol.lookiest.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

) {
    val todasLasPrendas  by viewModel.prendas.collectAsState()
    val prendasUsadasHoy by viewModel.prendasUsadasHoy.collectAsState()

    Scaffold(
        topBar = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(
            selected = 2, navController) },
        containerColor = BlancoFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "Prendas usadas hoy",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                modifier   = Modifier.padding(bottom = 12.dp)
            )

            if (todasLasPrendas.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Tu clóset está vacío.\n¡Agrega prendas primero!",
                        color    = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns               = GridCells.Fixed(2),
                    verticalArrangement   = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier              = Modifier.weight(1f)
                ) {
                    items(todasLasPrendas) { prenda ->
                        val usadaHoy = prendasUsadasHoy.any { it.id == prenda.id }
                        PrendaSeleccionableCard(
                            prenda       = prenda,
                            seleccionada = usadaHoy,
                            onClick      = { viewModel.toggleUsadaHoy(prenda.id, !usadaHoy) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(70.dp))
        }
    }
}