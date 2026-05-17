
package ruiz.marisol.lookiest.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.ui.theme.components.CampoRegistro
import ruiz.marisol.lookiest.viewModel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: AuthViewModel,
    onRegistrationComplete: () -> Unit
) {
    var user          by remember { mutableStateOf("") }
    var nombres       by remember { mutableStateOf("") }
    var apellidos     by remember { mutableStateOf("") }
    var correo        by remember { mutableStateOf("") }
    var pass          by remember { mutableStateOf("") }
    var confirmarPass by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var genero        by remember { mutableStateOf("") }
    var expandido     by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val opcionesGenero  = listOf("Masculino", "Femenino", "Otro", "Prefiero no decirlo")

    val isLoading    by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context      = LocalContext.current

    // Validación local antes de llamar Firebase
    var errorLocal by remember { mutableStateOf("") }

    Scaffold(
        modifier       = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter            = painterResource(id = R.drawable.lookiest_logo),
                contentDescription = null,
                modifier           = Modifier.size(100.dp),
                colorFilter        = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Spacer(Modifier.height(10.dp))

            Text(
                "Lookiest",
                fontSize   = 38.sp,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 20.sp)) { append("Dress") }
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp)) { append(" Your ") }
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, fontSize = 20.sp)) { append("Best") }
                },
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(15.dp))

            CampoRegistro(label = "Nombre de usuario",   value = user,      onValueChange = { user = it })
            CampoRegistro(label = "Nombre(s)",           value = nombres,   onValueChange = { nombres = it })
            CampoRegistro(label = "Apellido(s)",         value = apellidos, onValueChange = { apellidos = it })
            CampoRegistro(label = "Correo Electrónico",  value = correo,    onValueChange = { correo = it; viewModel.limpiarError() })

            Spacer(Modifier.height(15.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                Box(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("Fecha de Nacimiento", fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value         = fechaNacimiento,
                            onValueChange = {},
                            readOnly      = true,
                            modifier      = Modifier.fillMaxWidth().clickable { mostrarCalendario = true },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor   = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor      = Color.Transparent,
                                unfocusedBorderColor    = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                    Box(modifier = Modifier.matchParentSize().padding(top = 25.dp).clickable { mostrarCalendario = true })
                }

                if (mostrarCalendario) {
                    DatePickerDialog(
                        onDismissRequest = { mostrarCalendario = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    fechaNacimiento = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                                }
                                mostrarCalendario = false
                            }) { Text("OK") }
                        }
                    ) { DatePicker(state = datePickerState) }
                }

                // Género
                Box(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("Género", fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.height(4.dp))
                        ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = !expandido }) {
                            TextField(
                                value         = genero,
                                onValueChange = {},
                                readOnly      = true,
                                modifier      = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                shape         = RoundedCornerShape(16.dp),
                                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor   = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedIndicatorColor   = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                            ExposedDropdownMenu(
                                expanded          = expandido,
                                onDismissRequest  = { expandido = false },
                                modifier          = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                opcionesGenero.forEach { opcion ->
                                    DropdownMenuItem(
                                        text    = { Text(opcion) },
                                        onClick = { genero = opcion; expandido = false },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                }
            }

            CampoRegistro(label = "Contraseña",          value = pass,          onValueChange = { pass = it; errorLocal = "" }, isPassword = true)
            CampoRegistro(label = "Confirmar Contraseña", value = confirmarPass, onValueChange = { confirmarPass = it; errorLocal = "" }, isPassword = true)

            Spacer(Modifier.height(8.dp))

            val mensajeError = errorLocal.ifBlank { errorMessage ?: "" }
            Text(
                text      = mensajeError,
                color     = MaterialTheme.colorScheme.error,
                fontSize  = 13.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    val camposIncompletos = user.isBlank() || correo.isBlank() || pass.isBlank() ||
                            nombres.isBlank() || apellidos.isBlank() ||
                            fechaNacimiento.isBlank() || genero.isBlank()

                    when {
                        camposIncompletos ->
                            errorLocal = "Por favor, llena todos los campos"
                        pass != confirmarPass ->
                            errorLocal = "Las contraseñas no coinciden"
                        pass.length < 6 ->
                            errorLocal = "La contraseña debe tener al menos 6 caracteres"
                        else -> {
                            errorLocal = ""
                            viewModel.registrar(
                                email       = correo.trim(),
                                password    = pass,
                                displayName = "$nombres $apellidos",
                                onSuccess = {
                                    Toast.makeText(context, "¡Usuario registrado con éxito!", Toast.LENGTH_SHORT).show()
                                    onRegistrationComplete()
                                },
                                onError = { error ->
                                }
                            )
                        }
                    }
                },
                enabled  = !isLoading,
                modifier = Modifier.fillMaxWidth().height(49.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color    = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Registrarme", fontSize = 17.sp)
                }
            }
        }
    }
}