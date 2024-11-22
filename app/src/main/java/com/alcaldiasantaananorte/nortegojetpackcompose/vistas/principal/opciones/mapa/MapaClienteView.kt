package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.opciones.mapa

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.maps.android.compose.MapUiSettings
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.DriverLocation
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.GeoProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener


@Composable
fun MapaClienteView(navController: NavController){
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationPermissionGranted = remember { mutableStateOf(false) }
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val driverMarkers = remember { mutableStateListOf<DriverLocation>() }
    val cameraPositionState = rememberCameraPositionState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted.value = isGranted
    }

    // Solicitar permisos
    LaunchedEffect(Unit) {
        if (!hasLocationPermission(context)) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationPermissionGranted.value = true
        }
    }

    // Obtener ubicación actual
    LaunchedEffect(locationPermissionGranted.value) {
        if (locationPermissionGranted.value) {
            startLocationUpdates(context, fusedLocationClient) { location ->
                currentLocation.value = location
                if (cameraPositionState.position.target == LatLng(0.0, 0.0)) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                }
                // Buscar motoristas cercanos
                getNearbyDrivers(location) { nearbyDrivers ->
                    driverMarkers.clear()
                    driverMarkers.addAll(nearbyDrivers)
                }
            }
        }
    }

    // Renderizar mapa cuando la ubicación está disponible
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true
            ),
            uiSettings = MapUiSettings(myLocationButtonEnabled = false)
        ) {
            driverMarkers.forEach { driver ->
                Marker(
                    state = MarkerState(position = LatLng(driver.latlng!!.latitude, driver.latlng!!.longitude)),
                    title = "Conductor disponible",
                    snippet = "Cerca de tu ubicación"
                )
            }
        }

        // Botón para centrar la cámara en la ubicación actual
        FloatingActionButton(
            onClick = {
                currentLocation.value?.let { location ->
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Centrar ubicación"
            )
        }
    }
}



fun getNearbyDrivers(
    currentLocation: LatLng,
    onDriversFound: (List<DriverLocation>) -> Unit
) {
    // Simula obtener conductores cercanos usando geoqueries
    GeoProvider().getNearbyDrivers(currentLocation, 30.0)
        .addGeoQueryEventListener(object : GeoQueryEventListener {
            val drivers = mutableListOf<DriverLocation>()

            override fun onKeyEntered(documentID: String, location: GeoPoint) {
                drivers.add(DriverLocation(documentID, LatLng(location.latitude, location.longitude)))
            }

            override fun onKeyExited(documentID: String) {
                // Manejo cuando un motorista deja el área
            }

            override fun onKeyMoved(documentID: String, location: GeoPoint) {
                // Actualización de la ubicación del motorista
            }

            override fun onGeoQueryError(exception: Exception) {}
            override fun onGeoQueryReady() {
                onDriversFound(drivers)
            }
        })
}


fun startLocationUpdates(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationUpdate: (LatLng) -> Unit
) {
    if (hasLocationPermission(context)) {
        val locationRequest = LocationRequest.create().apply {
            interval = 3000
            fastestInterval = 1500
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onLocationUpdate(LatLng(location.latitude, location.longitude))
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}



// Función para verificar si se tienen permisos de ubicación
fun hasLocationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}
