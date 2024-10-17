package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.login

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import com.alcaldiasantaananorte.nortegojetpackcompose.model.rutas.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.login.LoginViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras.BloqueTextFieldLogin
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.TokenManager
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones.SolicitudTalaArbolViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.opciones.denuncias.DenunciaBasicaScreen
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.PrincipalScreen
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.opciones.medioambiente.SolicitudDenunciaTalaArbolView
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.opciones.solvencia.SolvenciaView
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.solicitudes.SolicitudesScreen

class SplashApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MODO VERTICAL
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        enableEdgeToEdge()
        setContent {
            // INICIO DE APLICACION
            AppNavigation()
        }
    }
}

// *** RUTAS DE NAVEGACION ***
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

        composable(Routes.VistaDenunciaBasica.route) { backStackEntry ->
            val _idTipoServicio = backStackEntry.arguments?.getString("idservicio") ?: "0"
            val idTipoServicio = _idTipoServicio.toIntOrNull() ?: 0

            val titulo = backStackEntry.arguments?.getString("titulo") ?: ""
            val descripcion = backStackEntry.arguments?.getString("descripcion") ?: ""

            DenunciaBasicaScreen(idTipoServicio, titulo, descripcion, navController)
        }

        composable(Routes.VistaSolicitudTalaArbol.route) { SolicitudDenunciaTalaArbolView(navController) }
        composable(Routes.VistaSolvencias.route) { SolvenciaView(navController) }
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









