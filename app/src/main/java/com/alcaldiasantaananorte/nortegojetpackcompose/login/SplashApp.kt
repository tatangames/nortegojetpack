package com.alcaldiasantaananorte.nortegojetpackcompose.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.PhoneNumberVisualTransformation
import com.alcaldiasantaananorte.nortegojetpackcompose.model.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.NorteGoJetpackComposeTheme


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
        composable(Routes.VistaVerificarNumero.route) { vistaVerificarNumero(navController) }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {

    // Evitar que el usuario volver al splash con el botón atrás
    DisposableEffect(Unit) {
        onDispose {
            navController.popBackStack(Routes.VistaSplash.route, true)
        }
    }

    // Control de la navegación tras un retraso0
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





@Composable
fun LoginScreen(navController: NavHostController) {

    var txtFieldNumero by remember { mutableStateOf("") }

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
                text = stringResource(id = R.string.app_name), // Tu string aquí
                fontSize = 27.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center, // Alinea el texto al centro horizontalmente
                lineHeight = 40.sp,
                fontFamily = fontMonserratMedium
            )

            Spacer(modifier = Modifier.height(20.dp)) // Espacio entre el título y el listado

            BloqueTextFieldLogin(text = txtFieldNumero, onTextChanged = { newText ->
                txtFieldNumero = newText
            })

            Spacer(modifier = Modifier.height(50.dp)) // Espacio antes del botón

            // Botón de registro
            Button(
                onClick = {

                    navController.navigate(Routes.VistaVerificarNumero.route) {
                        // Asegúrate de mantener la pantalla de login en la pila de navegación
                        popUpTo(Routes.VistaLogin.route) { inclusive = false }
                        launchSingleTop = true
                    }

                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(
                        ContextCompat.getColor(
                            LocalContext.current,
                            R.color.c_azulv1
                        )
                    ),
                    contentColor = Color.White
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
    }
}

@Composable
fun BloqueTextFieldLogin(text: String, onTextChanged: (String) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)) // Bordes redondeados
            .background(Color(0xFFF5F5F5)) // Color de fondo
            .padding(6.dp), // Espaciado interno
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen de la bandera
        Image(
            painter = painterResource(id = R.drawable.flag_elsalvador), // Tu bandera
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


@Composable
fun vistaVerificarNumero(navController: NavHostController){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Otra Vista")
    }}


// vericar entradas
fun verificarEntradas(numero: String) {

    // numero es requerido

    // minimo 8 caracteres

    // verificar
}




