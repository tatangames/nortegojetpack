package com.alcaldiasantaananorte.nortegojetpackcompose.login


import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.GreyDark
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.GreyLight

@Composable
fun VistaVerificarNumero(navController: NavHostController, telefono: String, segundos: Int){

    var otp by remember { mutableStateOf("") }

   BarraToolbar(navController)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Imagen centrada
        Text(
            text = stringResource(R.string.codigo_mensaje, telefono),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el texto y la imagen

        // Imagen centrada debajo del texto
        Image(
            painter = painterResource(id = R.drawable.charla), // Reemplaza con tu recurso de imagen
            contentDescription = stringResource(id = R.string.logo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp) // Tamaño de la imagen
        )

        Spacer(modifier = Modifier.height(35.dp)) // Espacio entre la imagen y el TextField

        OTPInput(otp = otp, onOtpChange = { otp = it })

        Spacer(modifier = Modifier.height(35.dp))

        // Puedes agregar un botón para enviar el OTP
        Button(
            onClick = { /* Manejar clic */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorAzulGob, // Color de fondo
                contentColor = Color.White // Color del texto (puedes usar ColorTexto si lo defines)
            )
        ) {
            Text(
                text = "Verificar",
                style = TextStyle(fontSize = 16.sp) // Ajusta el tamaño de la fuente según sea necesario
            )
        }



    }
}


@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNext: () -> Unit
) {
    TextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= 1) {
                onValueChange(newValue)
                if (newValue.isNotEmpty()) {
                    onNext() // Mueve al siguiente campo al ingresar un número
                }
            }
        },
        modifier = modifier
            .size(56.dp) // Ajusta el tamaño del TextField
            .padding(4.dp), // Espaciado opcional
        textStyle = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = VisualTransformation.None, // Texto visible
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

@Composable
fun OTPInput(
    otp: String,
    onOtpChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Crea un campo para cada dígito del OTP
        for (i in 0 until 6) {
            val currentChar = if (i < otp.length) otp[i].toString() else ""
            OtpTextField(
                value = currentChar,
                onValueChange = { newValue ->
                    // Actualiza el OTP completo
                    val newOtp = otp.toMutableList()
                    if (newValue.isNotEmpty()) {
                        newOtp[i] = newValue.first()
                    } else {
                        newOtp[i] = ' ' // O puedes dejarlo como un espacio
                    }
                    onOtpChange(newOtp.joinToString(""))
                },
                modifier = Modifier.size(50.dp), // Ajusta el tamaño según sea necesario
                onNext = {
                    // Mueve el foco al siguiente campo
                    if (i < 5) {
                        // Lógica para cambiar al siguiente TextField
                        // Esto se maneja en la lógica del controlador de entrada
                    }
                }
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraToolbar(navController: NavHostController){
    var isBackButtonEnabled by remember { mutableStateOf(true) }
    TopAppBar(
        title = {
            // Usamos un Box para alinear el texto en el centro.
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Título Centrado",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                if (isBackButtonEnabled) {
                    isBackButtonEnabled = false // Deshabilitar el botón
                    navController.popBackStack()
                }
            }, enabled = isBackButtonEnabled ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        },
        actions = {
            // Puedes agregar acciones adicionales aquí
        },
        modifier = Modifier.height(56.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewVistaVerificarNumero() {
    val navController = rememberNavController()
    VistaVerificarNumero(navController, telefono = "+503 6666-6666", segundos = 20)
}