package ruiz.marisol.lookiest.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ruiz.marisol.lookiest.ui.theme.Blanco
import ruiz.marisol.lookiest.ui.theme.Negro
import ruiz.marisol.lookiest.ui.theme.Rosa50

@Composable
fun EstadisticaRow(label: String, valor: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = Negro, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Rosa50)
                .width(152.dp)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("$valor", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Blanco)
        }
    }
}