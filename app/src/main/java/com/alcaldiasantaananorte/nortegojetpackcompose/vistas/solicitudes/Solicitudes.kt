package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.solicitudes

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.TokenManager
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.MSolicitudesListado
import com.alcaldiasantaananorte.nortegojetpackcompose.model.rutas.Routes
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.ServiciosViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.SolicitudesViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.login.BarraToolbar
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.login.CountdownTimer
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.login.OtpTextField
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SolicitudesScreen(navController: NavController, viewModel: SolicitudesViewModel = viewModel()) {
    val ctx = LocalContext.current
    val isLoading by viewModel.isLoading.observeAsState(initial = true)
    val tokenManager = remember { TokenManager(ctx) }
    val resultado by viewModel.resultado.observeAsState()
    val scope = rememberCoroutineScope()
    var listadoSolicitudes by remember { mutableStateOf(listOf<MSolicitudesListado>()) }
    var showNoHayDatos by remember { mutableStateOf(false) }


    // Lanzar la solicitud cuando se carga la pantalla
    LaunchedEffect(Unit) {
        scope.launch {
            val _token = tokenManager.userToken.first()
            val _idusuario = tokenManager.idUsuario.first()
            viewModel.solicitudesRetrofit(_token, _idusuario)
        }
    }

    Scaffold(
        topBar = {
            BarraToolbar(navController, stringResource(R.string.solicitudes))
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            if (listadoSolicitudes.isNotEmpty()) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listadoSolicitudes.forEach { solicitud ->

                        when (solicitud.tipo) {
                            1 -> { // SOLICITUD BASICA
                                SolicitudCardTipo1(solicitud = solicitud)
                            }
                            2 -> { // SOLICITUD TALA DE ARBOL
                                SolicitudCardTipo2(solicitud = solicitud)
                            }
                        }
                    }
                }
            }else{
                if(showNoHayDatos){
                    Text(
                        text = stringResource(R.string.no_hay_solicitudes_realizadas),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            if (isLoading) {
                LoadingModal(isLoading = true)
            }
        }
    }

    resultado?.getContentIfNotHandled()?.let { result ->
        when (result.success) {
            1 -> {
                if(result.haydatos == 1){
                    listadoSolicitudes = result.listado
                }else{
                    showNoHayDatos = true
                }
            }
            else -> {
                CustomToasty(
                    ctx,
                    stringResource(id = R.string.error_reintentar),
                    ToastType.ERROR
                )
            }
        }
    }
}

// DENUNCIA BASICO
@Composable
fun SolicitudCardTipo1(solicitud: MSolicitudesListado) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            filaRowSolicitudBasica(titulo = stringResource(R.string.solicitud_dospuntos), descripcion = solicitud.nombretipo)
            filaRowSolicitudBasica(titulo = stringResource(R.string.estado_dospuntos), descripcion = solicitud.estado)
            filaRowSolicitudBasica(titulo = stringResource(R.string.fecha_dospuntos), descripcion = solicitud.fecha ?: "")
            solicitud.nota?.let { nota ->
                if (nota.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    filaRowSolicitudBasica(titulo = stringResource(R.string.nota_dospuntos), descripcion = nota)
                }
            }// ?: run { // es null }
        }
    }
}

@Composable
fun filaRowSolicitudBasica(titulo:String, descripcion:String){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = titulo,
            color = Color.Black,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(IntrinsicSize.Max)
        )

        Text(
            text = descripcion,
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}


// TALA DE ARBOL
@Composable
fun SolicitudCardTipo2(solicitud: MSolicitudesListado) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            filaRowSolicitudBasica(titulo = stringResource(R.string.solicitud_dospuntos), descripcion = solicitud.nombretipo)
            filaRowSolicitudBasica(titulo = stringResource(R.string.estado_dospuntos), descripcion = solicitud.estado)
            filaRowSolicitudBasica(titulo = stringResource(R.string.fecha_dospuntos), descripcion = solicitud.fecha ?: "")
            filaRowSolicitudBasica(titulo = stringResource(R.string.nombre_dospuntos), descripcion = solicitud.fecha ?: "")
            filaRowSolicitudBasica(titulo = stringResource(R.string.telefono_dospuntos), descripcion = solicitud.telefono ?: "")
            filaRowSolicitudBasica(titulo = stringResource(R.string.direccion_dospuntos), descripcion = solicitud.direccion?: "")
            solicitud.nota?.let { nota ->
                if (nota.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    filaRowSolicitudBasica(titulo = stringResource(R.string.nota_dospuntos), descripcion = nota)
                }
            }// ?: run { // es null }
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${RetrofitBuilder.urlImagenes}${solicitud.imagen}")
                    .crossfade(true)
                    .placeholder(R.drawable.spinloading)
                    .error(R.drawable.errorcamara)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .heightIn(max = 220.dp),
                contentScale = ContentScale.Inside
            )
        }
    }
}