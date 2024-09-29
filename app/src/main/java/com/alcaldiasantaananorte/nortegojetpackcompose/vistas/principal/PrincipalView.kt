package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModalCerrarSesion
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.ItemsMenuLateral
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.TokenManager
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.itemsMenu
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ListaServicio
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloListaServicios
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.TipoServicio
import com.alcaldiasantaananorte.nortegojetpackcompose.model.rutas.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.GreyLight
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.ServiciosViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


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
    var showModal1Boton by remember { mutableStateOf(false) }
    val tokenManager = remember { TokenManager(ctx) }
    val resultado by viewModel.resultado.observeAsState()
    val scope = rememberCoroutineScope() // Crea el alcance de coroutine
    var imageUrls by remember { mutableStateOf(listOf<String>()) }
    var modeloListaServicios by remember { mutableStateOf(listOf<TipoServicio>()) }

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
                    when (item.id) {
                        1 -> {

                            navController.navigate(Routes.VistaSolicitudes.route) {
                                navOptions {
                                    launchSingleTop = true
                                }
                            }
                        }

                        2 -> {
                            // cerrar sesion
                            showModalCerrarSesion = true
                        }
                    }

                    scope.launch {
                        drawerState.close()
                    }

                }
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
                                                }

                                                else -> {
                                                    println("Otro tipo de servicio: $idTipoServicio")
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

            if (showModal1Boton) {
                CustomModal1Boton(
                    showModal1Boton,
                    stringResource(R.string.numero_bloqueado),
                    onDismiss = {
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
                    imageUrls = result.slider.map { sliderItem ->
                        // Construir la URL completa de la imagen
                        "${RetrofitBuilder.urlImagenes}${sliderItem.imagen}"
                    }

                    modeloListaServicios = result.tiposervicio
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
    }
}


@Composable
fun ServicioCard(servicio: ListaServicio, onClick: (Int, String, String) -> Unit) {
    Card(
        modifier = Modifier
            .padding(top = 16.dp, start = 4.dp, end = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .clickable {
                onClick(
                    servicio.tiposervicio,
                    servicio.nombre,
                    servicio.descripcion ?: ""
                )
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Color de fondo blanco
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp // Elevación similar a CardView
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${RetrofitBuilder.urlImagenes}${servicio.imagen}")
                    .crossfade(true)
                    .placeholder(R.drawable.spinloading)
                    .error(R.drawable.errorcamara)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .heightIn(max = 220.dp)
                    .padding(top = 8.dp),
                contentScale = ContentScale.Inside
            )
            Text(
                text = servicio.nombre,
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
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