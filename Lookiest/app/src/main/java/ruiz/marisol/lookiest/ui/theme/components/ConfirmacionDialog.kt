package ruiz.marisol.lookiest.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ruiz.marisol.lookiest.ui.theme.Amarillo
import ruiz.marisol.lookiest.ui.theme.Rosa

@Composable
fun ConfirmacionDialog(
    mensaje: String,
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit
) {
    Dialog(onDismissRequest = onCancelar) {
        Card(
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(mensaje, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick  = onCancelar,
                        colors   = ButtonDefaults.buttonColors(containerColor = Rosa),
                        shape    = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancelar") }
                    Button(
                        onClick  = onConfirmar,
                        colors   = ButtonDefaults.buttonColors(containerColor = Amarillo),
                        shape    = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) { Text("Confirmar") }
                }
            }
        }
    }
}