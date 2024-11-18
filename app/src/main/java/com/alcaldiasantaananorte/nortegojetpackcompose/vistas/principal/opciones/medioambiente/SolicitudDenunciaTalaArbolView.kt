package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.opciones.medioambiente

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.BarraToolbarColor
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomCheckboxTala
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ImageBoxSolicitudTala
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.RequestCameraPermission
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.SolicitarPermisosUbicacion
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras.CustomTextFieldOpciones
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras.CustomTextSolicitud
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.TokenManager
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorGris1Gob
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones.DenunciaTalaArbolViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones.SolicitudTalaArbolViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.opciones.denuncias.redireccionarAjustes
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SolicitudDenunciaTalaArbolView(
    navController: NavController,
    viewModelSolicitud: SolicitudTalaArbolViewModel = viewModel(),
    viewModelDenuncia: DenunciaTalaArbolViewModel = viewModel()
) {
    val ctx = LocalContext.current
    var showBottomSheetCamara by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Crea el alcance de coroutine
    val isLoadingSolicitud by viewModelSolicitud.isLoading.observeAsState(initial = false)
    val isLoadingDenuncia by viewModelDenuncia.isLoading.observeAsState(initial = false)

    val resultadoSolicitud by viewModelSolicitud.resultado.observeAsState()
    val resultadoDenuncia by viewModelDenuncia.resultado.observeAsState()

    val tokenManager = remember { TokenManager(ctx) }
    var popPermisoCamaraRequerido by remember { mutableStateOf(false) }
    var token by remember { mutableStateOf("") }
    var idCliente by remember { mutableStateOf("") }

    // variables
    val nota by viewModelSolicitud.nota.observeAsState("")
    val nombre by viewModelSolicitud.nombre.observeAsState("")
    val telefono by viewModelSolicitud.telefono.observeAsState("")
    val direccion by viewModelSolicitud.direccion.observeAsState("")
    var isChecked by remember { mutableStateOf(false) }
    var hayPermisoGps by remember { mutableStateOf(false) }
    var latitudUsuario by remember { mutableStateOf<Double?>(null) }
    var longitudUsuario by remember { mutableStateOf<Double?>(null) }
    var popPermisoGPS by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(0) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Función para obtener la ubicación
    fun getLocation() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            hayPermisoGps = true

            lastKnownLocation?.let {
                latitudUsuario = it.latitude
                longitudUsuario = it.longitude
            } ?: run {
                // Manejar el caso donde lastKnownLocation es nulo
                Log.d("Location", "No se pudo obtener la ubicación")
            }
        } else {
            // Manejar el caso donde no se tienen los permisos de ubicación
            Log.d("Location", "No se tienen los permisos de ubicación")
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

    // Lanzar la solicitud cuando se carga la pantalla
    LaunchedEffect(Unit) {
        scope.launch {
            token = tokenManager.userToken.first()
            idCliente = tokenManager.idUsuario.first()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        val correctedBitmap = getRotatedBitmap(context, uri!!)
        imageBitmap = correctedBitmap
    }




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






    val file = remember { File(context.filesDir, "camera_photo.jpg") }
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
            imageUri = uri
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
                .padding(innerPadding)
                .padding(horizontal = 16.dp) // Padding adicional
                .verticalScroll(rememberScrollState()) // Hacer la vista desplazable
                .imePadding(), // Ajustar al teclado
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {


            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = stringResource(R.string.opciones),
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 30.dp, start = 4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedOption == 0,
                        onClick = { selectedOption = 0 },
                        colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                    )
                    Text(
                        text = stringResource(R.string.solicitud_tala_arbol),
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .align(Alignment.CenterVertically)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedOption == 1,
                        onClick = { selectedOption = 1 },
                        colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                    )
                    Text(
                        text = stringResource(R.string.denuncia_tala_arbol),
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))



            when (selectedOption) {
                0 -> {

                    CustomTextSolicitud(
                        text = stringResource(R.string.nombre_dospuntos),
                        paddingTop = 20.dp
                    )
                    CustomTextFieldOpciones(
                        value = nombre,
                        onValueChange = { viewModelSolicitud.setNombre(it) },
                        placeholder = stringResource(R.string.nombre),
                        keyboardType = KeyboardType.Text,
                        maxLength = 100,
                        paddingTop = 20.dp
                    )

                    //** telefono

                    CustomTextSolicitud(
                        text = stringResource(R.string.telefono_dospuntos),
                        paddingTop = 35.dp
                    )

                    CustomTextFieldOpciones(
                        value = telefono,
                        onValueChange = { viewModelSolicitud.setTelefono(it) },
                        placeholder = stringResource(R.string.telefono),
                        keyboardType = KeyboardType.Phone,
                        maxLength = 8,
                        paddingTop = 20.dp
                    )

                    //** direccion


                    CustomTextSolicitud(
                        text = stringResource(R.string.direccion_dospuntos),
                        paddingTop = 35.dp
                    )

                    CustomTextFieldOpciones(
                        value = direccion,
                        onValueChange = { viewModelSolicitud.setDireccion(it) },
                        placeholder = stringResource(R.string.direccion),
                        keyboardType = KeyboardType.Text,
                        maxLength = 500,
                        paddingTop = 20.dp
                    )

                    //** nota

                    CustomTextSolicitud(
                        text = stringResource(R.string.nota_opcional_dospuntos),
                        paddingTop = 35.dp
                    )

                    CustomTextFieldOpciones(
                        value = nota,
                        onValueChange = { viewModelSolicitud.setNota(it) },
                        placeholder = stringResource(R.string.nota),
                        keyboardType = KeyboardType.Text,
                        maxLength = 1000,
                        paddingTop = 20.dp
                    )

                    Spacer(modifier = Modifier.height(38.dp))

                    CustomCheckboxTala(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        label = stringResource(R.string.tiene_escrituras)
                    )


                    //** imagen

                    CustomTextSolicitud(
                        text = stringResource(R.string.seleccionar_imagen),
                        paddingTop = 38.dp
                    )

                    ImageBoxSolicitudTala(
                        imageBitmap = imageBitmap,
                        onClick = { showBottomSheetCamara = true },
                        paddingTop = 30.dp, // Ajusta el padding superior aquí.
                        contentDescription = stringResource(R.string.seleccionar_imagen)
                    )


                }
                1 -> {

                    //** imagen

                    CustomTextSolicitud(
                        text = stringResource(R.string.seleccionar_imagen),
                        paddingTop = 38.dp
                    )

                    ImageBoxSolicitudTala(
                        imageBitmap = imageBitmap,
                        onClick = { showBottomSheetCamara = true },
                        paddingTop = 30.dp, // Ajusta el padding superior aquí.
                        contentDescription = stringResource(R.string.seleccionar_imagen)
                    )


                    //** nota
                    CustomTextSolicitud(
                        text = stringResource(R.string.nota_opcional_dospuntos),
                        paddingTop = 35.dp
                    )

                    CustomTextFieldOpciones(
                        value = nota,
                        onValueChange = { viewModelSolicitud.setNota(it) },
                        placeholder = stringResource(R.string.nota),
                        keyboardType = KeyboardType.Text,
                        maxLength = 1000,
                        paddingTop = 20.dp
                    )
                }
            }


            Spacer(modifier = Modifier.height(40.dp))


            Button(
                onClick = {

                    getLocation()

                    if(hayPermisoGps){
                        if (imageUri != null) {

                            if(selectedOption == 0){

                                val isSolicitudCompleta = verificarSolicitud(context, nombre, telefono, direccion)
                                val valorCheckEscritura: Int = if (isChecked) 1 else 0 // solo para medio ambiente

                                if (isSolicitudCompleta) {
                                    viewModelSolicitud.registrarSolicitudTalaArbolRX(token,
                                        context, imageUri!!,
                                        valorCheckEscritura.toString(),
                                        latitudUsuario?.toString() ?: "",
                                        longitudUsuario?.toString() ?: ""
                                    )
                                }
                            }else{
                                viewModelDenuncia.registrarDenunciaTalaArbolRX(token,
                                    context, imageUri!!,
                                    idCliente,
                                    latitudUsuario?.toString() ?: "",
                                    longitudUsuario?.toString() ?: "",
                                    nota
                                )
                            }
                        } else {
                            showBottomSheetCamara = true
                        }
                    }else{
                        popPermisoGPS = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 16.dp), // Mantén solo el padding horizontal aquí
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

            Spacer(modifier = Modifier.height(30.dp))


            // Mostrar Bottom Sheet
            if (showBottomSheetCamara) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheetCamara = false }
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
                                    showBottomSheetCamara = false
                                    // Pedir permisos de cámara y lanzar
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
                                showBottomSheetCamara = false
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
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
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

            if (isLoadingSolicitud || isLoadingDenuncia) {
                LoadingModal(isLoading = true)
            }
        }
    }


    resultadoSolicitud?.getContentIfNotHandled()?.let { result ->
        when (result.success) {
            1 -> {
                // registro correcto
                imageUri = null
                imageBitmap = null
                viewModelSolicitud.setNombre("")
                viewModelSolicitud.setTelefono("")
                viewModelSolicitud.setDireccion("")
                viewModelSolicitud.setNota("")

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


    resultadoDenuncia?.getContentIfNotHandled()?.let { result ->
        when (result.success) {
            1 -> {
                // registro correcto
                imageUri = null
                imageBitmap = null
                viewModelSolicitud.setNota("")

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

fun verificarSolicitud(context: Context, nombre: String, telefono: String, direccion: String): Boolean {
    if (nombre.isEmpty()) {
        CustomToasty(
            context,
            context.getString(R.string.nombre_es_requerido),
            ToastType.INFO
        )
        return false
    }
    if (telefono.isEmpty()) {
        CustomToasty(
            context,
            context.getString(R.string.telefono_es_requerido),
            ToastType.INFO
        )
        return false
    }
    if (direccion.isEmpty()) {
        CustomToasty(
            context,
            context.getString(R.string.direccion_es_requerida),
            ToastType.INFO
        )
        return false
    }

    // Si todos los campos tienen texto, retorna true
    return true
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
