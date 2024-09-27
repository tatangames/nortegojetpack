package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
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
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.ReintentoSMSViewModel
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CountdownViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.TokenManager
import com.alcaldiasantaananorte.nortegojetpackcompose.model.rutas.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.VerificarCodigoViewModel
import kotlinx.coroutines.launch

@Composable
fun VistaVerificarNumeroView(
    navController: NavHostController,
    telefono: String,
    segundos: Int,
    viewModel: ReintentoSMSViewModel = viewModel(),
    viewModelCodigo: VerificarCodigoViewModel = viewModel()
) {
    var txtFieldCodigo by remember { mutableStateOf("") }
    val countdownViewModel = remember { CountdownViewModel() }
    val ctx = LocalContext.current

    // Mensajes de error y éxito predefinidos usando stringResource
    val msgCodigoRequerido = stringResource(id = R.string.codigo_requerido)
    val msgCodigoIncorrecto = stringResource(id = R.string.codigo_incorrecto)

    // MODAL
    var showModal1Boton by remember { mutableStateOf(false) }
    var modalMensajeString by remember { mutableStateOf("") }

    val tokenManager = remember { TokenManager(ctx) } // Recuerda inicializar el contexto
    val scope = rememberCoroutineScope()
    val isLoadingSMS by viewModel.isLoading.observeAsState(false)
    val isLoadingCodigo by viewModelCodigo.isLoading.observeAsState(false)
    val resultadoSMS by viewModel.resultado.observeAsState()

    // Asignar el teléfono al ViewModel para la llamada
    LaunchedEffect(telefono) {
        viewModel.setTelefono(telefono)
        viewModelCodigo.setTelefono(telefono)
        countdownViewModel.updateTimer(value = segundos)
    }



    // Estructura del Scaffold
    Scaffold(
        topBar = {
            BarraToolbar(navController, "")
        }
    ) { innerPadding ->
        // Contenido del Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Aplicar el padding proporcionado por el Scaffold
                .imePadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Ajuste a la parte superior
        ) {
            Spacer(modifier = Modifier.height(24.dp)) // Añade espacio adicional si es necesario

            Text(
                text = stringResource(R.string.codigo_mensaje, telefono),
                fontSize = 18.sp,
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
                viewModelCodigo.setCodigo(newText)
            })

            Spacer(modifier = Modifier.height(35.dp))

            Button(
                onClick = {
                    when {
                        txtFieldCodigo.isBlank() -> {
                            CustomToasty(ctx, msgCodigoRequerido, ToastType.ERROR)
                        }

                        txtFieldCodigo.length < 6 -> {
                            CustomToasty(ctx, msgCodigoRequerido, ToastType.ERROR)
                        }

                        else -> {
                            viewModelCodigo.verificarCodigoRetrofit()
                        }
                    }
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

            CountdownTimer(
                countdownViewModel = countdownViewModel,
                reintentoSMSViewModel = viewModel
            )

            if (isLoadingSMS) {
                LoadingModal(isLoading = isLoadingSMS)
            }

            if (isLoadingCodigo) {
                LoadingModal(isLoading = isLoadingCodigo)
            }


            if(showModal1Boton){
                CustomModal1Boton(showModal1Boton, modalMensajeString, onDismiss = {showModal1Boton = false})
            }



            resultadoSMS?.getContentIfNotHandled()?.let { result ->
                when (result.success) {
                    1 -> {
                        // numero bloqueado
                        modalMensajeString = stringResource(id = R.string.numero_bloqueado)
                        showModal1Boton = true
                    }
                    2 -> {
                        countdownViewModel.resetTimer()
                    }
                    else -> {
                        // Error, mostrar Toast
                        CustomToasty(ctx, stringResource(id = R.string.error_reintentar), ToastType.ERROR)
                    }
                }
            }

            val resultadoCodigo by viewModelCodigo.resultado.observeAsState()
            resultadoCodigo?.getContentIfNotHandled()?.let { result ->
                when (result.success) {
                    1 -> {
                        val _token = result.token ?: ""
                        val _id = (result.id ?: 0).toString()

                        scope.launch {
                            tokenManager.saveToken(_token)
                            tokenManager.saveID(_id)

                            navController.navigate(Routes.VistaPrincipal.route) {
                                popUpTo(0) { // Esto elimina todas las vistas de la pila de navegación
                                    inclusive = true // Asegura que ninguna pantalla anterior quede en la pila
                                }
                                launchSingleTop = true // Evita múltiples instancias de la misma vista
                            }
                        }
                    }
                    else -> {
                        CustomToasty(ctx, msgCodigoIncorrecto, ToastType.ERROR)
                    }
                }
            }
        }
    }
}


@Composable
fun CountdownTimer(
    countdownViewModel: CountdownViewModel,
    reintentoSMSViewModel: ReintentoSMSViewModel // Pasar el ViewModel que maneja la lógica de la API
) {

    Text(
        text = if (countdownViewModel.timer > 0) {
            "Reintentar en ${countdownViewModel.timer} segundos"
        } else {
            "Reenviar código"
        },
        modifier = Modifier
            .padding(top = 16.dp)
            .clickable(enabled = countdownViewModel.timer == 0) {
                // Acción al hacer clic en el texto
                if (countdownViewModel.timer == 0) {
                    // Llamar al método del ViewModel para hacer la solicitud de reenvío
                    reintentoSMSViewModel.reitentoSMSRetrofit()
                }
            },
        style = TextStyle(
            fontSize = 15.sp, // Cambia el tamaño del texto a 18sp
            color = if (countdownViewModel.timer > 0) Color.Gray else Color.Black
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraToolbar(navController: NavController, titulo: String) {
    var isNavigating by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            // Usamos un Box para alinear el texto en el centro.
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = titulo,
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
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.volver)
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