package ruiz.marisol.lookiest.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ruiz.marisol.lookiest.ui.theme.Azul
import ruiz.marisol.lookiest.ui.theme.Azul50
import ruiz.marisol.lookiest.ui.theme.Negro
import ruiz.marisol.lookiest.ui.theme.Rosa
import ruiz.marisol.lookiest.ui.theme.Rosa50

@Composable
fun InfoChip(texto: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50)) // Stadium shape total
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(texto, fontSize = 13.sp, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ChipSeleccionable(
    texto: String,
    seleccionado: Boolean,
    colorFuerte: Color = Rosa50,
    colorClaro: Color = Azul50,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (seleccionado) colorFuerte else colorClaro)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = texto,
            fontSize = 13.sp,
            color = Negro,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorMultiple(
    titulo: String,
    opciones: List<String>,
    seleccionadas: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opciones.forEach { op ->
                val esSeleccionado = op in seleccionadas

                Surface(
                    onClick = { onToggle(op) },
                    shape = RoundedCornerShape(50), // Stadium shape
                    color = if (esSeleccionado) Rosa50 else Azul50,
                    contentColor = Negro
                ) {
                    Text(
                        text = op,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}