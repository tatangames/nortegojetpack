package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.opciones.denuncias

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.BarraToolbarColor
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModal1ImageBoton
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.SolicitarPermisosUbicacion
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.TokenManager
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorGris1Gob
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones.RegistrarDenunciaBasicaViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.provider.MediaStore
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DenunciaBasicaScreen(
    idservicio: Int, titulo: String,
    descripcion: String,
    navController: NavController,
    viewModel: RegistrarDenunciaBasicaViewModel = viewModel()
) {
    val ctx = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Crea el alcance de coroutine
    val nota by viewModel.nota.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val resultado by viewModel.resultado.observeAsState()
    val tokenManager = remember { TokenManager(ctx) }
    var popPermisoCamaraRequerido by remember { mutableStateOf(false) }
    var popPermisoGPS by remember { mutableStateOf(false) }
    var popDenunciaPendiente by remember { mutableStateOf(false) }
    var hayPermisoGps by remember { mutableStateOf(false) }
    var token by remember { mutableStateOf("") }

    //var location by remember { mutableStateOf<Location?>(null) }
    var latitudUsuario by remember { mutableStateOf<Double?>(null) }
    var longitudUsuario by remember { mutableStateOf<Double?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isLoadingUbicacion by remember { mutableStateOf(false) }


    // Función para obtener la ubicación
    fun getLocation() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            isLoadingUbicacion = true

            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            hayPermisoGps = true

            lastKnownLocation?.let {
                latitudUsuario = it.latitude
                longitudUsuario = it.longitude
                isLoadingUbicacion = false
            } ?: run {
                // Manejar el caso donde lastKnownLocation es nulo
                Log.d("Location", "No se pudo obtener la ubicación")
                isLoadingUbicacion = false
            }
        } else {
            // Manejar el caso donde no se tienen los permisos de ubicación
            Log.d("Location", "No se tienen los permisos de ubicación")
            isLoadingUbicacion = false
        }
    }

    SolicitarPermisosUbicacion(
        onPermisosConcedidos = {
            getLocation()
            hayPermisoGps = true
        },
        onPermisosDenegados = {
            hayPermisoGps = false
        }
    )


    val cameraPermission = Manifest.permission.CAMERA
    val permissionStateCamara = rememberPermissionState(permission = cameraPermission)

    LaunchedEffect(Unit) {
        if (!permissionStateCamara.status.isGranted) {
            permissionStateCamara.launchPermissionRequest()
        }
    }

    when {
        permissionStateCamara.status.isGranted -> {
            // Si el permiso está otorgado
          //  Log.d("PERMISO", "permisio camara aceptado")
        }
        permissionStateCamara.status.shouldShowRationale -> {
            // Si el usuario rechazó el permiso previamente
           // Log.d("PERMISO", "necesitamos acceso a la camara para continuar")
        }
        else -> {
           // Log.d("PERMISO", "esperando respuesta")
        }
    }






    // Lanzar la solicitud cuando se carga la pantalla
    LaunchedEffect(Unit) {
        scope.launch {
            token = tokenManager.userToken.first()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        val correctedBitmap = getRotatedBitmap(context, uri!!)
        imageBitmap = correctedBitmap
    }


    val file = remember { createImageFile(context) }

    val uri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Actualizar imageUri después de tomar la foto
            imageUri = uri // 'uri' debe ser el URI del archivo de imagen creado
            val correctedBitmap = getRotatedBitmap(context, uri)
            imageBitmap = correctedBitmap
        }
    }




    Scaffold(
        topBar = {
            BarraToolbarColor(navController, "", ColorAzulGob)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Aplica el padding generado por el Scaffold
                .padding(16.dp) // Aplica un padding adicional de 16.dp
                .verticalScroll(rememberScrollState()) // Hacer la vista desplazable
                .imePadding(), // Ajustar al teclado si se muestra
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = titulo,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = stringResource(R.string.seleccionar_imagen),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 0.dp),
                textAlign = TextAlign.Start
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(top = 0.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!.asImageBitmap(), // Convierte Bitmap a ImageBitmap
                        contentDescription = stringResource(R.string.seleccionar_imagen),
                        modifier = Modifier
                            .height(225.dp)
                            .width(225.dp)
                            .align(Alignment.Center)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                showBottomSheet = true
                            },
                        contentScale = ContentScale.Inside
                    )
                } else {
                    AsyncImage(
                        model = R.drawable.camarafoto,
                        contentDescription = stringResource(R.string.seleccionar_imagen),
                        modifier = Modifier
                            .height(200.dp)
                            .width(200.dp)
                            .align(Alignment.Center)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                showBottomSheet = true
                            },
                        contentScale = ContentScale.Crop
                    )
                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                value = nota,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { viewModel.setNota(it) },
                label = { Text(stringResource(R.string.nota_opcional)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                placeholder = { Text(text = stringResource(id = R.string.nota)) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    errorContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black,
                    focusedLabelColor = Color.Black, // Color del label cuando está enfocado
                    unfocusedLabelColor = Color.Black // Color del label cuando no está enfocado
                ),
            )

            Spacer(modifier = Modifier.height(65.dp))

            Button(
                onClick = {
                    getLocation()
                    keyboardController?.hide()

                    if(hayPermisoGps){
                        if (imageUri != null) {
                            coroutineScope.launch {
                                viewModel.registrarDenunciaBasicaRetrofit(
                                    token,
                                    idservicio,
                                    context,
                                    imageUri!!,
                                    latitudUsuario?.toString() ?: "",
                                    longitudUsuario?.toString() ?: ""
                                )
                            }
                        } else {
                            // Mostrar un mensaje de error o un diálogo indicando que se necesita una imagen
                            showBottomSheet = true
                        }
                    }else{
                        popPermisoGPS = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorAzulGob,
                    contentColor = ColorBlancoGob
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.enviar_denuncia),
                    fontSize = 18.sp,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))


            // Mostrar Bottom Sheet
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Título del modal
                        Text(
                            text = stringResource(R.string.opciones),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(16.dp)
                        )

                        // Botón para abrir la cámara
                        Button(
                            onClick = {
                                if(permissionStateCamara.status.isGranted){
                                    showBottomSheet = false
                                    cameraLauncher.launch(uri)
                                }else{
                                    popPermisoCamaraRequerido = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorAzulGob,
                                contentColor = ColorBlancoGob
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                stringResource(R.string.abrir_camara),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Botón para abrir la galería
                        Button(
                            onClick = {
                                showBottomSheet = false
                                // Pedir permisos de galería y abrir
                                galleryLauncher.launch("image/*")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorAzulGob,
                                contentColor = ColorBlancoGob
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                stringResource(R.string.abrir_galeria),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }



            if (popPermisoCamaraRequerido) {
                AlertDialog(
                    onDismissRequest = { popPermisoCamaraRequerido = false },
                    title = { Text(stringResource(R.string.permiso_de_camara_requerido)) },
                    text = { Text(stringResource(R.string.para_usar_esta_funcion_camara)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                popPermisoCamaraRequerido = false
                                redireccionarAjustes(context)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorAzulGob,
                                contentColor = ColorBlancoGob
                            )
                        ){
                            Text(stringResource(R.string.ir_a_ajustes))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                popPermisoCamaraRequerido = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorGris1Gob,
                                contentColor = ColorBlancoGob
                            )
                        ){
                            Text(stringResource(R.string.cancelar))
                        }
                    }
                )
            }

            if(popPermisoGPS){
                AlertDialog(
                    onDismissRequest = { popPermisoGPS = false },
                    title = { Text(stringResource(R.string.permiso_gps_requerido)) },
                    text = { Text(stringResource(R.string.para_usar_esta_funcion_gps)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                popPermisoGPS = false
                                redireccionarAjustes(context)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorAzulGob,
                                contentColor = ColorBlancoGob
                            )
                        ){
                            Text(stringResource(R.string.ir_a_ajustes))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                popPermisoGPS = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorGris1Gob,
                                contentColor = ColorBlancoGob
                            )
                        ){
                            Text(stringResource(R.string.cancelar))
                        }
                    }
                )
            }

            if(popDenunciaPendiente){
                CustomModal1ImageBoton(popDenunciaPendiente, stringResource(R.string.denuncia_pendiente_zona),
                    R.drawable.alerta, onDismiss = {popDenunciaPendiente = false})
            }

            if (isLoading) {
                LoadingModal(isLoading = true)
            }

            if (isLoadingUbicacion) {
                LoadingModal(isLoading = true, titulo = stringResource(R.string.obteniendo_ubicacion))
            }
        }
    }

    resultado?.getContentIfNotHandled()?.let { result ->
        when (result.success) {
            1 -> {
                // solicitud pendiente en su ubicacion
                popDenunciaPendiente = true
            }
            2 -> {
                // denuncia registrada
                imageUri = null
                imageBitmap = null
                viewModel.setNota("")
                CustomToasty(
                    ctx,
                    stringResource(id = R.string.solicitud_enviada),
                    ToastType.SUCCESS
                )
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


fun redireccionarAjustes(context: Context){
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}


fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        context.filesDir
    )
}

fun getRotatedBitmap(context: Context, uri: Uri): Bitmap? {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    // Obtener orientación EXIF
    val exif = ExifInterface(context.contentResolver.openInputStream(uri)!!)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    // Corregir la orientación del bitmap
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
        else -> bitmap
    }
}

// Función para rotar la imagen
private fun rotateImage(source: Bitmap, angle: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle.toFloat())
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}