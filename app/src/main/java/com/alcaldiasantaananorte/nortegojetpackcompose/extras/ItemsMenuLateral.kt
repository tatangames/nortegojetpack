package com.alcaldiasantaananorte.nortegojetpackcompose.extras

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.model.rutas.Routes

sealed class ItemsMenuLateral(
    val icon: ImageVector,
    val idString: Int,
    val id: Int
) {
    object ItemMenu1 : ItemsMenuLateral(
        Icons.Outlined.RealEstateAgent,
        R.string.solicitudes,
        1
    )

    object ItemMenu2 : ItemsMenuLateral(
        Icons.Outlined.Home,
        R.string.cerrar_sesion,
        2
    )
}

// Lista de items del menú lateral
val itemsMenu = listOf(ItemsMenuLateral.ItemMenu1, ItemsMenuLateral.ItemMenu2)
