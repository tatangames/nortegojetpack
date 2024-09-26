package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.login

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModal2Botones
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.PhoneNumberVisualTransformation
import com.alcaldiasantaananorte.nortegojetpackcompose.model.rutas.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.LoginViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.TokenManager
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.PrincipalScreen
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.vistassolicitudes.SolicitudesScreen

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

    NavHost(navController = navController, startDestination = Routes.VistaSplash.route) {

        composable(Routes.VistaSplash.route) { SplashScreen(navController) }
        composable(Routes.VistaLogin.route) { LoginScreen(navController) }

        composable(Routes.VistaVerificarNumero.route) { backStackEntry ->
            val telefono = backStackEntry.arguments?.getString("telefono") ?: ""
            val _segundos = backStackEntry.arguments?.getString("segundos") ?: "0"
            val segundos = _segundos.toIntOrNull() ?: 0

            VistaVerificarNumeroView(navController = navController, telefono = telefono, segundos = segundos)
        }

        composable(Routes.VistaPrincipal.route) { PrincipalScreen(navController) }
        composable(Routes.VistaSolicitudes.route) { SolicitudesScreen(navController) }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {

    val ctx = LocalContext.current
    val tokenManager = remember { TokenManager(ctx) }
    val token by tokenManager.userToken.collectAsState(initial = "")

    // Evitar que el usuario volver al splash con el botón atrás
    DisposableEffect(Unit) {
        onDispose {
            navController.popBackStack(Routes.VistaSplash.route, true)
        }
    }

    // Control de la navegación tras un retraso
    LaunchedEffect(Unit) {
        delay(2000)

        if (token.isNotEmpty()) {
            navController.navigate(Routes.VistaPrincipal.route) {
                popUpTo(Routes.VistaSplash.route) { inclusive = true }
            }
        }else{
            navController.navigate(Routes.VistaLogin.route) {
                popUpTo(Routes.VistaSplash.route) { inclusive = true }
            }
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

@Composable
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel = viewModel()) {

    val ctx = LocalContext.current
    val telefono by viewModel.telefono.observeAsState("")
    val resultado by viewModel.resultado.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    var txtFieldNumero by remember { mutableStateOf(telefono) }

    // MODAL 1 BOTON
    var showModal1Boton by remember { mutableStateOf(false) }
    var modalMensajeString by remember { mutableStateOf("") }

    // MODAL 2 BOTON
    var showModal2Boton by remember { mutableStateOf(false) }

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

                    when {
                        txtFieldNumero.isBlank() -> {
                            modalMensajeString = ctx.getString(R.string.telefono_es_requerido)
                            showModal1Boton = true
                        }

                        txtFieldNumero.length < 8 -> { // VA SIN GUION
                            modalMensajeString = ctx.getString(R.string.telefono_es_requerido)
                            showModal1Boton = true
                        }
                        else -> {
                            // abrir modal para mostrarle al usuario si el numero es correcto
                            showModal2Boton = true
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

        if(showModal1Boton){
            CustomModal1Boton(showModal1Boton, modalMensajeString, onDismiss = {showModal1Boton = false})
        }

        if (isLoading) {
            LoadingModal(isLoading = isLoading)
        }

        if(showModal2Boton){
            CustomModal2Botones(
                showDialog = true,
                message = stringResource(id = R.string.verificar_numero_introducido, telefono),
                onDismiss = { showModal2Boton = false },
                onAccept = {
                    showModal2Boton = false
                    viewModel.verificarTelefono()
                }
            )
        }
    }

    resultado?.getContentIfNotHandled()?.let { result ->
        when (result.success) {

            1 -> {
                // Número bloqueado
                modalMensajeString = stringResource(id = R.string.numero_bloqueado)
                showModal1Boton = true
            }
            2 -> {
                val segundos = (result.segundos ?: 60).toString() // 60 por defecto

                LaunchedEffect(Unit) {
                    navController.navigate(Routes.VistaVerificarNumero.createRoute(telefono, segundos))
                }
            }
            else -> {
                // Error, mostrar Toast
                CustomToasty(ctx, stringResource(id = R.string.error_reintentar), ToastType.ERROR)
            }
        }
    }
}

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







