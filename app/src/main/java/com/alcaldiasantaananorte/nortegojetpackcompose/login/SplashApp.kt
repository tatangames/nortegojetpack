package com.alcaldiasantaananorte.nortegojetpackcompose.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.PhoneNumberVisualTransformation
import com.alcaldiasantaananorte.nortegojetpackcompose.model.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import com.alcaldiasantaananorte.nortegojetpackcompose.pruebas.HomePage
import com.alcaldiasantaananorte.nortegojetpackcompose.pruebas.HomeViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorGris2Gob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorNegroGob
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.LoginViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


import android.content.Context
import androidx.compose.material.IconButton
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.MutableState
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import es.dmoral.toasty.Toasty

class SplashApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // INICIO DE APLICACION
            AppNavigation()
        }
    }
}



@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Pantalla1.route) {

        composable(Routes.Pantalla1.route) { Pantalla1(navController) }
        composable(Routes.Pantalla2.route) {
            Pantalla2(navController)
        }

        /* composable(Routes.VistaSplash.route) { SplashScreen(navController) }
        composable(Routes.VistaLogin.route) { LoginScreen(navController) }


        composable(Routes.Pantalla2.route) {
            Pantalla2(navController)
        }*/

       /* composable(
            route = Routes.VistaVerificarNumeroView.route,
            arguments = listOf(
                navArgument("telefono") { type = NavType.StringType },
                navArgument("segundos") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val telefono = backStackEntry.arguments?.getString("telefono") ?: ""
            val segundos = backStackEntry.arguments?.getInt("segundos") ?: 0
            VistaVerificarNumeroView(navController, telefono, segundos)
        }*/


    }
}






@Composable
fun Pantalla1(navController: NavHostController, viewModel: LoginViewModel = viewModel()) {
    val telefono by viewModel.telefono.observeAsState("")
    val resultado by viewModel.resultado.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    var txtFieldNumero by remember { mutableStateOf(telefono) }

    // MODAL
    var showModal1Boton by remember { mutableStateOf(false) }
    var modalMessage by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    val fontMonserratMedium = FontFamily(
        Font(R.font.montserratmedium)
    )


    val navigateToPantalla2 = remember { mutableStateOf(false) }




    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 25.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Imagen (logotipo)
            Image(
                painter = painterResource(id = R.drawable.logofinal),
                contentDescription = stringResource(id = R.string.logo),
                modifier = Modifier.size(199.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Texto (titulo)
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 27.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                fontFamily = fontMonserratMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            BloqueTextFieldLogin(text = txtFieldNumero, onTextChanged = { newText ->
                txtFieldNumero = newText
                viewModel.setTelefono(newText)  // Actualiza el ViewModel
            })

            Spacer(modifier = Modifier.height(50.dp))

            // Botón de registro
            Button(
                onClick = {

                    // navController.navigate(Routes.Pantalla2.route)

                    when {
                        txtFieldNumero.isBlank() -> {
                            modalMessage = ctx.getString(R.string.telefono_es_requerido)
                            showModal1Boton = true
                        }

                        txtFieldNumero.length < 8 -> { // VA SIN GUION
                            modalMessage = ctx.getString(R.string.logo)
                            showModal1Boton = true
                        }
                        else -> {
                            viewModel.verificarTelefono()
                        }
                    }


                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorAzulGob,
                    contentColor = ColorBlancoGob
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.verificar),
                    fontSize = 18.sp,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Mostrar el loading modal si isLoading es true
        if (isLoading) {
            LoadingModal(isLoading = isLoading)
        }
    }


    resultado?.let { result ->

        when (result.success) {
            1 -> {
                // Número bloqueado
                modalMessage = stringResource(id = R.string.numero_bloqueado)
                showModal1Boton = true
            }
            2 -> {
                CustomToasty(ctx, "entra", ToastType.INFO)
                navigateToPantalla2.value = true
                cambiar(navController, navigateToPantalla2)
               // navController.navigate(Routes.Pantalla2.route)
            }
            else -> {
                // Error, mostrar Toast
                CustomToasty(ctx, stringResource(id = R.string.error_reintentar), ToastType.ERROR)
            }
        }

        Log.i("RESULTADO", "resultado es v1: " + result.success)
    }


}


@Composable
fun cambiar(navController: NavHostController, navigateToPantalla2: MutableState<Boolean>){
    LaunchedEffect(Unit) {
        navController.navigate("pantalla2")
        navigateToPantalla2.value = false // Reinicia la navegación
    }
}





@Composable
fun Pantalla2(navController: NavHostController) {
    Scaffold(
        topBar = {
            BarraToolbar2(navController)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Pantalla 222", fontSize = 24.sp)
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraToolbar2(navController: NavHostController) {

    var isNavigating by remember { mutableStateOf(false) }

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
            IconButton (onClick = {
                if (!isNavigating) {
                    isNavigating = true
                    navController.popBackStack()
                    // Opcionalmente, resetea la bandera después de un delay
                }

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









/*
@Composable
fun SplashScreen(navController: NavHostController) {

    // Evitar que el usuario volver al splash con el botón atrás
    DisposableEffect(Unit) {
        onDispose {
            navController.popBackStack(Routes.VistaSplash.route, true)
        }
    }

    // Control de la navegación tras un retraso
    LaunchedEffect(key1 = true) {
        delay(2000)
        navController.navigate(Routes.VistaLogin.route) {
            popUpTo(Routes.VistaSplash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logofinal), // Tu imagen aquí
            contentDescription = stringResource(id = R.string.logo),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(200.dp)
        )
    }
}

*/

/*
@Composable
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel = viewModel()) {
    val telefono by viewModel.telefono.observeAsState("")
    val resultado by viewModel.resultado.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    var txtFieldNumero by remember { mutableStateOf(telefono) }

    // MODAL
    var showModal1Boton by remember { mutableStateOf(false) }
    var modalMessage by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    val fontMonserratMedium = FontFamily(
        Font(R.font.montserratmedium)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 25.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Imagen (logotipo)
            Image(
                painter = painterResource(id = R.drawable.logofinal),
                contentDescription = stringResource(id = R.string.logo),
                modifier = Modifier.size(199.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Texto (titulo)
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 27.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                fontFamily = fontMonserratMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            BloqueTextFieldLogin(text = txtFieldNumero, onTextChanged = { newText ->
                txtFieldNumero = newText
                viewModel.setTelefono(newText)  // Actualiza el ViewModel
            })

            Spacer(modifier = Modifier.height(50.dp))

            // Botón de registro
            Button(
                onClick = {

                   // navController.navigate(Routes.Pantalla2.route)

                     when {
                         txtFieldNumero.isBlank() -> {
                             modalMessage = ctx.getString(R.string.telefono_es_requerido)
                             showModal1Boton = true
                         }

                         txtFieldNumero.length < 8 -> { // VA SIN GUION
                             modalMessage = ctx.getString(R.string.logo)
                             showModal1Boton = true
                         }
                         else -> {
                             viewModel.verificarTelefono()
                         }
                     }





                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorAzulGob,
                    contentColor = ColorBlancoGob
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.verificar),
                    fontSize = 18.sp,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Mostrar el loading modal si isLoading es true
        if (isLoading) {
            LoadingModal(isLoading = isLoading)
        }
    }




    resultado?.let { result ->

        when (result.success) {
            1 -> {
                // Número bloqueado
                modalMessage = stringResource(id = R.string.numero_bloqueado)
                showModal1Boton = true
            }
            2 -> {
                CustomToasty(ctx, "entra", ToastType.INFO)
                val _segundos = result.segundos ?: 60 // defecto

                //navController.navigate(Routes.Pantalla2.route)
                LaunchedEffect(key1 = true) {
                    //delay(2000)
                    navController.navigate(Routes.Pantalla2.route)
                }

               /* navController.navigate(
                    Routes.VistaVerificarNumeroView.verificarNumeroConParametros(txtFieldNumero, 3)
                ) {
                    popUpTo(Routes.VistaLogin.route) { inclusive = false }
                    launchSingleTop = true
                }
*/
            }
            else -> {
                // Error, mostrar Toast
                CustomToasty(ctx, stringResource(id = R.string.error_reintentar), ToastType.ERROR)
            }
        }

        Log.i("RESULTADO", "resultado es v1: " + result.success)
    }
}
*/




@Composable
fun BloqueTextFieldLogin(text: String, onTextChanged: (String) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F5F5))
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.flag_elsalvador),
            contentDescription = stringResource(id = R.string.el_salvador),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Código del país
        Text(
            text = stringResource(id = R.string.area_pais),
            fontSize = 18.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
        ) {
            TextField(
                value = text,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                onValueChange = { newText ->
                    if (newText.length <= 8) {
                        onTextChanged(newText)
                    }
                },

                // transformar numeros para agregar el gion
                visualTransformation = PhoneNumberVisualTransformation(),

                textStyle = TextStyle(
                    fontSize = 18.sp, // Tamaño del texto
                    fontWeight = FontWeight.Medium // Negrita
                ),
                placeholder = { Text(text = stringResource(id = R.string.numero_telefono)) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    errorContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black
                ),
            )
        }
    }
}







