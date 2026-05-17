package ruiz.marisol.lookiest.ui.theme.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.ui.theme.Amarillo
import ruiz.marisol.lookiest.ui.theme.Rosa
import ruiz.marisol.lookiest.ui.theme.components.ChipSeleccionable
import ruiz.marisol.lookiest.ui.theme.components.ConfirmacionDialog
import ruiz.marisol.lookiest.ui.theme.components.LookiestBottomBar
import ruiz.marisol.lookiest.ui.theme.components.LookiestTextField
import ruiz.marisol.lookiest.ui.theme.components.LookiestTopBar
import ruiz.marisol.lookiest.ui.theme.components.SelectorMultiple
import ruiz.marisol.lookiest.ui.theme.components.rememberCameraHandler
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditarPrendaScreen(
    prendaInicial: PrendaRopa,
    viewModel: ClosetViewModel,
    onGuardado: () -> Unit = {},
    onDescartado: () -> Unit = {},
    navController: NavController
) {

    val context = LocalContext.current

    var imageUri by remember {
        mutableStateOf(prendaInicial.imagen?.let { Uri.parse(it) })
    }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var mostrarMenuFoto by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
        }
    }

    val cameraHandler = rememberCameraHandler { uri ->
        imageUri = uri
    }

    var nombre by remember { mutableStateOf(prendaInicial.nombre) }
    var tienda by remember { mutableStateOf(prendaInicial.tienda) }
    var talla by remember { mutableStateOf(prendaInicial.talla) }
    var color by remember { mutableStateOf(prendaInicial.color) }
    var estampado by remember { mutableStateOf(prendaInicial.estampado) }
    var categoria by remember { mutableStateOf(prendaInicial.categoria) }
    var tagsSeleccionadas  by remember { mutableStateOf(prendaInicial.tags.toSet()) }
    var temporadasSeleccionadas by remember { mutableStateOf(prendaInicial.temporada.toSet()) }
    var formalidad by remember { mutableStateOf(prendaInicial.formalidad) }
    var expandedColor by remember { mutableStateOf(false) }

    var expandedTalla by remember { mutableStateOf(false) }
    var mostrarDialogoGuardar by remember { mutableStateOf(false) }

    val opcionesTallas = viewModel.tallas
    val categorias = viewModel.categorias
    val tags = viewModel.tags
    val temporadas = viewModel.temporadas
    val formalidades = viewModel.formalidades
    val colores = viewModel.colores


    if (mostrarMenuFoto) {
        AlertDialog(
            onDismissRequest = { mostrarMenuFoto = false },
            title = { Text("Agregar foto") },
            text = { Text("¿Desde dónde quieres agregar la foto de la prenda?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarMenuFoto = false
                    val uriSegura = cameraHandler.obtenerUri()
                    if (uriSegura != null) {
                        cameraHandler.launcher.launch(uriSegura)
                    }
                }) {
                    Text("Cámara", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarMenuFoto = false
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text("Galería", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    if (mostrarDialogoGuardar) {
        ConfirmacionDialog(
            mensaje     = "¿Deseas guardar los cambios?",
            onCancelar  = { mostrarDialogoGuardar = false },
            onConfirmar = {
                mostrarDialogoGuardar = false

                val prendaActualizada = prendaInicial.copy(
                    nombre = nombre,
                    tienda = tienda,
                    talla = talla,
                    color = color,
                    estampado = estampado,
                    categoria = categoria,
                    tags = tagsSeleccionadas.toList(),
                    temporada = temporadasSeleccionadas.toList(),
                    formalidad = formalidad,
                    imagen = imageUri?.toString()
                )
                viewModel.actualizarPrenda(prendaActualizada)
                onGuardado()
            }
        )
    }

    Scaffold(
        topBar  = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(
            selected = 2, navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Text(
                text = "Editar Prenda",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // foto d la prenda
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Checkroom,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    IconButton(
                        onClick  = { mostrarMenuFoto = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // textfields
            LookiestTextField("Nombre de la Prenda", nombre) { nombre = it }
            Spacer(Modifier.height(10.dp))
            LookiestTextField("Tienda/Marca", tienda) { tienda = it }

            Spacer(Modifier.height(14.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // talla
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
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.primary,
                                unfocusedTextColor = MaterialTheme.colorScheme.primary
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

                // colores
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
                            color = MaterialTheme.colorScheme.surface,
                            onClick = { expandedColor = true }
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
                                    contentDescription = "Cambiar color",
                                    tint = MaterialTheme.colorScheme.onSurface
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
                                            Box(modifier = Modifier.size(16.dp).background(valorC, CircleShape))
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

            // estampado
            Text("Estampado", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                // Opción Sí
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = estampado,
                        onClick = { estampado = true },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Rosa,
                            unselectedColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text("Sí", fontSize = 14.sp)
                }

                // Opción No
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !estampado,
                        onClick = { estampado = false },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Rosa,
                            unselectedColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text("No", fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(14.dp))


            // categoria
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

            SelectorMultiple("Tags Extra", tags, tagsSeleccionadas) { op ->
                tagsSeleccionadas = if (op in tagsSeleccionadas) {
                    tagsSeleccionadas - op
                } else {
                    tagsSeleccionadas + op
                }
            }
            Spacer(Modifier.height(12.dp))

            SelectorMultiple("Temporada", temporadas, temporadasSeleccionadas) { op ->
                temporadasSeleccionadas = if (op in temporadasSeleccionadas) {
                    temporadasSeleccionadas - op
                } else {
                    temporadasSeleccionadas + op
                }
            }
            Spacer(Modifier.height(12.dp))

            // formalidad
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

            // botones guardar y descartar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDescartado,
                    colors = ButtonDefaults.buttonColors(containerColor = Rosa),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                ) { Text("Descartar") }

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