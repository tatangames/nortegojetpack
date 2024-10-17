package com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.ItemsMenuLateral
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ListaServicio
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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