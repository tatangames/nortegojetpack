package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.agenda


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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.BarraToolbarColor

import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.MSolicitudesListado

import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloAgendaArray
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob

import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.solicitudes.AgendaViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.solicitudes.SolicitudesOcultarViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.solicitudes.ConfirmDialogBorrarSolicitud
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.solicitudes.SolicitudCardTipo1
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.solicitudes.SolicitudCardTipo2
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.solicitudes.SolicitudCardTipo3
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.solicitudes.SolicitudCardTipo4
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.solicitudes.filaRowSolicitudBasica

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(navController: NavController, viewModel: AgendaViewModel = viewModel()
) {

    val ctx = LocalContext.current
    val isLoading by viewModel.isLoading.observeAsState(initial = true)
    val resultado by viewModel.resultado.observeAsState()

    val scope = rememberCoroutineScope()
    var modeloAgendaArray by remember { mutableStateOf(listOf<ModeloAgendaArray>()) }
    // Lanzar la solicitud cuando se carga la pantalla
    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.agendasRetrofit()
        }
    }

    resultado?.getContentIfNotHandled()?.let { result ->
        when (result.success) {
            1 -> {
                modeloAgendaArray = result.listado // Asegúrate de que esto sea una lista de ModeloAgendaArray
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



    Scaffold(
        topBar = {
            BarraToolbarColor(navController, stringResource(R.string.agenda), ColorAzulGob)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 16.dp)
        ) {

            if (modeloAgendaArray.isNotEmpty()) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Define 2 columnas
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modeloAgendaArray.forEach { solicitud ->
                        item {
                            CardBloque(solicitud)
                        }
                    }
                }

            }

            if (isLoading) {
                LoadingModal(isLoading = true)
            }
        }
    }






}


@Composable
fun CardBloque(solicitud: ModeloAgendaArray) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
         ,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Color de fondo blanco
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp // Elevación similar a CardView
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

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
                    .heightIn(max = 235.dp),
                contentScale = ContentScale.Inside
            )

            filaRowAgenda(titulo = solicitud.nombre)
            filaRowAgenda(titulo = solicitud.telefono)
        }
    }
}


@Composable
fun filaRowAgenda(titulo:String, isBold: Boolean = false){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center, // Centra el contenido horizontalmente
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            color = Color.Black,
            fontSize = 17.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.width(IntrinsicSize.Max)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}


