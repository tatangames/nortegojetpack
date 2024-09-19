package com.alcaldiasantaananorte.nortegojetpackcompose.extras

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// UTILIZADO PARA PANTALLA LOGIN PARA AGREGUAR GUIONES DESPUES DE 4 NUMEROS
class PhoneNumberVisualTransformation: VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val input = text.text
        val transformedText = buildString {
            for (i in input.indices) {
                append(input[i])
                if (i == 3) {
                    append('-')
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset > 3) offset + 1 else offset
            }

            override fun transformedToOriginal(offset: Int): Int {
                return if (offset > 4) offset - 1 else offset
            }
        }

        return TransformedText(AnnotatedString(transformedText), offsetMapping)
    }
}