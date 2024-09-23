package com.alcaldiasantaananorte.nortegojetpackcompose.login


import android.util.Log
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.model.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VistaVerificarNumeroView(navController: NavHostController, telefono: String, segundos: Int){

    var txtFieldCodigo by remember { mutableStateOf("") }

    BarraToolbar(navController)

    /*Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
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

        OtpTextField(codigo = txtFieldCodigo, onTextChanged = { newText ->
            txtFieldCodigo = newText
        })

        Spacer(modifier = Modifier.height(35.dp))

        // Puedes agregar un botón para enviar el OTP
        Button(
            onClick = {


                      },
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

        Spacer(modifier = Modifier.height(35.dp))

        CountdownTimer(segundos = 3) {
            // Acción cuando se presiona "Reenviar código"
            // Aquí puedes agregar la lógica para reenviar el código o hacer alguna otra acción
        }
    }*/
}


@Composable
fun OtpTextField(codigo: String, onTextChanged: (String) -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = codigo,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, // Cambiado a Number para solo números
            imeAction = ImeAction.Done // Para evitar el botón "Enter"
        ),
        keyboardActions = KeyboardActions(
            onDone = {keyboardController?.hide() }
        ),
        onValueChange = { newText ->
            if (newText.length <= 6){
                onTextChanged(newText)
            }
        },
        singleLine = true
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(6) { index ->
                val number = when {
                    index >= codigo.length -> ""
                    else -> codigo[index]
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = number.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Box(modifier = Modifier.width(40.dp).height(2.dp)
                        .background(Color.Black))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraToolbar(navController: NavHostController){
    var isNavigating by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            // Usamos un Box para alinear el texto en el centro.
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Títu",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        },

        navigationIcon = {
            IconButton(onClick = {

                if (!isNavigating) {
                    isNavigating = true
                    navController.popBackStack()
                }

            },) {
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



@Composable
fun CountdownTimer(segundos: Int, onResendClick: () -> Unit) {
    var timerValue by remember { mutableStateOf(segundos) }
    var isCounting by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isCounting) {
        if (isCounting) {
            while (timerValue > 0) {
                delay(1000L)
                timerValue--
            }
            isCounting = false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isCounting) {
            Text(text = "Reenviar en $timerValue s")
        } else {
            TextButton(
                onClick = {
                    timerValue = 60 // Reiniciar el temporizador
                    isCounting = true
                    scope.launch { onResendClick() } // Acción al presionar "Reenviar código"
                }
            ) {
                Text(text = stringResource(id = R.string.reenviar_codigo),)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraToolbar2(navController: NavHostController) {



    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pantalla 2.",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        navigationIcon = {
            androidx.compose.material.IconButton(onClick = {


            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Volver a login "
                )
            }
        },
        modifier = Modifier.height(56.dp)
    )
}



@Preview(showBackground = true)
@Composable
fun PreviewVistaVerificarNumero() {
    val navController = rememberNavController()
    VistaVerificarNumeroView(navController, telefono = "+503 6666-6666", segundos = 20)
}