package com.alcaldiasantaananorte.nortegojetpackcompose.model

sealed class Routes(val route: String) {
   // object VistaSplash:Routes("splash")
   // object VistaLogin:Routes("login")



    object Pantalla1 : Routes("pantalla1")
    object Pantalla2 : Routes("pantalla2")


  /*  object VistaVerificarNumeroView: Routes("verificarNumero/{telefono}/{segundos}")

    fun verificarNumeroConParametros(telefono: String, segundos: Int): String {
        return "verificarNumero/$telefono/$segundos"
    }*/


}