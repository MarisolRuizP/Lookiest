package ruiz.marisol.lookiest.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.ui.theme.components.BiometricHelper
import ruiz.marisol.lookiest.viewModel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToForgetPass: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val userName        by viewModel.username.collectAsState()
    val isRegistered    by viewModel.isLoggedIn.collectAsState()
    val biometriaActiva by viewModel.biometriaHabilitada.collectAsState()

    val isLoading    by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var email       by remember { mutableStateOf("") }
    var pass        by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }

    val context         = LocalContext.current
    val biometricHelper = remember { BiometricHelper(context) }

    LaunchedEffect(userName) {
        if (userName.isNotEmpty()) viewModel.verificarBiometria(userName)
    }

    Scaffold(
        modifier       = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.weight(1f))

            Image(
                painter            = painterResource(id = R.drawable.lookiest_logo),
                contentDescription = null,
                modifier           = Modifier.size(130.dp),
                colorFilter        = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Spacer(Modifier.height(10.dp))

            Text(
                text       = "Lookiest",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 50.sp,
                color      = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary,      fontWeight = FontWeight.Bold, fontSize = 20.sp)) { append("Dress") }
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp))                              { append(" Your ") }
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary,    fontWeight = FontWeight.Bold, fontSize = 20.sp)) { append("Best") }
                },
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.height(30.dp))

            Text(
                text       = if (isRegistered) "Bienvenido de vuelta\n$userName"
                else              "Iniciar Sesión",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 30.sp,
                textAlign  = TextAlign.Center,
                color      = if (isRegistered) MaterialTheme.colorScheme.secondary
                else              MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(60.dp))
            
            if (!isRegistered) {
                Text(
                    modifier   = Modifier.align(Alignment.Start),
                    fontSize   = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    text       = "Usuario"
                )
                TextField(
                    value         = email,
                    onValueChange = { email = it; viewModel.limpiarError() },
                    shape         = RoundedCornerShape(20.dp),
                    modifier      = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor   = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor  = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor   = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine      = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(Modifier.height(15.dp))
            }

            Text(
                modifier   = Modifier.align(Alignment.Start),
                fontSize   = 16.sp,
                fontFamily = FontFamily.Monospace,
                text       = "Contraseña"
            )
            TextField(
                value         = pass,
                onValueChange = { pass = it; viewModel.limpiarError() },
                shape         = RoundedCornerShape(20.dp),
                modifier      = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor  = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine           = true,
                visualTransformation = if (passVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(
                            imageVector        = if (passVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = "Visibilidad de contraseña"
                        )
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text      = errorMessage ?: "",
                    color     = MaterialTheme.colorScheme.error,
                    fontSize  = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        val usuarioAValidar = if (isRegistered) userName else email
                        if (usuarioAValidar.isBlank()) {
                            Toast.makeText(context, "Ingresa tu usuario", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.verificarSiExiste(usuarioAValidar) { existe ->
                                if (existe) {
                                    viewModel.cargarDatosUsuario(usuarioAValidar)
                                    onNavigateToForgetPass()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "El usuario '$usuarioAValidar' no está registrado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    },
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onSurface,
                text     = "Olvidaste tu contraseña?"
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (pass.isBlank()) {
                        Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (isRegistered) {
                        viewModel.login(userName.trim(), pass.trim()) { success ->
                            if (success) onLoginSuccess()
                            else Toast.makeText(context, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.login(email.trim(), pass.trim()) { success ->
                            if (success) onLoginSuccess()
                            else Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled  = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(49.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color    = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(text = "Ingresar", fontSize = 17.sp)
                }
            }

            Spacer(Modifier.height(10.dp))

            if (isRegistered && biometriaActiva) {
                Button(
                    onClick = {
                        biometricHelper.lanzarBiometria(
                            onSuccess = {
                                viewModel.loginConBiometria { success ->
                                    if (success) onLoginSuccess()
                                    else Toast.makeText(context, "Error al autenticar", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(49.dp)
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = "Usar huella", fontSize = 17.sp)
                }
            }

            if (isRegistered) {
                Button(
                    onClick  = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(49.dp)
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(text = "Ingresar con otra cuenta", fontSize = 17.sp)
                }
            }

            if (!isRegistered) {
                Button(
                    onClick  = onNavigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(49.dp)
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = "Registrarme", fontSize = 17.sp)
                }
            }

            Spacer(Modifier.weight(1.2f))
        }
    }
}