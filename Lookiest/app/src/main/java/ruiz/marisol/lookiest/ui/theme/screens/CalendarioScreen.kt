package ruiz.marisol.lookiest.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ruiz.marisol.lookiest.ui.theme.Amarillo
import ruiz.marisol.lookiest.ui.theme.Azul50
import ruiz.marisol.lookiest.ui.theme.Rosa
import ruiz.marisol.lookiest.ui.theme.components.LookiestBottomBar
import ruiz.marisol.lookiest.ui.theme.components.LookiestTopBar
import ruiz.marisol.lookiest.viewModel.ClosetViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarioScreen(
    viewModel: ClosetViewModel,
    navController: NavController
) {
    var mesActual by remember { mutableStateOf(YearMonth.now()) }
    var fechaSeleccionada by remember { mutableStateOf(LocalDate.now()) }

    val usos by viewModel.usos.collectAsState()
    val diasConUso = remember(usos) { usos.map { it.fecha }.toSet() }

    val prendasDelDia = remember(fechaSeleccionada, usos) {
        viewModel.prendasUsadasEn(fechaSeleccionada.toString())
    }

    val nombreMes = fechaSeleccionada.month
        .getDisplayName(TextStyle.FULL, Locale("es"))
        .replaceFirstChar { it.uppercase() }

    Scaffold(
        topBar = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(selected = 3, navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) {padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Mi Calendario",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { mesActual = mesActual.minusMonths(1) }) {
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = "Mes anterior",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            val nombreMesCal = mesActual.month
                                .getDisplayName(TextStyle.FULL, Locale("es"))
                                .replaceFirstChar { it.uppercase() }
                            Text(
                                text = "$nombreMesCal ${mesActual.year}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(onClick = { mesActual = mesActual.plusMonths(1) }) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Mes siguiente",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        val diasSemana = listOf("D", "L", "M", "M", "J", "V", "S")
                        Row(modifier = Modifier.fillMaxWidth()) {
                            diasSemana.forEach { dia ->
                                Text(
                                    text = dia,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Rosa,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        val primerDia = mesActual.atDay(1).dayOfWeek.value % 7
                        val totalDias = mesActual.lengthOfMonth()
                        val filas = ((primerDia + totalDias) + 6) / 7

                        for (fila in 0 until filas) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                for (col in 0 until 7) {
                                    val index = fila * 7 + col
                                    val dia = index - primerDia + 1
                                    val fecha = if (dia in 1..totalDias) mesActual.atDay(dia) else null
                                    val esHoy = fecha == LocalDate.now()
                                    val esSeleccionado = fecha == fechaSeleccionada
                                    val tieneUso = fecha != null && diasConUso.contains(fecha.toString())

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(3.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    esSeleccionado -> Rosa
                                                    esHoy -> Azul50
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .then(
                                                if (fecha != null)
                                                    Modifier.clickable { fechaSeleccionada = fecha }
                                                else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (fecha != null) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = dia.toString(),
                                                    fontSize = 13.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = when {
                                                        esSeleccionado -> Color.White
                                                        esHoy -> MaterialTheme.colorScheme.primary
                                                        else -> MaterialTheme.colorScheme.onBackground
                                                    },
                                                    fontWeight = if (esHoy || esSeleccionado) FontWeight.Bold else FontWeight.Normal
                                                )
                                                if (tieneUso && !esSeleccionado) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .background(Rosa, CircleShape)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column {
                    Text(
                        text = "Prendas usadas en",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${fechaSeleccionada.dayOfMonth} de $nombreMes de ${fechaSeleccionada.year}",
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Rosa
                    )
                }
            }

            if (prendasDelDia.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay prendas registradas para este día",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(prendasDelDia) { prenda ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!prenda.imagen.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = prenda.imagen,
                                        contentDescription = prenda.nombre,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Checkroom,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = prenda.nombre,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = prenda.categoria,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}