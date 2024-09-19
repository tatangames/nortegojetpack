package com.alcaldiasantaananorte.nortegojetpackcompose.model

sealed class Routes(val route: String) {
    object VistaSplash:Routes("splash")
    object VistaLogin:Routes("login")
    object VistaVerificarNumero:Routes("verificarNumero")


}