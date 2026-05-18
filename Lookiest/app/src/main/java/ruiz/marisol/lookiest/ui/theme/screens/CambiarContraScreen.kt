package ruiz.marisol.lookiest.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.ui.theme.components.BiometricHelper
import ruiz.marisol.lookiest.ui.theme.components.CampoContra
import ruiz.marisol.lookiest.viewModel.AuthViewModel

@Composable
fun CambiarContraScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    var passAnterior by remember { mutableStateOf("") }
    var passNueva by remember { mutableStateOf("") }
    var passConfirmar by remember { mutableStateOf("") }

    var visible1 by remember { mutableStateOf(false) }
    var visible2 by remember { mutableStateOf(false) }
    var visible3 by remember { mutableStateOf(false) }

    val currentUser by viewModel.currentUser.collectAsState()
    val biometriaActiva by viewModel.biometriaHabilitada.collectAsState()

    val biometricHelper = remember { BiometricHelper(context) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.lookiest_logo),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Lookiest", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = currentUser?.photoUrl,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            CampoContra(
                label = "Contraseña Anterior",
                value = passAnterior,
                onValueChange = { passAnterior = it },
                isVisible = visible1,
                onToggleVisibility = {
                    if (biometriaActiva) {
                        biometricHelper.lanzarBiometria(
                            onSuccess = { visible1 = !visible1 },
                            onError = { Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show() }
                        )
                    } else {
                        Toast.makeText(context, "Activa la biometría para ver este campo", Toast.LENGTH_LONG).show()
                    }
                }
            )

            CampoContra(
                label = "Contraseña Nueva",
                value = passNueva,
                onValueChange = { passNueva = it },
                isVisible = visible2,
                onToggleVisibility = { visible2 = !visible2 }
            )

            CampoContra(
                label = "Confirmar Contraseña Nueva",
                value = passConfirmar,
                onValueChange = { passConfirmar = it },
                isVisible = visible3,
                onToggleVisibility = { visible3 = !visible3 }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA63968)),
                    shape = RoundedCornerShape(20.dp)
                ) { Text("Descartar") }

                Button(
                    onClick = {
                        if (passAnterior.isEmpty()) {
                            Toast.makeText(context, "Escribe tu contraseña actual", Toast.LENGTH_SHORT).show()
                        } else if (passNueva != passConfirmar) {
                            Toast.makeText(context, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        } else if (passNueva.length < 6) {
                            Toast.makeText(context, "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.updatePassword(passNueva, passAnterior) { exito ->
                                if (exito) {
                                    Toast.makeText(context, "¡Contraseña actualizada!", Toast.LENGTH_SHORT).show()
                                    onNavigateBack()
                                } else {
                                    Toast.makeText(context, "Error: Contraseña actual incorrecta o error de red", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = RoundedCornerShape(20.dp)
                ) { Text("Guardar") }
            }
        }
    }
}
