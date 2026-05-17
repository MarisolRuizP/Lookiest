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
import ruiz.marisol.lookiest.viewModel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email       by remember { mutableStateOf("") }
    var pass        by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }

    val isLoading    by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentUser  by viewModel.currentUser.collectAsState()
    val context      = LocalContext.current

    LaunchedEffect(currentUser) {
        if (currentUser != null) onLoginSuccess()
    }

    Scaffold(
        modifier       = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter            = painterResource(id = R.drawable.lookiest_logo),
                contentDescription = null,
                modifier           = Modifier.size(130.dp),
                colorFilter        = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Spacer(Modifier.height(10.dp))

            Text(
                "Lookiest",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 50.sp,
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

            Spacer(Modifier.height(30.dp))
            Text(
                "Iniciar Sesión",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 30.sp,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(30.dp))

            // Correo electrónico
            Text(
                "Correo electrónico",
                modifier   = Modifier.align(Alignment.Start),
                fontSize   = 16.sp,
                fontFamily = FontFamily.Monospace
            )
            TextField(
                value         = email,
                onValueChange = { email = it; viewModel.limpiarError() },
                shape         = RoundedCornerShape(20.dp),
                modifier      = Modifier.fillMaxWidth().height(56.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(15.dp))

            // Contraseña
            Text(
                "Contraseña",
                modifier   = Modifier.align(Alignment.Start),
                fontSize   = 16.sp,
                fontFamily = FontFamily.Monospace
            )
            TextField(
                value         = pass,
                onValueChange = { pass = it; viewModel.limpiarError() },
                shape         = RoundedCornerShape(20.dp),
                modifier      = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine            = true,
                visualTransformation  = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions       = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(
                            imageVector        = if (passVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Visibilidad"
                        )
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text      = errorMessage ?: "",
                color     = MaterialTheme.colorScheme.error,
                fontSize  = 13.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.login(email.trim(), pass.trim()) { success ->
                        if (!success) {
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
                    Text("Ingresar", fontSize = 17.sp)
                }
            }

            Spacer(Modifier.height(10.dp))

            TextButton(
                onClick  = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth().height(49.dp)
            ) {
                Text(
                    "Registrarme",
                    fontSize = 17.sp,
                    color    = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}