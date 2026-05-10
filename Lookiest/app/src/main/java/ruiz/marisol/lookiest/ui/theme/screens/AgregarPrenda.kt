package ruiz.marisol.lookiest.ui.theme.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.navigation.Screen
import ruiz.marisol.lookiest.ui.theme.Amarillo
import ruiz.marisol.lookiest.ui.theme.BlancoFondo
import ruiz.marisol.lookiest.ui.theme.LookiestTheme
import ruiz.marisol.lookiest.ui.theme.Rosa
import ruiz.marisol.lookiest.ui.theme.components.ChipSeleccionable
import ruiz.marisol.lookiest.ui.theme.components.ConfirmacionDialog
import ruiz.marisol.lookiest.ui.theme.components.LookiestBottomBar
import ruiz.marisol.lookiest.ui.theme.components.LookiestTextField
import ruiz.marisol.lookiest.ui.theme.components.LookiestTopBar
import ruiz.marisol.lookiest.ui.theme.components.SelectorMultiple
import ruiz.marisol.lookiest.viewModel.ClosetViewModel
import java.io.File


fun Context.createImageFile(): File {
    return File.createTempFile(
        "JPEG_${System.currentTimeMillis()}_",
        ".jpg",
        externalCacheDir
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AgregarPrendaScreen(
    viewModel: ClosetViewModel,
    onGuardado: () -> Unit = {},
    onDescartado: () -> Unit = {},
    navController: NavController
) {

    val context = LocalContext.current

    // estados
    var nombre by remember { mutableStateOf("") }
    var tienda by remember { mutableStateOf("") }
    var talla by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var estampado by remember { mutableStateOf(false) } // Cambiado a Boolean
    var categoria by remember { mutableStateOf("") }
    var tagsSeleccionadas by remember { mutableStateOf(emptySet<String>()) } // Cambiado a Set
    var temporadasSeleccionadas by remember { mutableStateOf(emptySet<String>()) } // Cambiado a Set
    var formalidad by remember { mutableStateOf("") } // Sintaxis corregida

    var expandedColor by remember { mutableStateOf(false) }
    var expandedTalla by remember { mutableStateOf(false) }
    var mostrarDialogoGuardar by remember { mutableStateOf(false) }

    val opcionesTallas = viewModel.tallas
    val categorias = viewModel.categorias
    val tags = viewModel.tags
    val temporadas = viewModel.temporadas
    val formalidades = viewModel.formalidades
    val colores = viewModel.colores

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var mostrarMenuFoto by remember { mutableStateOf(false) }

    // Lanzador para la galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
        }
    }

    // Lanzador para la camara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = tempImageUri
        }
    }

    if (mostrarMenuFoto) {
        AlertDialog(
            onDismissRequest = { mostrarMenuFoto = false },
            title = { Text("Agregar foto") },
            text = { Text("¿Desde dónde quieres agregar la foto de la prenda?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarMenuFoto = false
                    // Preparamos la URI temporal y lanzamos la cámara
                    val file = context.createImageFile()
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    tempImageUri = uri
                    cameraLauncher.launch(uri)
                }) {
                    Text("Cámara", color = Rosa)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarMenuFoto = false
                    // Lanzamos el Photo Picker de la galería
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text("Galería", color = Rosa)
                }
            }
        )
    }

    // dialogo de confirmacion
    if (mostrarDialogoGuardar) {
        ConfirmacionDialog(
            mensaje = "¿Deseas guardar la nueva prenda?",
            onCancelar = { mostrarDialogoGuardar = false },
            onConfirmar = {

                mostrarDialogoGuardar = false
                // armar dto
                val nuevaPrenda = PrendaRopa(
                    id = 0, // la bd sobreescribe el id
                    nombre = nombre,
                    tienda = tienda,
                    talla = talla,
                    color = color,
                    estampado = estampado,
                    categoria = categoria,
                    tags = tagsSeleccionadas.toList(),
                    temporada = temporadasSeleccionadas.toList(),
                    formalidad = formalidad,
                    imagen = imageUri?.toString(), // va la url d la imagen
                    favorito = false
                )

                viewModel.agregarPrenda(nuevaPrenda)
                onGuardado()
            }
        )
    }
    Scaffold(
        topBar = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(
            selected = 2, navController) },
        containerColor = BlancoFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Text(
                text = "Agregar Prenda",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            // contenedor de la foto
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Foto de la prenda",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF0EEF0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Checkroom,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = Color.LightGray
                            )
                        }
                    }

                    IconButton(
                        onClick = { mostrarMenuFoto = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .background(Color.White.copy(alpha = 0.7f), CircleShape)
                    ){
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Agregar foto",
                            tint = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // info de la prenda
            LookiestTextField("Nombre de la Prenda", nombre) { nombre = it }
            Spacer(Modifier.height(10.dp))
            LookiestTextField("Tienda/Marca", tienda) { tienda = it }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Talla
                Column(modifier = Modifier.weight(1f)) {
                    Text("Talla", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedTalla,
                        onExpandedChange = { expandedTalla = it }
                    ) {
                        OutlinedTextField(
                            value = talla,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTalla) },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F7),
                                unfocusedContainerColor = Color(0xFFF5F5F7),
                                focusedBorderColor = Color(0xFFD1D1D6),
                                unfocusedBorderColor = Color(0xFFD1D1D6),
                                focusedTextColor = Color(0xFFA73266),
                                unfocusedTextColor = Color(0xFFA73266)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTalla,
                            onDismissRequest = { expandedTalla = false }
                        ) {
                            opcionesTallas.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t) },
                                    onClick = { talla = t; expandedTalla = false }
                                )
                            }
                        }
                    }
                }

                // Color
                Column(modifier = Modifier.weight(1f)) {
                    Text("Color", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedColor,
                        onExpandedChange = { expandedColor = it }
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .menuAnchor(),
                            shape = RoundedCornerShape(50),
                            color = BlancoFondo,
                            onClick = { expandedColor = true },
                            border = BorderStroke(1.dp, Color(0xFFD1D1D6))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val colorVisual = colores.toMap()[color] ?: Color.LightGray

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = colorVisual,
                                            shape = CircleShape
                                        )
                                )

                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Agregar color",
                                    tint = Color.DarkGray
                                )
                            }
                        }

                        ExposedDropdownMenu(
                            expanded = expandedColor,
                            onDismissRequest = { expandedColor = false }
                        ) {
                            colores.forEach { (nombreC, valorC) ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier
                                                .size(16.dp)
                                                .background(valorC, CircleShape))
                                            Spacer(Modifier.width(8.dp))
                                            Text(nombreC)
                                        }
                                    },
                                    onClick = {
                                        color = nombreC
                                        expandedColor = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Estampado
            Text("Estampado", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = estampado,
                        onClick = { estampado = true },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Rosa,
                            unselectedColor = Color.Gray
                        )
                    )
                    Text("Sí", fontSize = 14.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !estampado,
                        onClick = { estampado = false },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Rosa,
                            unselectedColor = Color.Gray
                        )
                    )
                    Text("No", fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Categoría
            Text("Categoría", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { op ->
                    ChipSeleccionable(op, op == categoria) { categoria = op }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Tags extra
            SelectorMultiple("Tags Extra", tags, tagsSeleccionadas) { op ->
                tagsSeleccionadas = if (op in tagsSeleccionadas) {
                    tagsSeleccionadas - op
                } else {
                    tagsSeleccionadas + op
                }
            }
            Spacer(Modifier.height(12.dp))

            // Temporada
            SelectorMultiple("Temporada", temporadas, temporadasSeleccionadas) { op ->
                temporadasSeleccionadas = if (op in temporadasSeleccionadas) {
                    temporadasSeleccionadas - op
                } else {
                    temporadasSeleccionadas + op
                }
            }
            Spacer(Modifier.height(12.dp))

            // Formalidad
            Text("Formalidad", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                formalidades.forEach { op ->
                    ChipSeleccionable(op, op == formalidad) { formalidad = op }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Botones Guardar y Descartar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // descartar
                Button(
                    onClick = onDescartado,
                    colors = ButtonDefaults.buttonColors(containerColor = Rosa),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                ) { Text("Descartar") }

                // guardar
                Button(
                    onClick = { mostrarDialogoGuardar = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Amarillo),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                ) { Text("Guardar") }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

