package com.alcaldiasantaananorte.nortegojetpackcompose.componentes

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorGris1Gob
import com.alcaldiasantaananorte.nortegojetpackcompose.ui.theme.ColorNegroGob
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LoadingModal(isLoading: Boolean) {
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
                        text = "Cargando...",
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
                        fontSize = 18.sp,
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



