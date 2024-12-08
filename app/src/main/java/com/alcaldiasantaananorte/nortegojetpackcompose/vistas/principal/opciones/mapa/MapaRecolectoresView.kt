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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.BlueSoft
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MapaClienteView(navController: NavController){
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationPermissionGranted = remember { mutableStateOf(false) }
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState()
    var isLoading by remember { mutableStateOf(true) }
    val driverIcon = remember { mutableStateOf<BitmapDescriptor?>(null) }
    val driverMap = remember { mutableStateMapOf<String, DriverLocation>() }
    val markerMap = remember { mutableStateMapOf<String, MarkerState>() }
    val showAvailableDrivers = remember { mutableStateOf(false) }
    val availableDrivers = remember { mutableStateListOf<DriverLocation>() }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted.value = isGranted
    }

    // Inicializar BitmapDescriptorFactory y crear el ícono DEL CAMION
    LaunchedEffect(Unit) {
        try {
            MapsInitializer.initialize(context)
            driverIcon.value = getBitmapDescriptor(context, R.drawable.camion60)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // SOLICITAR PERMISOS UBICACION
    LaunchedEffect(Unit) {
        if (!hasLocationPermission(context)) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationPermissionGranted.value = true
        }
    }

    // AL CARGAR MOSTRAR LOADING Y SE QUITA HASTA QUE MAPA HA SIDO CARGADO
    val onMapLoadedCallback: () -> Unit = {
        // Código que deseas ejecutar cuando el mapa esté cargado
        isLoading = false
    }


    // Obtener ubicación actual
    LaunchedEffect(locationPermissionGranted.value) {
        if (locationPermissionGranted.value) {
            startLocationUpdates(context, fusedLocationClient) { location ->
                currentLocation.value = location
                if (cameraPositionState.position.target == LatLng(0.0, 0.0)) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 14f)
                }

                // Buscar motoristas cercanos
                getNearbyDrivers(location) { nearbyDrivers ->

                    // Actualizar la lista de conductores disponibles
                    availableDrivers.clear() // Limpiar la lista antes de agregar nuevos conductores
                    availableDrivers.addAll(nearbyDrivers)

                    // Procesar los conductores en el mapa
                    val nearbyDriverIds = nearbyDrivers.map { it.id!! }

                    // Eliminar conductores que ya no están disponibles
                    val driversToRemove = driverMap.keys - nearbyDriverIds
                    driversToRemove.forEach { id ->
                        driverMap.remove(id)
                        markerMap.remove(id) // Eliminar marcador correspondiente
                    }

                    // Agregar o actualizar conductores
                    nearbyDrivers.forEach { driver ->
                        val id = driver.id!!
                        driverMap[id] = driver

                        // Actualizar la posición del marcador o agregar uno nuevo
                        markerMap[id]?.apply {
                            position = LatLng(driver.latlng!!.latitude, driver.latlng!!.longitude)
                        } ?: run {
                            markerMap[id] = MarkerState(
                                position = LatLng(driver.latlng!!.latitude, driver.latlng!!.longitude)
                            )
                        }
                    }
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
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false, // Botón de ubicación
                zoomControlsEnabled = false      // Oculta los botones de zoom
            ),
            onMapLoaded = onMapLoadedCallback
        ) {
            driverMap.values.forEach { driver ->
                driverIcon.value?.let { icon ->
                    Marker(
                        state = MarkerState(position = LatLng(driver.latlng!!.latitude, driver.latlng!!.longitude)),
                        title = driver.nombre ?: stringResource(id = R.string.recolectores_disponibles),
                        snippet = driver.descripcion ?: "",
                        icon = icon
                    )
                }
            }
        }

        // Botón para centrar la cámara en la ubicación actual
        FloatingActionButton(
            onClick = {
                currentLocation.value?.let { location ->
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 14f)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .offset(y = (-65).dp),
            containerColor = BlueSoft, // Aplica el color azul suave aquí
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = stringResource(id = R.string.centrar_ubicacion)
            )
        }


        // MOSTRAR PANEL DE LOS DRIVER DISPONIBLES
        FloatingActionButton(
            onClick = {
                showAvailableDrivers.value = !showAvailableDrivers.value
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 32.dp, start = 25.dp),

            containerColor = BlueSoft,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = stringResource(id = R.string.recolectores_disponibles))
        }

        // Mostrar lista de conductores disponibles en un modal
        if (showAvailableDrivers.value) {
            AvailableDriversList(
                availableDrivers = availableDrivers,
                onDriverClick = { selectedDriver ->
                    // Cierra el modal y centra la cámara en el conductor seleccionado
                    showAvailableDrivers.value = false
                    selectedDriver.latlng?.let { location ->
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            14f
                        )
                    }
                },
                onClose = { showAvailableDrivers.value = false }
            )
        }
    }


    if (isLoading) {
        LoadingModal(isLoading = true)
    }
}


// METODO PARA OBTENER LOS DRIVER DE FIREBASE
fun getNearbyDrivers(
    currentLocation: LatLng,
    onDriversFound: (List<DriverLocation>) -> Unit
) {
    GeoProvider().getNearbyDrivers(currentLocation, 10_000.0) // 10,000 km
        .addGeoQueryEventListener(object : GeoQueryEventListener {
            val drivers = mutableMapOf<String, DriverLocation>()

            override fun onKeyEntered(documentID: String, location: GeoPoint) {
                val driverDoc = FirebaseFirestore.getInstance().collection("Drivers").document(documentID)
                driverDoc.get().addOnSuccessListener { document ->
                    if (document != null) {
                        val description = document.getString("descripcion")
                        val nombre = document.getString("nombre")
                        val tipo: Int = document.getString("tipo")?.toIntOrNull() ?: 0

                        val newDriver = DriverLocation(documentID, LatLng(location.latitude, location.longitude), description, nombre, tipo)

                        if (drivers[documentID] != newDriver) { // Solo actualiza si cambió
                            drivers[documentID] = newDriver
                            onDriversFound(drivers.values.toList())
                        }
                    }
                }
            }

            override fun onKeyExited(documentID: String) {
                Log.d("FIREBASE", "Conductor desconectado: $documentID")

                // Eliminar el conductor de la lista
                drivers.remove(documentID)
                onDriversFound(drivers.values.toList()) // Actualiza la lista de conductores disponibles
            }

            override fun onKeyMoved(documentID: String, location: GeoPoint) {
                Log.d("FIREBASE", "Conductor en movimiento: $documentID")
            }

            override fun onGeoQueryError(exception: Exception) {
                Log.e("FIREBASE", "Error en la consulta geográfica: ${exception.message}")
            }

            override fun onGeoQueryReady() {
                onDriversFound(drivers.values.toList())
            }
        })
}



fun startLocationUpdates(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationUpdate: (LatLng) -> Unit
) {
    if (hasLocationPermission(context)) {
        // Crear el LocationRequest usando LocationRequest.Builder
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            10000 // Actualización estándar: cada 10 segundos
        ).apply {
            setMinUpdateIntervalMillis(5000) // Intervalo mínimo: cada 5 segundos
            setMaxUpdateDelayMillis(15000) // Máximo retraso: 15 segundos
        }.build()

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


// MODAL QUE MUESTRA CONDUCTORES EN TIEMPO REAL
@Composable
fun AvailableDriversList(
    availableDrivers: List<DriverLocation>,
    onDriverClick: (DriverLocation) -> Unit,
    onClose: () -> Unit
) {
    // Animación para el indicador de scroll
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val translateAnim = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Translate Animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .fillMaxWidth(0.8f)
                .heightIn(min = 250.dp, max = 350.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Encabezado con indicador de scroll
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(id = R.string.recolectores_disponibles),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    // Indicador de scroll animado
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(id = R.string.scroll),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(y = translateAnim.value.dp)
                            .size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    val scrollState = rememberLazyListState()
                    val isScrolledToEnd = !scrollState.canScrollForward
                    val sortedDrivers = availableDrivers.sortedBy { it.tipo }

                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(sortedDrivers) { driver ->
                            DriverItem(driver = driver, onDriverClick = onDriverClick)
                            Divider(
                                color = Color.LightGray,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    // Gradiente en la parte inferior que se desvanece cuando llegas al final
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colors.surface.copy(
                                            alpha = if (isScrolledToEnd) 0f else 0.8f
                                        )
                                    )
                                )
                            )
                    )
                }

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueSoft, // Color de fondo del botón
                        contentColor = Color.White  // Color del texto o íconos dentro del botón
                    )
                ) {

                    Text(stringResource(id = R.string.cerrar))
                }
            }
        }
    }
}




// ITEM DE CADA DRIVER
@Composable
private fun DriverItem(driver: DriverLocation, onDriverClick: (DriverLocation) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.camion60), // Reemplaza con el nombre del archivo
            contentDescription = stringResource(id = R.string.recolector),
            modifier = Modifier.size(32.dp), // Ajusta el tamaño según sea necesario
            tint = Color.Unspecified // Usa los colores originales del ícono
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = driver.nombre ?: "",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDriverClick(driver) } // Maneja el clic
                .padding(vertical = 8.dp),
            style = MaterialTheme.typography.body1
        )
    }
}





// Función para convertir drawable a BitmapDescriptor
private fun getBitmapDescriptor(context: Context, resourceId: Int): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(context, resourceId)
    drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        drawable?.intrinsicWidth ?: 0,
        drawable?.intrinsicHeight ?: 0,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable?.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}








