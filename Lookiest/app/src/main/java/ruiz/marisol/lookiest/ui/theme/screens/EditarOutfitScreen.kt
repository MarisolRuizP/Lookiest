package ruiz.marisol.lookiest.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.ui.theme.*
import ruiz.marisol.lookiest.ui.theme.components.*
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

@Composable
fun EditarOutfitScreen(
    outfitInicial: Outfit,
    viewModel: ClosetViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onGuardado:   () -> Unit = {},
    onDescartado: () -> Unit = {},
    navController: NavController,
) {
    // Estado pre-cargado con los datos del outfit existente
    var nombre               by remember { mutableStateOf(outfitInicial.nombre) }
    var esPublico            by remember { mutableStateOf(outfitInicial.esPublico) }
    var etiquetaTexto        by remember { mutableStateOf("") }
    var mostrarDialogoGuardar by remember { mutableStateOf(false) }

    var prendasSeleccionadas by remember {
        mutableStateOf(
            outfitInicial.prendas
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .toSet()
        )
    }

    var etiquetas by remember {
        mutableStateOf(
            outfitInicial.etiquetas
                .split(",")
                .filter { it.isNotBlank() }
        )
    }

    val allPrendas by viewModel.prendas.collectAsState(initial = emptyList())


    if (mostrarDialogoGuardar) {
        ConfirmacionDialog(
            mensaje     = "¿Deseas guardar los cambios?",
            onCancelar  = { mostrarDialogoGuardar = false },
            onConfirmar = {
                mostrarDialogoGuardar = false
                val prendasDelOutfit = allPrendas.filter { it.id in prendasSeleccionadas }
                val urls = prendasDelOutfit.mapNotNull { it.imagen }.filter { it.isNotEmpty() }

                val outfitActualizado = outfitInicial.copy(
                    nombre       = nombre.ifBlank { "Mi Outfit" },
                    prendas      = prendasSeleccionadas.joinToString(","),
                    esPublico    = esPublico,
                    etiquetas    = etiquetas.joinToString(","),
                    imagenesUrls = urls  // ← nuevo
                )
                viewModel.actualizarOutfit(outfitActualizado)
                onGuardado()
            }
        )
    }

    Scaffold(
        topBar    = { LookiestTopBar() },
//        bottomBar = { LookiestBottomBar(selected = 1, navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "Editar Outfit",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(bottom = 10.dp)
            )

            LookiestTextField(
                label         = "Nombre del Outfit",
                value         = nombre,
                onValueChange = { nombre = it }
            )

            Spacer(Modifier.height(10.dp))

            // Grid con todas las prendas del closet; las del outfit ya vienen marcadas
            LazyVerticalGrid(
                columns               = GridCells.Fixed(2),
                verticalArrangement   = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier              = Modifier.weight(1f)
            ) {
                items(allPrendas) { prenda ->
                    val seleccionada = prenda.id in prendasSeleccionadas
                    PrendaSeleccionableCard(
                        prenda       = prenda,
                        seleccionada = seleccionada,
                        onClick = {
                            prendasSeleccionadas =
                                if (seleccionada) prendasSeleccionadas - prenda.id
                                else              prendasSeleccionadas + prenda.id
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Toggle público / privado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Outfit Público", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = esPublico,
                        onClick  = { esPublico = true },
                        colors   = RadioButtonDefaults.colors(selectedColor = Rosa)
                    )
                    Text("Sí", fontSize = 14.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !esPublico,
                        onClick  = { esPublico = false },
                        colors   = RadioButtonDefaults.colors(selectedColor = Rosa)
                    )
                    Text("No", fontSize = 14.sp)
                }
            }

            // Etiquetas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                Text("Etiquetas", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = {
                    val nueva = etiquetaTexto.trim()
                    if (nueva.isNotBlank() && nueva !in etiquetas) {
                        etiquetas     = etiquetas + nueva
                        etiquetaTexto = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Rosa)
                }
                OutlinedTextField(
                    value         = etiquetaTexto,
                    onValueChange = { etiquetaTexto = it },
                    placeholder   = { Text("Nueva etiqueta", fontSize = 12.sp) },
                    modifier      = Modifier.weight(1f).height(50.dp),
                    shape         = RoundedCornerShape(50),
                    singleLine    = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor   = Rosa
                    )
                )
            }

            if (etiquetas.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    etiquetas.forEach { tag ->
                        AssistChip(
                            onClick = { etiquetas = etiquetas - tag },
                            label   = { Text(tag, fontSize = 12.sp) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botones Descartar / Guardar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick  = onDescartado,
                    colors   = ButtonDefaults.buttonColors(containerColor = Rosa),
                    shape    = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Descartar", fontWeight = FontWeight.Bold) }

                Button(
                    onClick  = { mostrarDialogoGuardar = true },
                    colors   = ButtonDefaults.buttonColors(containerColor = Amarillo),
                    shape    = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Guardar", fontWeight = FontWeight.Bold) }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}