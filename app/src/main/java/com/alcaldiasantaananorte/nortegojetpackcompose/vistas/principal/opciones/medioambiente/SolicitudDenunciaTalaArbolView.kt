package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.opciones.medioambiente

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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.RequestCameraPermission
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.SolicitarPermisosUbicacion
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras.CustomTextFieldSolicitudTala
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras.CustomTextSolicitud
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.PhoneNumberVisualTransformation
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.TokenManager
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorGris1Gob
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones.DenunciaTalaArbolViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones.SolicitudTalaArbolViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
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

    // variables
    val nota by viewModelSolicitud.nota.observeAsState("")
    val nombre by viewModelSolicitud.nombre.observeAsState("")
    val telefono by viewModelSolicitud.telefono.observeAsState("")
    val direccion by viewModelSolicitud.direccion.observeAsState("")


    var latitudUsuario by remember { mutableStateOf<Double?>(null) }
    var longitudUsuario by remember { mutableStateOf<Double?>(null) }

    var selectedOption by remember { mutableStateOf(0) }


    // Función para obtener la ubicación
    fun getLocation() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

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
        },
        onPermisosDenegados = {
        }
    )

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
    }

    // ** CAMARA
    var permisoCamara by remember { mutableStateOf(false) }

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
        }
    }

    RequestCameraPermission {
        permisoCamara = true
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
                .padding(16.dp)
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
                    CustomTextFieldSolicitudTala(
                        value = nombre,
                        onValueChange = { viewModelSolicitud.setNombre(it) },
                        placeholder = stringResource(R.string.nombre),
                        keyboardType = KeyboardType.Text,
                        maxLength = 100,
                        paddingTop = 20.dp
                    )


                    CustomTextSolicitud(
                        text = stringResource(R.string.telefono_dospuntos),
                        paddingTop = 35.dp
                    )
                    CustomTextFieldSolicitudTala(
                        value = telefono,
                        onValueChange = { viewModelSolicitud.setTelefono(it) },
                        placeholder = stringResource(R.string.telefono),
                        keyboardType = KeyboardType.Phone,
                        maxLength = 8,
                        paddingTop = 20.dp
                    )



                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .padding(top = 30.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        AsyncImage(
                            model = imageUri ?: R.drawable.camarafoto,
                            contentDescription = stringResource(R.string.seleccionar_imagen),
                            modifier = Modifier
                                .height(150.dp)
                                .width(150.dp)
                                .align(Alignment.Center)
                                .clickable(
                                    indication = null, // Elimina el efecto de sombreado
                                    interactionSource = remember { MutableInteractionSource() } // Fuente de interacción personalizada
                                ) {
                                    showBottomSheetCamara = true
                                },
                            contentScale = ContentScale.Crop
                        )
                    }




                }
                1 -> {
                    // Vista para la Opción 2: dos textos
                    Column {
                        Text(text = "Texto 1 para la Opción 2")
                        Text(text = "Texto 2 para la Opción 2")
                    }
                }
            }








            Button(
                onClick = {

                    getLocation()
                    if (imageUri != null) {
                       /* viewModel.registrarDenunciaBasicaRetrofit(token, idservicio, context, imageUri!!,
                            latitudUsuario?.toString() ?: "",
                            longitudUsuario?.toString() ?: ""
                        )*/
                    } else {
                        // Mostrar un mensaje de error o un diálogo indicando que se necesita una imagen
                        showBottomSheetCamara = true
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
                                if(permisoCamara){
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
                    text = { Text(stringResource(R.string.para_usar_esta_funcion)) },
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


