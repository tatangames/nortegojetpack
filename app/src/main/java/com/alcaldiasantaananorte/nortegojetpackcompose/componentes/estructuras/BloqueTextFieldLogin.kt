package com.alcaldiasantaananorte.nortegojetpackcompose.componentes.estructuras

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alcaldiasantaananorte.nortegojetpackcompose.R
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.PhoneNumberVisualTransformation

@Composable
fun BloqueTextFieldLogin(text: String, onTextChanged: (String) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F5F5))
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.flag_elsalvador),
            contentDescription = stringResource(id = R.string.el_salvador),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Código del país
        Text(
            text = stringResource(id = R.string.area_pais),
            fontSize = 18.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
        ) {
            TextField(
                value = text,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                onValueChange = { newText ->
                    if (newText.length <= 8) {
                        onTextChanged(newText)
                    }
                },

                // transformar numeros para agregar el gion
                visualTransformation = PhoneNumberVisualTransformation(),

                textStyle = TextStyle(
                    fontSize = 18.sp, // Tamaño del texto
                    fontWeight = FontWeight.Medium // Negrita
                ),
                placeholder = { Text(text = stringResource(id = R.string.numero_telefono)) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    errorContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black
                ),
            )
        }
    }
}






@Composable
fun CustomTextFieldSolicitudTala(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLength: Int = Int.MAX_VALUE, // Por defecto no se limita la longitud
    paddingTop: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = {
            if (it.length <= maxLength) {
                onValueChange(it)
            }
        },
        /* label = {
             Text(
                 text = label,
                 fontSize = 15.sp,
                 color = Color.Black,
                 fontWeight = FontWeight.Normal
             )
         },*/
        modifier = modifier
            .fillMaxWidth()
            .padding(top = paddingTop),
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        ),
        placeholder = { Text(text = placeholder) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedContainerColor = Color(0xFFF5F5F5),
            disabledContainerColor = Color(0xFFF5F5F5),
            errorContainerColor = Color(0xFFF5F5F5),
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black
        )
    )
}


@Composable
fun CustomTextSolicitud(
    text: String,
    fontSize: TextUnit = 18.sp,
    color: Color = Color.Black,
    fontWeight: FontWeight = FontWeight.Normal,
    paddingTop: Dp = 30.dp,
    paddingStart: Dp = 4.dp
) {
    Text(
        text = text,
        fontSize = fontSize,
        color = color,
        fontWeight = fontWeight,
        modifier = Modifier
            .padding(top = paddingTop, start = paddingStart) // Padding directamente en el Text
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start) // Alinear a la izquierda
    )
}