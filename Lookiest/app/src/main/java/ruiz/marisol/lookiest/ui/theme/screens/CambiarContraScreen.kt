package ruiz.marisol.lookiest.ui.theme.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.ui.theme.BlancoFondo
import ruiz.marisol.lookiest.ui.theme.components.BiometricHelper
import ruiz.marisol.lookiest.ui.theme.components.CampoContra
import ruiz.marisol.lookiest.viewModel.AuthViewModel

@Composable
fun CambiarContraScreen(
    viewModel: AuthViewModel,
    esOlvido: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // Obtenemos la contraseña actual guardada para validar
    val passGuardada by viewModel.password.collectAsState()
    val context = LocalContext.current

    // Estados para los campos
    var passAnterior by remember(passGuardada) { mutableStateOf(passGuardada) }
    var passNueva by remember { mutableStateOf("") }
    var passConfirmar by remember { mutableStateOf("") }

    // Estados de visibilidad
    var visible1 by remember { mutableStateOf(false) }
    var visible2 by remember { mutableStateOf(false) }
    var visible3 by remember { mutableStateOf(false) }

    val usuarioData by viewModel.usuarioLogueado.collectAsState()
    val biometricHelper = remember { BiometricHelper(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.actualizarFotoPerfil(usuarioData?.email ?: "", it.toString())
        }
    }

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
                val esOscuro = isSystemInDarkTheme()

                Image(
                    painter = painterResource(id = R.drawable.lookiest_logo),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Lookiest", fontSize = 24.sp, fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = usuarioData?.fotoPerfil,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                )
                IconButton(
                    onClick = {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(30.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            if (!esOlvido) {
                CampoContra(
                    label = "Contraseña Anterior",
                    value = passAnterior,
                    onValueChange = { passAnterior = it },
                    isVisible = visible1,
                    onToggleVisibility = {
                        if (usuarioData?.biometriaActiva == true) {
                            biometricHelper.lanzarBiometria(
                                onSuccess = { visible1 = !visible1 },
                                onError = { Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show() }
                            )
                        } else {
                            Toast.makeText(context, "Activa la biometría para ver este campo", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }

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
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA63968)),
                    shape = RoundedCornerShape(20.dp)
                ) { Text("Descartar") }

                Button(
                    onClick = {
                        val anteriorCorrecta = esOlvido || passAnterior == passGuardada

                        if (!anteriorCorrecta) {
                            Toast.makeText(context, "La contraseña anterior no coincide", Toast.LENGTH_SHORT).show()
                        } else if (passNueva != passConfirmar) {
                            Toast.makeText(context, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        } else if (passNueva.isEmpty()) {
                            Toast.makeText(context, "Escribe una nueva contraseña", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.updatePassword(passNueva)
                            Toast.makeText(context, "¡Contraseña actualizada!", Toast.LENGTH_SHORT).show()
                            if(esOlvido){
                                onNavigateToHome()
                            }else{
                                onNavigateBack()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = RoundedCornerShape(20.dp)
                ) { Text("Guardar") }
            }
        }
    }
}