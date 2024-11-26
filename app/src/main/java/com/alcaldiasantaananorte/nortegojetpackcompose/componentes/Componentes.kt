package com.alcaldiasantaananorte.nortegojetpackcompose.componentes

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorGris1Gob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorNegroGob
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LoadingModal(isLoading: Boolean, titulo:String = "Cargando...") {
    if (isLoading) {
        Dialog(onDismissRequest = { /* Evitar que se cierre el modal */ }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = ColorAzulGob)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = titulo,
                        fontSize = 18.sp,
                        color = ColorNegroGob
                    )
                }
            }
        }
    }
}

@Composable
fun CustomModal1Boton(showDialog: Boolean, message: String, onDismiss: () -> Unit) {
    if (showDialog) {
        Dialog(onDismissRequest = {}) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth() // Ajusta el ancho al 80% de la pantalla
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(4.dp), // Agrega padding alrededor del contenido
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = message,
                        fontSize = 18.sp,
                        color = ColorNegroGob,
                        modifier = Modifier.padding(bottom = 16.dp) // Espacio entre el texto y el botón
                    )
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorAzulGob,
                            contentColor = ColorBlancoGob
                        ),
                    ) {
                        Text(text = stringResource(id = R.string.aceptar))
                    }
                }
            }
        }
    }
}



@Composable
fun CustomModal1ImageBoton(showDialog: Boolean, message: String, @DrawableRes imageResId: Int, onDismiss: () -> Unit) {
    if (showDialog) {
        Dialog(onDismissRequest = {}) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = imageResId), // Reemplaza con tu recurso de imagen
                        contentDescription = null, // Descripción de la imagen para accesibilidad
                        modifier = Modifier
                            .size(100.dp) // Tamaño de la imagen
                            .padding(bottom = 16.dp) // Espacio entre la imagen y el texto
                    )
                    Text(
                        text = message,
                        fontSize = 18.sp,
                        color = ColorNegroGob,
                        modifier = Modifier.padding(bottom = 16.dp) // Espacio entre el texto y el botón
                    )
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorAzulGob,
                            contentColor = ColorBlancoGob
                        ),
                    ) {
                        Text(text = stringResource(id = R.string.aceptar))
                    }
                }
            }
        }
    }
}



@Composable
fun CustomModalUpdateApp(showDialog: Boolean, message: String, @DrawableRes imageResId: Int,
                         onDismiss: () -> Unit,
                         onAccept: () -> Unit)
{
    if (showDialog) {
        Dialog(onDismissRequest = {}) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = imageResId), // Reemplaza con tu recurso de imagen
                        contentDescription = null, // Descripción de la imagen para accesibilidad
                        modifier = Modifier
                            .size(100.dp) // Tamaño de la imagen
                            .padding(bottom = 16.dp) // Espacio entre la imagen y el texto
                    )
                    Text(
                        text = message,
                        fontSize = 18.sp,
                        color = ColorNegroGob,
                        modifier = Modifier.padding(bottom = 16.dp) // Espacio entre el texto y el botón
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(id = R.string.cancelar))
                        }
                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorAzulGob,
                                contentColor = ColorBlancoGob
                            ),
                        ) {
                            Text(stringResource(id = R.string.verificar), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CustomModal2Botones(
    showDialog: Boolean,
    message: String,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = message,
                        fontSize = 17.sp,
                        color = ColorNegroGob,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(id = R.string.cancelar))
                        }
                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorAzulGob,
                                contentColor = ColorBlancoGob
                            ),
                        ) {
                            Text(stringResource(id = R.string.verificar), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CustomModalCerrarSesion(
    showDialog: Boolean,
    message: String,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = message,
                        fontSize = 18.sp,
                        color = ColorNegroGob,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorGris1Gob,
                                contentColor = ColorBlancoGob
                            ),
                        ) {
                            Text(stringResource(id = R.string.no), color = Color.White)
                        }

                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorAzulGob,
                                contentColor = ColorBlancoGob
                            ),
                        ) {
                            Text(stringResource(id = R.string.si), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}



enum class ToastType {
    SUCCESS,
    ERROR,
    INFO,
    WARNING
}

fun CustomToasty(context: Context, message: String, type: ToastType) {
    when (type) {
        ToastType.SUCCESS -> Toasty.success(context, message, Toasty.LENGTH_SHORT, true).show()
        ToastType.ERROR -> Toasty.error(context, message, Toasty.LENGTH_SHORT, true).show()
        ToastType.INFO -> Toasty.info(context, message, Toasty.LENGTH_SHORT, true).show()
        ToastType.WARNING -> Toasty.warning(context, message, Toasty.LENGTH_SHORT, true).show()
    }
}


class CountdownViewModel : ViewModel() {
    var timer by mutableStateOf(5)
        private set

    fun updateTimer(value: Int) {
        timer = value
    }

    var isButtonEnabled by mutableStateOf(false)
        private set

    init {
        startTimer()
    }

    // Función para iniciar el temporizador
    private fun startTimer() {
        viewModelScope.launch {
            while (timer > 0) {
                delay(1000L)
                timer--
            }
            isButtonEnabled = true
        }
    }

    // Función para reiniciar el temporizador
    fun resetTimer() {
        timer = 60 // defecto al reiniciar
        isButtonEnabled = false
        startTimer()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraToolbarColor(navController: NavController, titulo: String, backgroundColor: Color) {

    var isNavigating by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = titulo,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Medium,
            )
        },

        navigationIcon = {
            IconButton(
                onClick = {
                    if (!isNavigating) {
                        isNavigating = true
                        navController.popBackStack()
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.volver),
                    tint = Color.White // Color del ícono de navegación
                )
            }
        },
        actions = {
            // Puedes agregar acciones adicionales aquí si lo necesitas
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = backgroundColor, // Color de fondo de la barra
            navigationIconContentColor = Color.White, // Color del ícono de navegación
            titleContentColor = Color.White, // Color del título
            actionIconContentColor = Color.White // Color de las acciones
        ),
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeightIn(min = 56.dp) // Define una altura mínima
    )
}


@Composable
fun ImageBoxSolicitudTala(
    imageBitmap: Bitmap?, // Puede ser un Int (para drawable) o Uri.
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    boxHeight: Dp = 200.dp,
    imageSize: Dp = 150.dp,
    paddingTop: Dp = 5.dp,
    contentDescription: String = ""
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(boxHeight)
            .padding(top = paddingTop),
        contentAlignment = Alignment.Center
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
                        onClick()
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
                        onClick()
                    },
                contentScale = ContentScale.Inside
            )
        }
    }
}


@Composable
fun CustomCheckboxTala(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        // Checkbox que puede cambiar el estado
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = ColorAzulGob, // Color cuando está marcado
                uncheckedColor = Color.Gray, // Color cuando no está marcado
                checkmarkColor = Color.White // Color del check (la palomita)
            ),
            modifier = Modifier
                .size(24.dp) // Tamaño del checkbox
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Texto que también puede cambiar el estado al ser presionado
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable { onCheckedChange(!checked) } // Cambia el estado al hacer clic en el texto
                .padding(start = 4.dp)
        )
    }
}
