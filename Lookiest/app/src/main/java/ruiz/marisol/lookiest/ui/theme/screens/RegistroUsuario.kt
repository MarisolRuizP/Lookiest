package ruiz.marisol.lookiest.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ruiz.marisol.lookiest.R

@Composable
fun RegistroScreen(

){
    var user by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirmarPass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.drawable.lookiest_logo),
            "Logo de la aplicación",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        
        
        Text(text = "Lookiest", fontSize = 38.sp, fontWeight = FontWeight.Bold)

        val text = buildAnnotatedString {
            withStyle(style = SpanStyle(
                color = Color(0xFF005681),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            ) {
                append("Dress")
            }

            withStyle(style = SpanStyle(
                color = Color.Black,
                fontSize = 20.sp
            )) {
                append(" Your ")
            }

            withStyle(style = SpanStyle(
                color = Color(0xFFA63968),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )) {
                append("Best")
            }
        }
        Text(
            text = text,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(15.dp))

        CampoRegistro(label = "Nombre de usuario", value = user, onValueChange = { user = it })

        CampoRegistro(label = "Nombre(s)", value = nombres, onValueChange = { nombres = it })

        CampoRegistro(label = "Apellido(s)", value = apellidos, onValueChange = { apellidos = it })

        CampoRegistro(label = "Correo Electrónico", value = correo, onValueChange = { correo = it })

        Spacer(modifier = Modifier.height(15.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                CampoRegistro(label = "Fecha de Nacimiento", value = fechaNacimiento, onValueChange = {fechaNacimiento = it})
            }
            Box(modifier = Modifier.weight(1f)) {
                CampoRegistro(label = "Género", value = genero, onValueChange = {genero = it})
            }
        }

        CampoRegistro(label = "Contraseña", value = pass, onValueChange = {pass = it}, isPassword = true)

        CampoRegistro(label = "Confirmar Contraseña", value = confirmarPass, onValueChange = {confirmarPass = it}, isPassword = true)

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(49.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.mustard_yellow)
            )
        ) { Text(
            fontSize = 17.sp,
            text = "Registrarme") }
    }
}

@Composable
fun CampoRegistro(label: String, value: String, onValueChange: (String) -> Unit, isPassword: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegistroScreenPreview(){
    RegistroScreen()
}