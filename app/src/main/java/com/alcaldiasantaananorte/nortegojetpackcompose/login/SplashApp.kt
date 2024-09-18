package com.alcaldiasantaananorte.nortegojetpackcompose.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.model.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.NorteGoJetpackComposeTheme


class SplashApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySplash()
        }
    }
}

@Composable
fun MySplash() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.VistaSplash.route) {
        composable(Routes.VistaSplash.route) { SplashScreen(navController) }
        composable(Routes.VistaLogin.route) { LoginScreen() }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    // Evita volver al splash con el botón atrás
    DisposableEffect(Unit) {
        onDispose {
            navController.popBackStack("splash", true)
        }
    }

    // Control de la navegación tras un retraso
    LaunchedEffect(key1 = true) {
        delay(3000) // 3 segundos de retraso
        navController.navigate(Routes.VistaLogin.route) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logofinal), // Tu imagen aquí
            contentDescription = "Splash Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(200.dp) // Tamaño máximo de la imagen
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 25.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Habilita scroll
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp)) // Margen superior

            // Imagen (logotipo)
            Image(
                painter = painterResource(id = R.drawable.logofinal), // Tu imagen
                contentDescription = "Logo",
                modifier = Modifier.size(199.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(15.dp)) // Espacio entre la imagen y el texto

            // Texto (titulo)
            Text(
                text = "App Name", // Tu string aquí
                fontSize = 28.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(20.dp)) // Espacio entre el título y el listado

            // Layout para el listado de país y número de teléfono
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen de la bandera
                Image(
                    painter = painterResource(id = R.drawable.flag_elsalvador), // Tu bandera
                    contentDescription = "El Salvador",
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Código del país
                Text(
                    text = "+503",
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Campo de texto para el teléfono
                BasicTextField(
                    value = "", // Tu valor aquí
                    onValueChange = { /* Manejar el cambio de texto */ },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(50.dp)) // Espacio antes del botón

            // Botón de registro
            Button (
                onClick = { /* Acción del botón */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Ingresar",
                    fontSize = 18.sp
                )
            }
        }
    }
}



@Composable
fun DefaultPreview() {
    NorteGoJetpackComposeTheme {
        SplashScreen(rememberNavController())
    }
}