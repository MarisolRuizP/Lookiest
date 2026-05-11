package ruiz.marisol.lookiest.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onLoginSuccess: () -> Unit,
){
    val userName by viewModel.username.collectAsState()
    val isRegistered by viewModel.isLoggedIn.collectAsState()
    val biometriaActiva by viewModel.biometriaHabilitada.collectAsState()

    val context = LocalContext.current
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    val biometricHelper = remember { BiometricHelper(context) }

    LaunchedEffect(userName) {
        if (userName.isNotEmpty()) {
            viewModel.verificarBiometria(userName)
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
                .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.lookiest_logo),
                contentDescription = null,
                modifier = Modifier.size(130.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Lookiest",
                fontWeight = FontWeight.SemiBold,
                fontSize = 50.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            val text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                ) {
                    append("Dress")
                }

                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp
                    )
                ) {
                    append(" Your ")
                }

                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                ) {
                    append("Best")
                }
            }

            Text(
                text = text,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = if (isRegistered) "Bienvenido de vuelta\n$userName" else "Iniciar Sesión",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                color = if (isRegistered) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(60.dp))

            if (!isRegistered) {
                Text(
                    modifier = Modifier.align(Alignment.Start),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    text = "Usuario"
                )

                TextField(
                    value = user,
                    onValueChange = { user = it },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(15.dp))
            }

            Text(
                modifier = Modifier.align(Alignment.Start),
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                text = "Contraseña"
            )
            TextField(
                value = pass,
                onValueChange = { pass = it },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        val icono = if (passVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        Icon(imageVector = icono, contentDescription = "Visibilidad de contraseña")
                    }
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        val usuarioAValidar = if (isRegistered) userName else user

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
                color = MaterialTheme.colorScheme.onSurface,
                text = "Olvidaste tu contraseña?",
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (isRegistered) {
                        if (pass.isBlank()) {
                            Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.loginConRoom(userName.trim(), pass.trim()) { success ->
                                if (success) {
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(context, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        if (user.isBlank() || pass.isBlank()) {
                            Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.loginConRoom(user.trim(), pass.trim()) { success ->
                                if (success) {
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(49.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text(fontSize = 17.sp, text = "Ingresar")
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isRegistered && biometriaActiva) {
                Button(
                    onClick = {
                        biometricHelper.lanzarBiometria(
                            onSuccess = {
                                viewModel.loginConBiometria { success ->
                                    if (success) onLoginSuccess()
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
                    Text(fontSize = 17.sp, text = "Usar huella")
                }
            }

            if (isRegistered) {
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(49.dp)
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(fontSize = 17.sp, text = "Ingresar con otra cuenta")
                }
            }

            if (!isRegistered) {
                Button(
                    onClick = { onNavigateToRegister() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(49.dp)
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(fontSize = 17.sp, text = "Registrarme")
                }
            }
            Spacer(modifier = Modifier.weight(1.2f))
        }
    }
}