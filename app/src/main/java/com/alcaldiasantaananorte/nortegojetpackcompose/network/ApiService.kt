package com.alcaldiasantaananorte.nortegojetpackcompose.network

import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloVerificacion
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // VERIFICACION DE NUMERO
    @POST("app/verificacion/telefono")
    @FormUrlEncoded
    fun verificarTelefono(@Field("telefono") telefono: String): Observable<ModeloVerificacion>


}


data class TelefonoRequest(
    val telefono: String
)