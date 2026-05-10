package ruiz.marisol.lookiest.ui.theme.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.ui.theme.BlancoFondo
import ruiz.marisol.lookiest.ui.theme.components.CampoEditar
import ruiz.marisol.lookiest.viewModel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val currentUserName by viewModel.username.collectAsState()

    var user by remember { mutableStateOf(currentUserName) }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    var mostrarDialogo by remember { mutableStateOf(false) }
    val currentUserNameSession by viewModel.username.collectAsState()
    val usuarioData by viewModel.usuarioLogueado.collectAsState()

    LaunchedEffect(usuarioData) {
        usuarioData?.let {
            user = it.username
            nombre = it.nombre
            correo = it.email
        }
    }

    LaunchedEffect(currentUserNameSession) {
        if (currentUserNameSession.isNotEmpty()) {
            viewModel.cargarDatosUsuario(currentUserNameSession)
        }
    }

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
                .verticalScroll(rememberScrollState())
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
                Text(text = "Lookiest", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
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

            CampoEditar(label = "Usuario", value = user, onValueChange = { user = it })
            CampoEditar(label = "Nombre", value = nombre, onValueChange = { nombre = it })
            CampoEditar(label = "Correo Electrónico", value = correo, onValueChange = { correo = it })

            Spacer(modifier = Modifier.weight(1F).height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(20.dp)
                ) { Text("Descartar") }

                Button(
                    onClick = { mostrarDialogo = true },
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = RoundedCornerShape(20.dp)
                ) { Text("Guardar") }
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = {
                Text(
                    text = "¿Deseas guardar los cambios?",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = FontFamily.Monospace
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(user, nombre, correo)
                        mostrarDialogo = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) { Text("Confirmar") }
            },
            dismissButton = {
                Button(
                    onClick = { mostrarDialogo = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) { Text("Cancelar") }
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(15.dp)
        )
    }
}