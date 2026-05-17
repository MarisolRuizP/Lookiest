package ruiz.marisol.lookiest.ui.theme.screens

import android.content.Context
import android.net.Uri
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.ui.theme.components.CardOption
import ruiz.marisol.lookiest.ui.theme.components.LookiestBottomBar
import ruiz.marisol.lookiest.ui.theme.components.LookiestTopBar
import ruiz.marisol.lookiest.ui.theme.components.rememberCameraHandler
import ruiz.marisol.lookiest.viewModel.AuthViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: AuthViewModel,
    onNavigateToEdit: () -> Unit,
    navController: NavController,
    onNavigateToChangePass: () -> Unit,
    onLogout: () -> Unit
) {
    // Cambiado: Ya no necesitamos cargarDatosUsuario, FirebaseUser tiene todo
    val currentUser by viewModel.currentUser.collectAsState()
    val biometriaActiva by viewModel.biometriaHabilitada.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val context = LocalContext.current

    var showPhotoOptions by remember { mutableStateOf(false) }
    var cameraUriPath by rememberSaveable { mutableStateOf<String?>(null) }
    val cameraUri = cameraUriPath?.let { Uri.parse(it) }

    fun crearUriTemporalParaCamara(context: Context): Uri {
        val directory = File(context.cacheDir, "camera_photos").apply { mkdirs() }
        val file = File(directory, "temp_photo_${System.currentTimeMillis()}.jpg")

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {fotoUri ->
            viewModel.subirFoto(fotoUri, context) { exito ->
                if (exito) {
                    Toast.makeText(context, "¡Foto de perfil actualizada desde Galería!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error al subir la foto de Galería", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val cameraHandler = rememberCameraHandler { uri ->
        viewModel.subirFoto(uri, context) { exito ->
            if (exito) {
                Toast.makeText(context, "¡Foto de perfil actualizada!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error al subir a Firebase", Toast.LENGTH_SHORT).show()
            }
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
                    model = currentUser?.photoUrl,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "Usuario",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = currentUser?.email ?: "Cargando...",
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
                subtitle = if (biometriaActiva) "Desactivar" else "Activar",
                onClick = {
                    viewModel.cambiarBiometriaFirebase(!biometriaActiva)

                    val mensaje = if (!biometriaActiva) "Biometría activada" else "Biometría desactivada"
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            CardOption(
                title = "Cambiar Tema",
                subtitle = if (isDarkMode) "Tema Oscuro" else "Tema Claro",
                onClick = {
                    viewModel.cambiarTemaFirebase(isDarkMode)
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