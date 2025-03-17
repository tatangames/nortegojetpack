package com.alcaldiasantaananorte.nortegojetpackcompose.extras

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.ui.graphics.vector.ImageVector
import com.alcaldiasantaananorte.nortegojetpackcompose.R

sealed class ItemsMenuLateral(
    val icon: ImageVector,
    val idString: Int,
    val id: Int
) {
    object ItemMenu1 : ItemsMenuLateral(
        Icons.AutoMirrored.Filled.List,
        R.string.solicitudes,
        1
    )

    object ItemMenu2 : ItemsMenuLateral(
        Icons.AutoMirrored.Filled.Logout,
        R.string.agenda,
        2
    )

    object ItemMenu3 : ItemsMenuLateral(
        Icons.AutoMirrored.Filled.Logout,
        R.string.cerrar_sesion,
        3
    )
}

// Lista de items del menú lateral
val itemsMenu = listOf(ItemsMenuLateral.ItemMenu1, ItemsMenuLateral.ItemMenu2, ItemsMenuLateral.ItemMenu3)
