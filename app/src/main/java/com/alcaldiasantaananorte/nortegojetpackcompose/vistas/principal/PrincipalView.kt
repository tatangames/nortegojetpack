package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModalCerrarSesion
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.ItemsMenuLateral
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.TokenManager
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.itemsMenu
import com.alcaldiasantaananorte.nortegojetpackcompose.model.rutas.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.ServiciosViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(navController: NavHostController, viewModel: ServiciosViewModel = viewModel(),) {
    val ctx = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var isNavigating by remember { mutableStateOf(false) }
    var showModalCerrarSesion by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.observeAsState(false)
    var showModal1Boton by remember { mutableStateOf(false) }
    val tokenManager = remember { TokenManager(ctx) }
    val resultado by viewModel.resultado.observeAsState()
    val scope = rememberCoroutineScope() // Crea el alcance de coroutine




    LaunchedEffect(Unit) {
        scope.launch {
            val _token = tokenManager.userToken.first()
            val _idusuario = tokenManager.idUsuario.first()
            viewModel.serviciosRetrofit(_token, _idusuario)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                DrawerBody(items = itemsMenu) { item ->
                    if (!isNavigating) {
                        isNavigating = true

                        when (item.id) {
                            1 -> {
                                navController.navigate(Routes.VistaSolicitudes.route) // Navega a la pantalla de solicitudes
                            }

                            2 -> {
                                // cerrar sesion
                                showModalCerrarSesion = true
                            }
                        }

                        scope.launch {
                            drawerState.close()
                            delay(300) // Tiempo en milisegundos antes de permitir otra navegación
                            isNavigating = false
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(stringResource(R.string.servicios), color = Color.White)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
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
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Aquí puedes colocar contenido principal, como una lista o algo que desees mostrar.


                val images = listOf(
                    R.drawable.logofinal,
                    R.drawable.charla,
                    R.drawable.camarafoto
                )

                ImageSlider(images)


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

            if(showModal1Boton){
                CustomModal1Boton(showModal1Boton, stringResource(R.string.numero_bloqueado), onDismiss = {
                    scope.launch {
                        tokenManager.deletePreferences()
                        showModal1Boton = false
                        navigateToLogin(navController)
                    }
                })
            }

            if (isLoading) {
                LoadingModal(isLoading = isLoading)
            }
        }

        resultado?.getContentIfNotHandled()?.let { result ->
            when (result.success) {

                2 -> {

                }
                else -> {
                    // Error, mostrar Toast
                    CustomToasty(ctx, stringResource(id = R.string.error_reintentar), ToastType.ERROR)
                }
            }
        }
    }
}




@Composable
fun ImageSlider(images: List<Int>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .height(200.dp) // Establece la altura a 200dp
                .fillMaxWidth(), // Ocupa todo el ancho disponible
            pageSpacing = 16.dp // Espacio opcional entre páginas
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
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

@Composable
fun DrawerHeader() {

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        Color.Transparent,
        darkIcons = true
    ) // Hace transparente la barra de estado

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .statusBarsPadding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondonorte),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun DrawerBody(
    items: List<ItemsMenuLateral>,
    onItemClick: (ItemsMenuLateral) -> Unit
) {
    Column {
        items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = {
                    Text(
                        stringResource(id = item.idString),
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                },
                selected = false,
                onClick = { onItemClick(item) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}