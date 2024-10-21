package com.alcaldiasantaananorte.nortegojetpackcompose.extras

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

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