package ruiz.marisol.lookiest.ui.theme.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.ui.theme.BlancoFondo
import ruiz.marisol.lookiest.ui.theme.components.CardOption
import ruiz.marisol.lookiest.ui.theme.components.LookiestBottomBar
import ruiz.marisol.lookiest.ui.theme.components.LookiestTopBar
import ruiz.marisol.lookiest.viewModel.AuthViewModel

@Composable
fun PerfilScreen(
    viewModel: AuthViewModel,
    onNavigateToEdit: () -> Unit,
    navController: NavController,
    onNavigateToChangePass: () -> Unit,
    onLogout: () -> Unit
) {
    val userNameSession by viewModel.username.collectAsState()
    val usuarioData by viewModel.usuarioLogueado.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(userNameSession) {
        if (userNameSession.isNotEmpty()) {
            viewModel.cargarDatosUsuario(userNameSession)
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
        topBar = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(selected = 4, navController = navController) },
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

            Spacer(modifier = Modifier.height(10.dp))

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
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    shadowElevation = 4.dp
                ){
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Cambiar foto",
                        modifier = Modifier.padding(6.dp),
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = usuarioData?.nombre ?: "Cargando...",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = "@${usuarioData?.username ?: userNameSession}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
            Text(
                text = usuarioData?.email ?: "",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onNavigateToEdit,
                modifier = Modifier.fillMaxWidth().height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Editar Perfil")
            }

            Spacer(modifier = Modifier.height(20.dp))

            CardOption(
                title = "Biometría",
                subtitle = if (usuarioData?.biometriaActiva == true) "Desactivar" else "Activar",
                onClick = {
                    val usernameActual = usuarioData?.username ?: ""
                    val estadoActual = usuarioData?.biometriaActiva ?: false
                    viewModel.actualizarBiometria(usernameActual, !estadoActual)
                    val mensaje = if (!estadoActual) "Biometría activada" else "Biometría desactivada"
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            CardOption(
                title = "Cambiar Tema",
                subtitle = if (usuarioData?.isDarkMode == true) "Tema Oscuro" else "Tema Claro",
                onClick = {
                    val username = usuarioData?.username ?: ""
                    val modoActual = usuarioData?.isDarkMode ?: false
                    viewModel.actualizarTheme(username, modoActual)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onNavigateToChangePass,
                modifier = Modifier.fillMaxWidth().height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Cambiar contraseña")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    Toast.makeText(context, "Usa tu huella para cerrar sesión", Toast.LENGTH_LONG).show()
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA63968)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Cerrar Sesión")
            }
        }
    }
}