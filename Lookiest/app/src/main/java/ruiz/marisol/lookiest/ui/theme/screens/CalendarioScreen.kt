package ruiz.marisol.lookiest.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ruiz.marisol.lookiest.viewModel.ClosetViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarioScreen(viewModel: ClosetViewModel) {
    var mesActual by remember { mutableStateOf(YearMonth.now()) }
    var fechaSeleccionada by remember { mutableStateOf(LocalDate.now()) }

    val prendasDelDia = viewModel.prendasUsadasEn(fechaSeleccionada.toString())

    val nombreMes = fechaSeleccionada.month
        .getDisplayName(TextStyle.FULL, Locale("es"))
        .replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mi Calendario",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CalendarioMes(
            mesActual = mesActual,
            fechaSeleccionada = fechaSeleccionada,
            diasConUso = viewModel.usos.map { it.fecha }.toSet(),
            onDiaClick = { fechaSeleccionada = it },
            onMesAnterior = { mesActual = mesActual.minusMonths(1) },
            onMesSiguiente = { mesActual = mesActual.plusMonths(1) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Prendas usadas en",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${fechaSeleccionada.dayOfMonth} de $nombreMes de ${fechaSeleccionada.year}",
            fontSize = 14.sp,
            color = Color(0xFFE57373),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (prendasDelDia.isEmpty()) {
            Text(
                text = "No hay prendas registradas para este día",
                fontSize = 13.sp,
                color = Color.Gray
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(prendasDelDia) { prenda ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            prenda.imagen?.let {
                                Image(
                                    painter = painterResource(id = it),
                                    contentDescription = prenda.nombre,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(
                                            Color(0xFFF5F5F5),
                                            RoundedCornerShape(8.dp)
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = prenda.nombre,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarioMes(
    mesActual: YearMonth,
    fechaSeleccionada: LocalDate,
    diasConUso: Set<String>,
    onDiaClick: (LocalDate) -> Unit,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit
) {
    val nombreMes = mesActual.month
        .getDisplayName(TextStyle.FULL, Locale("es"))
        .replaceFirstChar { it.uppercase() }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onMesAnterior) {
                    Text("<", fontSize = 18.sp, color = Color(0xFFE57373))
                }
                Text(
                    text = "$nombreMes ${mesActual.year}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE57373)
                )
                TextButton(onClick = onMesSiguiente) {
                    Text(">", fontSize = 18.sp, color = Color(0xFFE57373))
                }
            }

            val diasSemana = listOf("DOM", "LUN", "MAR", "MIE", "JUE", "VIE", "SAB")
            Row(modifier = Modifier.fillMaxWidth()) {
                diasSemana.forEach { dia ->
                    Text(
                        text = dia,
                        modifier = Modifier.weight(1f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE57373),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val primerDia = mesActual.atDay(1).dayOfWeek.value % 7
            val totalDias = mesActual.lengthOfMonth()
            val filas = ((primerDia + totalDias) + 6) / 7

            for (fila in 0 until filas) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val index = fila * 7 + col
                        val dia = index - primerDia + 1
                        val fecha = if (dia in 1..totalDias)
                            mesActual.atDay(dia) else null
                        val esHoy = fecha == LocalDate.now()
                        val esSeleccionado = fecha == fechaSeleccionada
                        val tieneUso = fecha != null && diasConUso.contains(fecha.toString())

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .then(
                                    if (fecha != null)
                                        Modifier.clickable { onDiaClick(fecha) }
                                    else Modifier
                                )
                                .background(
                                    when {
                                        esSeleccionado -> Color(0xFFE57373)
                                        else -> Color.Transparent
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (fecha != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = dia.toString(),
                                        fontSize = 13.sp,
                                        color = when {
                                            esSeleccionado -> Color.White
                                            esHoy -> Color(0xFFE57373)
                                            else -> Color.Black
                                        },
                                        fontWeight = if (esHoy || esSeleccionado)
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (tieneUso && !esSeleccionado) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .background(Color(0xFFE57373), CircleShape)
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