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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.BlueSoft
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MapaClienteView(navController: NavController){
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationPermissionGranted = remember { mutableStateOf(false) }
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val driverMarkers = remember { mutableStateListOf<DriverLocation>() }
    val cameraPositionState = rememberCameraPositionState()
    var isLoading by remember { mutableStateOf(true) }
    // Convertir el drawable a BitmapDescriptor
    val driverIcon = remember { mutableStateOf<BitmapDescriptor?>(null) }


    val showAvailableDrivers = remember { mutableStateOf(false) }
    val availableDrivers = remember { mutableStateListOf<DriverLocation>() }
    val driverMap = remember { mutableStateMapOf<String, DriverLocation>() }
    var isListVisible by remember { mutableStateOf(true) }




    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted.value = isGranted
    }

    // Inicializar BitmapDescriptorFactory y crear el ícono
    LaunchedEffect(Unit) {
        try {
            MapsInitializer.initialize(context)
            driverIcon.value = getBitmapDescriptor(context, R.drawable.camion60)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Solicitar permisos
    LaunchedEffect(Unit) {
        if (!hasLocationPermission(context)) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationPermissionGranted.value = true
        }
    }

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
                    // Utilizamos un mapa para hacer un seguimiento de los conductores por ID
                    val newDriverMap = nearbyDrivers.associateBy { it.id }

                    // Actualizamos solo los conductores que han cambiado
                    newDriverMap.forEach { (driverId, driver) ->
                        val existingDriver = driverMap[driverId]
                        if (existingDriver == null || existingDriver.latlng != driver.latlng) {
                            driverMap[driverId!!] = driver // Actualizamos el conductor
                        }
                    }

                    // Filtramos solo los conductores disponibles (modifica esta lógica si es necesario)
                    availableDrivers.clear()
                    availableDrivers.addAll(driverMap.values.filter { it.description != null })

                    // Actualizamos la lista de marcadores (sin borrar todos)
                    driverMarkers.clear()
                    driverMarkers.addAll(driverMap.values)
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
            driverMarkers.forEach { driver ->
                driverIcon.value?.let { icon ->
                    Marker(
                        state = MarkerState(position = LatLng(driver.latlng!!.latitude, driver.latlng!!.longitude)),
                        title = driver.description ?: "Recolector disponible",
                        //snippet = "",
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
                .offset(y = (-100).dp),
            containerColor = BlueSoft, // Aplica el color azul suave aquí
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Centrar ubicación"
            )
        }





        // Botón para mostrar/ocultar lista de conductores disponibles
        FloatingActionButton(
            onClick = {
                showAvailableDrivers.value = !showAvailableDrivers.value
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            containerColor = BlueSoft,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "Mostrar conductores disponibles")
        }

        // Mostrar lista de conductores disponibles

        if (showAvailableDrivers.value) {
            AvailableDriversList(
                availableDrivers = availableDrivers,
                onClose = { showAvailableDrivers.value = false } // Cierra la lista cuando se toca afuera
            )
        }

    }


    if (isLoading) {
        LoadingModal(isLoading = true)
    }
}



@Composable
fun AvailableDriversList(
    availableDrivers: List<DriverLocation>,
    onClose: () -> Unit
) {
    // Animación para el indicador de scroll
    val infiniteTransition = rememberInfiniteTransition()
    val translateAnim = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
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
                        "Conductores Disponibles",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    // Indicador de scroll animado
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Scroll para ver más",
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

                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(availableDrivers) { driver ->
                            DriverItem(driver = driver)
                            DriverItem(driver = driver)
                            DriverItem(driver = driver)
                            DriverItem(driver = driver)
                            DriverItem(driver = driver)
                            DriverItem(driver = driver)
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}


@Composable
private fun DriverItem(driver: DriverLocation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = driver.description ?: "",
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


fun getNearbyDrivers(
    currentLocation: LatLng,
    onDriversFound: (List<DriverLocation>) -> Unit
) {
    GeoProvider().getNearbyDrivers(currentLocation, 30.0)
        .addGeoQueryEventListener(object : GeoQueryEventListener {
            val drivers = mutableMapOf<String, DriverLocation>()

            override fun onKeyEntered(documentID: String, location: GeoPoint) {
                val driverDoc = FirebaseFirestore.getInstance().collection("Drivers").document(documentID)
                driverDoc.get().addOnSuccessListener { document ->
                    if (document != null) {
                        val description = document.getString("descripcion")
                        val newDriver = DriverLocation(documentID, LatLng(location.latitude, location.longitude), description)

                        if (drivers[documentID] != newDriver) { // Solo actualiza si cambió
                            drivers[documentID] = newDriver
                            onDriversFound(drivers.values.toList())
                        }
                    }
                }

            }

            override fun onKeyExited(documentID: String) {
                /*if (drivers.containsKey(documentID)) {
                    drivers.remove(documentID)
                    onDriversFound(drivers.values.toList())
                }*/
            }

            override fun onKeyMoved(documentID: String, location: GeoPoint) {
                /*val existingDriver = drivers[documentID]
                if (existingDriver?.latlng != LatLng(location.latitude, location.longitude)) {
                    drivers[documentID]?.latlng = LatLng(location.latitude, location.longitude)
                    onDriversFound(drivers.values.toList())
                }*/
            }

            override fun onGeoQueryError(exception: Exception) {}
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














