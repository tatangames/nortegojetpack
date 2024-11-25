package com.alcaldiasantaananorte.nortegojetpackcompose.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class AuthProvider {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registroAnonimo(): Task<AuthResult> {
        return auth.signInAnonymously()
    }


    fun cerrarSesion() {
        auth.signOut()
    }
}