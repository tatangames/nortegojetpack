package com.alcaldiasantaananorte.nortegojetpackcompose.vistas.principal.opciones.solvencia

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.BarraToolbarColor
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.SolicitarPermisosUbicacion
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.ToastType
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras.CustomTextFieldOpciones
import com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras.CustomTextSolicitud
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.TokenManager
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones.SolvenciaViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolvenciaView(
    navController: NavController,
    viewModel: SolvenciaViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Crea el alcance de coroutine
    val isLoading by viewModel.isLoading.observeAsState(initial = false)

    val resultado by viewModel.resultado.observeAsState()

    val tokenManager = remember { TokenManager(ctx) }
    var token by remember { mutableStateOf("") }

    // variables
    val nombre by viewModel.nombre.observeAsState("")
    val dui by viewModel.dui.observeAsState("")
    var isChecked by remember { mutableStateOf(false) }

    var latitudUsuario by remember { mutableStateOf<Double?>(null) }
    var longitudUsuario by remember { mutableStateOf<Double?>(null) }

    var selectedOption by remember { mutableStateOf(0) }
    var showModal1Boton by remember { mutableStateOf(false) }

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
               // Log.d("Location", "No se pudo obtener la ubicación")
            }
        } else {
            // Manejar el caso donde no se tienen los permisos de ubicación
           // Log.d("Location", "No se tienen los permisos de ubicación")
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
                        text = stringResource(R.string.solvencia_de_inmueble),
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
                        text = stringResource(R.string.solvencia_de_empresa),
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


            CustomTextSolicitud(
                text = stringResource(R.string.nombre_dospuntos),
                paddingTop = 20.dp
            )

            CustomTextFieldOpciones(
                value = nombre,
                onValueChange = { viewModel.setNombre(it) },
                placeholder = stringResource(R.string.nombre),
                keyboardType = KeyboardType.Text,
                maxLength = 100,
                paddingTop = 20.dp
            )

            //** dui


            CustomTextSolicitud(
                text = stringResource(R.string.dui_dospuntos),
                paddingTop = 20.dp
            )

            CustomTextFieldOpciones(
                value = dui,
                onValueChange = { viewModel.setDui(it) },
                placeholder = stringResource(R.string.dui),
                keyboardType = KeyboardType.Text,
                maxLength = 100,
                paddingTop = 20.dp
            )


            Spacer(modifier = Modifier.height(65.dp))


            Button(
                onClick = {
                    getLocation()

                    val isSolicitudCompleta = verificarSolicitud(context, nombre, dui)
                    val valorCheckSolvencia: Int = if (isChecked) 1 else 2 // solo para solvencia

                    if (isSolicitudCompleta) {
                        viewModel.solvenciasRetrofit(token,
                            valorCheckSolvencia,
                            nombre,
                            dui,
                            latitudUsuario?.toString() ?: "",
                            longitudUsuario?.toString() ?: ""
                        )
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
                    text =  stringResource(id = R.string.enviar_solicitud),
                    fontSize = 18.sp,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            if(showModal1Boton){
                CustomModal1Boton(showModal1Boton, stringResource(R.string.solvencia_en_proceso), onDismiss = {showModal1Boton = false})
            }


            if (isLoading) {
                LoadingModal(isLoading = true)
            }
        }
    }


    resultado?.getContentIfNotHandled()?.let { result ->
        when (result.success) {
            1 -> {
                // registro correcto
                CustomToasty(
                    ctx,
                    stringResource(id = R.string.solicitud_enviada),
                    ToastType.SUCCESS
                )

                viewModel.setNombre("")
                viewModel.setDui("")

                showModal1Boton = true
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

fun verificarSolicitud(context: Context, nombre: String, dui: String): Boolean {
    if (nombre.isEmpty()) {
        CustomToasty(
            context,
            context.getString(R.string.nombre_es_requerido),
            ToastType.INFO
        )
        return false
    }
    if (dui.isEmpty()) {
        CustomToasty(
            context,
            context.getString(R.string.dui_es_requerido),
            ToastType.INFO
        )
        return false
    }

    // Si todos los campos tienen texto, retorna true
    return true
}


