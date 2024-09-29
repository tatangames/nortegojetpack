package com.alcaldiasantaananorte.nortegojetpackcompose.model.rutas

sealed class Routes(val route: String) {
    object VistaSplash: Routes("splash")
    object VistaLogin: Routes("login")

    object VistaVerificarNumero: Routes("verificarNumero/{telefono}/{segundos}") {
        fun createRoute(telefono: String, segundos: String) = "verificarNumero/$telefono/$segundos"
    }

    object VistaPrincipal: Routes("principal")

    object VistaSolicitudes: Routes("solicitudes")


    object VistaDenunciaBasica: Routes("denunciaBasica/{idservicio}/{titulo}/{descripcion}") {
        fun createRoute(idservicio: Int, titulo:String, descripcion:String) = "denunciaBasica/$idservicio/$titulo/$descripcion"
    }

}