package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModalCerrarSesion
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModalUpdateApp
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.SolicitarPermisosUbicacion
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras.DrawerBody
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras.DrawerHeader
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras.ServicioCard
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.TokenManager
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.itemsMenu
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.TipoServicio
import com.alcaldiasantaananorte.nortegojetpackcompose.model.rutas.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import com.alcaldiasantaananorte.nortegojetpackcompose.providers.AuthProvider
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorGris1Gob
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones.ServiciosViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.opciones.denuncias.redireccionarAjustes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

import com.onesignal.OneSignal;



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(
    navController: NavHostController,
    viewModel: ServiciosViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showModalCerrarSesion by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.observeAsState(false)
    val tokenManager = remember { TokenManager(ctx) }
    val resultado by viewModel.resultado.observeAsState()
    val scope = rememberCoroutineScope() // Crea el alcance de coroutine
    var imageUrls by remember { mutableStateOf(listOf<String>()) }
    var modeloListaServicios by remember { mutableStateOf(listOf<TipoServicio>()) }

    val phoneNumberDenuncias = "+50369886392"
    val uri = Uri.parse("https://wa.me/${phoneNumberDenuncias.replace("+", "")}")
    var showToastErrorWhats by remember { mutableStateOf(false) }
    var popNumeroBloqueado by remember { mutableStateOf(false) }
    var popNuevaActializacion by remember { mutableStateOf(false) }
    val popErrorLoginFirebase = remember { mutableStateOf(false) }
    var boolDatosCargados by remember { mutableStateOf(false) }
    var popPermisoGPS by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val versionLocal = getVersionName(ctx)
    val authProvider = AuthProvider()


    //  ES PARA VERIFICAR PERMISOS DE UBICACION CUANDO SE CARGUE LA PANTALLA,
    // UTILIZADO PARA CUANDO SE ABRE EL MAPA DE RECOLECTOR EN VIVO
    if(boolDatosCargados){
        SolicitarPermisosUbicacion(
            onPermisosConcedidos = { },
            onPermisosDenegados = { }
        )
    }

    LaunchedEffect(Unit) {
        scope.launch {
            val _token = tokenManager.userToken.first()
            val idonesignal = getOneSignalUserId()
            viewModel.serviciosRetrofit(_token, idonesignal)
        }
    }

    // ocultar teclado
    keyboardController?.hide()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                DrawerBody(items = itemsMenu) { item ->
                    when (item.id) {
                        1 -> {

                            navController.navigate(Routes.VistaSolicitudes.route) {
                                navOptions {
                                    launchSingleTop = true
                                }
                            }
                        }

                        2 -> {
                            // agenda
                            navController.navigate(Routes.VistaAgenda.route) {
                                navOptions {
                                    launchSingleTop = true
                                }
                            }
                        }

                        3 -> {
                            // cerrar sesion
                            showModalCerrarSesion = true
                        }
                    }

                    scope.launch {
                        drawerState.close()
                    }
                }

                // Spacer para empujar el contenido hacia arriba
                Spacer(modifier = Modifier.weight(1f))

                // Texto de la versión
                Text(
                    text = "Versión " + getVersionName(ctx),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    ) {

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.servicios),
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Gray,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                  //  .statusBarsPadding()
            ) {
                // Sección para el HorizontalPager
                if (imageUrls.isNotEmpty()) {
                    item {
                        val pagerState = rememberPagerState(pageCount = { imageUrls.size })
                        Column {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(top = 16.dp)
                            ) { page ->
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(imageUrls[page])
                                            .crossfade(true)
                                            .placeholder(R.drawable.spinloading)
                                            .error(R.drawable.errorcamara)
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(16f / 9f),
                                        contentScale = ContentScale.Inside
                                    )
                                }
                            }

                            // Indicadores de página
                            Row(
                                Modifier
                                    .height(50.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(pagerState.pageCount) { iteration ->
                                    val color =
                                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                                    Box(
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .size(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Aquí comienza la sección de servicios
                modeloListaServicios.forEach { tipoServicio ->
                    item {
                        Text(
                            text = tipoServicio.nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    items(tipoServicio.lista.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                        ) {
                            rowItems.forEach { servicio ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp)
                                ) {
                                    ServicioCard(
                                        servicio = servicio,
                                        onClick = { idTipoServicio, titulo, descripcion ->

                                            when (idTipoServicio) {
                                                1 -> {

                                                    // NECESITA HABILITAR PERMISO UBICACION
                                                    if(verificarSiPermisoUbicacion(context = ctx)){

                                                        // Navegar a la pantalla VistaDenunciaBasica
                                                        navController.navigate(
                                                            Routes.VistaDenunciaBasica.createRoute(
                                                                servicio.id,
                                                                titulo,
                                                                descripcion
                                                            ),
                                                            navOptions {
                                                                launchSingleTop = true
                                                            }
                                                        )
                                                    }else{
                                                        popPermisoGPS = true
                                                    }
                                                }
                                                2 -> {

                                                    // SOLICITUD MEDIO AMBIENTE Y DENUNCIAS

                                                    // NECESITA HABILITAR PERMISO UBICACION
                                                    if(verificarSiPermisoUbicacion(context = ctx)){

                                                        navController.navigate(
                                                            Routes.VistaSolicitudTalaArbol.route) {
                                                            navOptions {
                                                                launchSingleTop = true
                                                            }
                                                        }
                                                    }else{
                                                        popPermisoGPS = true
                                                    }

                                                }
                                                3 -> {
                                                    // DENUNCIAS WHATSAPP
                                                    val intent = Intent(Intent.ACTION_VIEW, uri)

                                                    try {
                                                        ctx.startActivity(intent)
                                                    } catch (e: ActivityNotFoundException) {
                                                        // En caso de que no se pueda abrir el Intent, se abre el navegador
                                                        try {
                                                            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                                                            ctx.startActivity(browserIntent)
                                                        } catch (e: ActivityNotFoundException) {
                                                            // Mostrar un mensaje si no hay navegador disponible
                                                            showToastErrorWhats = true
                                                        }
                                                    }
                                                }
                                                4 -> {
                                                    // SOLVENCIA CATASTRAL

                                                    navController.navigate(
                                                        Routes.VistaSolvencias.route) {
                                                        navOptions {
                                                            launchSingleTop = true
                                                        }
                                                    }
                                                }
                                                5 -> {

                                                    // RECOLECTORES EN TIEMPO REAL

                                                    // NECESITA HABILITAR PERMISO UBICACION
                                                    if(verificarSiPermisoUbicacion(context = ctx)){

                                                        if (authProvider.auth.currentUser != null) {
                                                            // Intentar obtener un token válido
                                                            authProvider.auth.currentUser?.getIdToken(true)
                                                                ?.addOnCompleteListener { tokenTask ->
                                                                    if (tokenTask.isSuccessful) {
                                                                        // Token válido, redirigir al mapa
                                                                        redireccionarMapaMotoristas(navController)
                                                                    } else {
                                                                        // El token no es válido, registrar un nuevo usuario anónimo
                                                                        iniciarSesionAnonima(authProvider, navController, viewModel, popErrorLoginFirebase)
                                                                    }
                                                                }
                                                        } else {
                                                            // El usuario no está autenticado, registrar un nuevo usuario anónimo
                                                            iniciarSesionAnonima(authProvider, navController, viewModel, popErrorLoginFirebase)
                                                        }
                                                    }else{
                                                        popPermisoGPS = true
                                                    }
                                                }

                                                else -> {
                                                    // CUANDO TOCA NUEVO SERVICIO,
                                                    // ACTIVARA ESTO SI LA APP NO ESTA ACTUALIZADA
                                                    popNuevaActializacion = true
                                                }
                                            }

                                        }
                                    )
                                }
                            }
                            // Si hay un número impar de items, agrega una caja vacía para la alineación
                            if (rowItems.size == 1) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            if(popErrorLoginFirebase.value){
                CustomModal1Boton(
                    popErrorLoginFirebase.value,
                    stringResource(R.string.error_login_firebase),
                    onDismiss = {
                        scope.launch {
                            popErrorLoginFirebase.value = false
                        }
                    })
            }

            if (showModalCerrarSesion) {
                CustomModalCerrarSesion(showModalCerrarSesion,
                    stringResource(R.string.cerrar_sesion),
                    onDismiss = { showModalCerrarSesion = false },
                    onAccept = {
                        scope.launch {
                            // Llamamos a deletePreferences de manera segura dentro de una coroutine
                            tokenManager.deletePreferences()

                            // cerrar modal
                            showModalCerrarSesion = false

                            navigateToLogin(navController)
                        }
                    })
            }

            if (popNumeroBloqueado) {
                CustomModal1Boton(
                    popNumeroBloqueado,
                    stringResource(R.string.numero_bloqueado),
                    onDismiss = {
                        scope.launch {
                            tokenManager.deletePreferences()
                            popNumeroBloqueado = false
                            navigateToLogin(navController)
                        }
                    })
            }

            if(popNuevaActializacion){
                CustomModalUpdateApp(
                    showDialog = true,
                    message = stringResource(id = R.string.nueva_actualizacion),
                    R.drawable.googleplay,
                    onDismiss = { popNuevaActializacion = false },
                    onAccept = {
                        popNuevaActializacion = false
                        redireccionGooglePlay(ctx)
                    }
                )
            }

            if (isLoading) {
                LoadingModal(isLoading = isLoading)
            }
        }


        resultado?.getContentIfNotHandled()?.let { result ->
            when (result.success) {

                1 -> {
                    // USUARIO BLOQUEADO
                    popNumeroBloqueado = true
                }
                2 -> {

                    // CARGA LA PANTALLA PRINCIPAL

                    imageUrls = result.slider.map { sliderItem ->
                        // Construir la URL completa de la imagen
                        "${RetrofitBuilder.urlImagenes}${sliderItem.imagen}"
                    }

                    modeloListaServicios = result.tiposervicio

                    // PARA MOSTRAR MODAL DE NUEVA ACTUALIZACION
                    if(versionLocal != "N/A" && result.modalandroid == 1){
                        // AQUI SE COMPARA LA VERSION QUE
                        // LA VERSION DE LA APP DEBE SER LA MISMA DEL SERVIDOR, SINO
                        // MOSTRARA NUEVA ACTUALIZACION

                        // CADA VEZ QUE SE SUBA LA APP A GOOGLE PLAY SE DESACTIVA LAS ACTUALIZACIONES
                        // Y SE ACTIVARA CUANDO YA ESTE LA APP DISPONIBLE PARA LA DESCARGA
                        // SETEANDO LA VERSION

                        val isUpdateAvailable = result.versionandroid != versionLocal
                        if (isUpdateAvailable) { popNuevaActializacion = true }
                    }

                    boolDatosCargados = true
                }
                else -> {
                    // Error, mostrar Toast
                    CustomToasty(
                        ctx,
                        stringResource(id = R.string.error_reintentar),
                        ToastType.ERROR
                    )
                }
            }
        }

        // solo mostrar si no puede abrir whatss App o Navegador
        if (showToastErrorWhats) {
            CustomToasty(
                ctx,
                stringResource(id = R.string.error_abrir_whatsapp),
                ToastType.ERROR
            )

            showToastErrorWhats = true
        }

        if(popPermisoGPS){
            AlertDialog(
                onDismissRequest = { popPermisoGPS = false },
                title = { Text(stringResource(R.string.permiso_gps_requerido)) },
                text = { Text(stringResource(R.string.para_usar_esta_funcion_gps)) },
                confirmButton = {
                    Button(
                        onClick = {
                            popPermisoGPS = false
                            redireccionarAjustes(ctx)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorAzulGob,
                            contentColor = ColorBlancoGob
                        )
                    ){
                        Text(stringResource(R.string.ir_a_ajustes))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            popPermisoGPS = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorGris1Gob,
                            contentColor = ColorBlancoGob
                        )
                    ){
                        Text(stringResource(R.string.cancelar))
                    }
                }
            )
        }

    }
}

// VERIFICA SI TIENE PERMISO UBICACION PERMITIDOS
fun verificarSiPermisoUbicacion(context: Context): Boolean{
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
        return true
    }else{
        return false
    }
}

// CREA UN USUARIO ANONIMO EN FIREBASE PARA ACCEDER Y LEER LOS RECOLECTORES EN VIVO
private fun iniciarSesionAnonima(
    authProvider: AuthProvider,
    navController: NavHostController,
    viewModel: ServiciosViewModel,
    popErrorLoginFirebase: MutableState<Boolean>,
) {
    viewModel.setLoading(loading = true)

    authProvider.registroAnonimo()
        .addOnCompleteListener { task ->
            viewModel.setLoading(loading = false)

            if (task.isSuccessful) {
                // Registro correcto, redirigir al mapa
                redireccionarMapaMotoristas(navController)
            } else {
                // Mostrar un error
                popErrorLoginFirebase.value = true
            }
        }
}


// REDIRECCION A MAPA DE RECOLECTORES EN VIVO
fun redireccionarMapaMotoristas(navController: NavHostController){
        navController.navigate(
            Routes.VistaMotoristas.route
        ) {
            navOptions {
                launchSingleTop = true
            }
        }
}

// redireccionar a vista login
private fun navigateToLogin(navController: NavHostController) {
    navController.navigate(Routes.VistaLogin.route) {
        popUpTo(Routes.VistaPrincipal.route) {
            inclusive = true // Elimina VistaPrincipal de la pila
        }
        launchSingleTop = true // Asegura que no se creen múltiples instancias de VistaLogin
    }
}

// REDIRECCIONA A TIENDA AUTOMATICAMENTE CON ID IDENTIFICADOR DE APP
private fun redireccionGooglePlay(ctx:Context){

    val appPackageName = ctx.packageName

    try {
        // Intenta abrir la Play Store directamente
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
        ctx.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Si no se puede abrir la Play Store, abre la URL en el navegador
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
        ctx.startActivity(intent)
    }
}

// COMPARAR VERSION NAME PARA MOSTRAR CARTEL NUEVA ACTUALIZACION
// Función auxiliar para obtener el versionName (puedes usarla fuera de composables)
fun getVersionName(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "N/A"
    } catch (e: PackageManager.NameNotFoundException) {
        "N/A"
    }
}

// ID DE ONE SIGNAL
fun getOneSignalUserId(): String {
    val deviceState = OneSignal.User.pushSubscription.id
    return deviceState
}