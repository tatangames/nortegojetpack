package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.ItemsMenuLateral
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.itemsMenu
import com.alcaldiasantaananorte.nortegojetpackcompose.model.rutas.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorGris1Gob
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.login.LoginScreen
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.login.SplashScreen
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.login.VistaVerificarNumeroView
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.vistassolicitudes.SolicitudesScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isNavigating by remember { mutableStateOf(false) }

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
                                navController.navigate(Routes.VistaSolicitudes.route)
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
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                // Aquí puedes colocar contenido principal, como una lista o algo que desees mostrar.
            }
        }
    }
}


@Composable
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp) // Altura fija
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.fondonorte), // Reemplaza con tu imagen
            contentDescription = null,
            contentScale = ContentScale.FillBounds, // Ajusta la imagen para que llene el espacio sin recortes
            modifier = Modifier.fillMaxSize() // Ocupa todo el espacio del Box
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
                label = { Text(stringResource(id = item.idString)) },
                selected = false,
                onClick = { onItemClick(item) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}