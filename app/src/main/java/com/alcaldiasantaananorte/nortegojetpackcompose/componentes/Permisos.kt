package com.alcaldiasantaananorte.nortegojetpackcompose.componentes

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestCameraPermission(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
        if (isGranted) {
            onPermissionGranted()
        }
    }

    LaunchedEffect(key1 = hasPermission) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        } else {
            onPermissionGranted()
        }
    }
}

@Composable
fun SolicitarPermisosUbicacion(
    onPermisosConcedidos: () -> Unit,
    onPermisosDenegados: () -> Unit
) {
    val context = LocalContext.current
    val permisoUbicacion = Manifest.permission.ACCESS_FINE_LOCATION

    // Manejo de resultados del permiso
    val solicitudPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            onPermisosConcedidos()
        } else {
            onPermisosDenegados()
        }
    }

    // Verificar si el permiso ya está concedido
    val permisosConcedidos = ContextCompat.checkSelfPermission(
        context, permisoUbicacion
    ) == PackageManager.PERMISSION_GRANTED

    // Si los permisos aún no están concedidos, se solicita el permiso
    if (!permisosConcedidos) {
        LaunchedEffect(Unit) {
            solicitudPermisos.launch(permisoUbicacion)
        }
    } else {
        onPermisosConcedidos()
    }
}
