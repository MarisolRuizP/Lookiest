package ruiz.marisol.lookiest.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
fun LoginScreen(

){
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ){
        Image(
            painter = painterResource(id = R.drawable.lookiest_logo),
            "Logo de la aplicación",
            modifier = Modifier.size(130.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Lookiest",
            fontWeight = FontWeight.SemiBold,
            fontSize = 50.sp
        )

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

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Inicio de sesión",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            modifier = Modifier
                .align(Alignment.Start),
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
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            modifier = Modifier
                .align(Alignment.Start),
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            text = "Contraseña"
        )
        TextField(
            value = pass,
            onValueChange = { pass = it },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
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
                .align(Alignment.End),
            fontSize = 12.sp,
            text = "Olvidaste tu contraseña?"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(49.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.mustard_yellow)
            )
        ) { Text(
            fontSize = 17.sp,
            text = "Ingresar") }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(49.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.blue)
            )
        ) { Text(
            fontSize = 17.sp,
            text = "Registrarme") }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    LoginScreen()
}