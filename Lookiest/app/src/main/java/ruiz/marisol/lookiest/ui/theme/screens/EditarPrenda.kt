package ruiz.marisol.lookiest.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.ui.theme.Amarillo
import ruiz.marisol.lookiest.ui.theme.BlancoFondo
import ruiz.marisol.lookiest.ui.theme.LookiestTheme
import ruiz.marisol.lookiest.ui.theme.Negro
import ruiz.marisol.lookiest.ui.theme.Rosa
import ruiz.marisol.lookiest.ui.theme.components.ChipSeleccionable
import ruiz.marisol.lookiest.ui.theme.components.ConfirmacionDialog
import ruiz.marisol.lookiest.ui.theme.components.LookiestBottomBar
import ruiz.marisol.lookiest.ui.theme.components.LookiestTextField
import ruiz.marisol.lookiest.ui.theme.components.LookiestTopBar
import ruiz.marisol.lookiest.ui.theme.components.SelectorMultiple
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditarPrendaScreen(
    prendaInicial: PrendaRopa,
    viewModel: ClosetViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onGuardado: () -> Unit = {},
    onDescartado: () -> Unit = {}
) {
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

    if (mostrarDialogoGuardar) {
        ConfirmacionDialog(
            mensaje     = "¿Deseas guardar los cambios?",
            onCancelar  = { mostrarDialogoGuardar = false },
            onConfirmar = {
                mostrarDialogoGuardar = false
                // viewModel.actualizarPrenda(...) — agregar cuando tengas la función
                onGuardado()
            }
        )
    }

    Scaffold(
        topBar  = { LookiestTopBar() },
        bottomBar = { LookiestBottomBar(selected = 2) },
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
                text = "Editar Prenda",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // foto d la prenda
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (prendaInicial.imagen != null) {
                        Image(
                            painter = painterResource(id = prendaInicial.imagen),
                            contentDescription = prendaInicial.nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
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
                        onClick  = { /* abrir galería/cámara */ },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = Color.DarkGray
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
                            color = BlancoFondo,
                            onClick = { expandedColor = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = try { Color(android.graphics.Color.parseColor(color)) } catch(e: Exception) { Color.Gray },
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
                            unselectedColor = Color.Gray
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
                            unselectedColor = Color.Gray
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

            // tags extra
            SelectorMultiple("Tags Extra", tags, tagsSeleccionadas) { op ->
                tagsSeleccionadas = (if (op in tags) tags - op else tags + op) as Set<String>
            }
            Spacer(Modifier.height(12.dp))

            // temporada
            SelectorMultiple("Temporada", temporadas, temporadasSeleccionadas) { op ->
                temporadasSeleccionadas = (if (op in temporadas) temporadas - op else temporadas + op) as Set<String>
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



@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewEditarPrenda() {

    val prendaDePrueba = PrendaRopa(
        id = 1,
        nombre = "Chaqueta de Vinipiel",
        tienda = "Zara",
        talla = "M",
        color = "Rojo",
        estampado = false,
        categoria = "OuterWear",
        tags = listOf("Leather"),
        temporada = listOf("Otoño", "Invierno"),
        formalidad = "Casual",
        imagen = null
    )
    
    LookiestTheme {
        EditarPrendaScreen(
            prendaInicial = prendaDePrueba,
            onGuardado = {},
            onDescartado = {}
        )
    }
}