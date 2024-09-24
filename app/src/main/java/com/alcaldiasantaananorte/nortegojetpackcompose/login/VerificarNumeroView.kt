package com.alcaldiasantaananorte.nortegojetpackcompose.login


import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.model.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.ReintentoSMSViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VistaVerificarNumeroView(
    navController: NavHostController,
    telefono: String,
    segundos: Int,
    viewModel: ReintentoSMSViewModel = viewModel()
) {
    var txtFieldCodigo by remember { mutableStateOf("") }

    // Estado mutable para el temporizador
    var tiempoRestante by remember { mutableStateOf(8) } // Valor inicial
    var isCounting by remember { mutableStateOf(true) }

    // Asignar el teléfono al ViewModel para la llamada
    LaunchedEffect(telefono) {
        viewModel.setTelefono(telefono)
    }

    BarraToolbar(navController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.codigo_mensaje, telefono),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.charla),
            contentDescription = stringResource(id = R.string.logo),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(35.dp))

        OtpTextField(codigo = txtFieldCodigo, onTextChanged = { newText ->
            txtFieldCodigo = newText
        })

        Spacer(modifier = Modifier.height(35.dp))

        Button(
            onClick = {
                // Acción para verificar el código
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorAzulGob,
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.verificar),
                style = TextStyle(fontSize = 16.sp)
            )
        }

        Spacer(modifier = Modifier.height(35.dp))

        // CountdownTimer
        CountdownTimer(
            segundos = tiempoRestante,
            isCounting = isCounting,
            onResendClick = {
                // Llamada a la API cuando se presiona "Reenviar"
                viewModel.reitentoSMSRetrofit()
            }
        )

        // Mostrar modal de carga mientras se espera respuesta de la API
        val isLoading by viewModel.isLoading.observeAsState(false)
        if (isLoading) {
            LoadingModal(isLoading = isLoading)
        }

        // Observa el resultado de la API
        val resultado by viewModel.resultado.observeAsState()
        resultado?.getContentIfNotHandled()?.let { result ->
            when (result.success) {
                1 -> {
                    // Número bloqueado
                    Log.d("RESULTADO", "Número bloqueado.")
                }
                2 -> {
                    // Código reenviado con éxito
                    Log.d("RESULTADO", "Código reenviado con éxito.")
                    // Reiniciar el temporizador
                    tiempoRestante = 12 // Reiniciar el temporizador a 60
                    isCounting = true    // Reinicia la cuenta regresiva
                }
                else -> {
                    // Error al reenviar código
                    Log.d("RESULTADO", "Error al reenviar código.")
                }
            }
        }
    }
}

@Composable
fun CountdownTimer(
    segundos: Int,
    isCounting: Boolean,
    onResendClick: () -> Unit
) {
    // Valor del temporizador, que se reinicia cuando cambia el número de segundos
    var timerValue by remember { mutableStateOf(segundos) }

    // Lanzar el temporizador solo si está contando
    LaunchedEffect(isCounting) {
        if (isCounting) {
            timerValue = segundos // Reinicia el valor del temporizador
            while (timerValue > 0) {
                delay(1000L)
                timerValue--
            }
            // Detiene la cuenta regresiva al llegar a cero
        }
    }

    // Actualiza el estado de isCounting
    LaunchedEffect(timerValue) {
        if (timerValue == 0) {
            onResendClick() // Llama a la función de reenviar al llegar a cero
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (timerValue > 0) {
            Text(
                text = "Reenviar en $timerValue s",
                fontSize = 16.sp,
                color = Color.Black
            )
        } else {
            TextButton(onClick = onResendClick) {
                Text(
                    text = stringResource(id = R.string.reenviar_codigo),
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraToolbar(navController: NavHostController) {
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
            IconButton(
                onClick = {

                    if (!isNavigating) {
                        isNavigating = true
                        navController.popBackStack()
                    }

                },
            ) {
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
fun OtpTextField(codigo: String, onTextChanged: (String) -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = codigo,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, // Cambiado a Number para solo números
            imeAction = ImeAction.Done // Para evitar el botón "Enter"
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        onValueChange = { newText ->
            if (newText.length <= 6) {
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

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(Color.Black)
                    )
                }
            }
        }
    }
}






/*@Preview(showBackground = true)
@Composable
fun PreviewVistaVerificarNumero() {
    val navController = rememberNavController()
    VistaVerificarNumeroView(navController, telefono = "+503 6666-6666", segundos = 20)
}*/