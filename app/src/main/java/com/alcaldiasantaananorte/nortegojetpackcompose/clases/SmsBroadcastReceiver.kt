package com.alcaldiasantaananorte.nortegojetpackcompose.clases

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

class SMSReceiver : BroadcastReceiver() {
    var onCodeReceived: ((String) -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val stringBuilder = StringBuilder()
            for (message in messages) {
                val messageBody = message.messageBody
                // Extraer todos los números del mensaje y agregar al StringBuilder
                val numbers = messageBody.replace(Regex("[^0-9]"), "")
                stringBuilder.append(numbers)
            }
            val fullCode = stringBuilder.toString()
            if (fullCode.isNotEmpty()) {
                onCodeReceived?.invoke(fullCode)
            }
        }
    }


}