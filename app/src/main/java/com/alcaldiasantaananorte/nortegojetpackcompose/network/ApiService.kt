package com.alcaldiasantaananorte.nortegojetpackcompose.network

import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloReintentoSMS
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloVerificacion
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloVerificarCodigo
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // VERIFICACION DE NUMERO
    @POST("app/verificacion/telefono")
    @FormUrlEncoded
    fun verificarTelefono(@Field("telefono") telefono: String): Single<ModeloVerificacion>

    // REINTENTO SMS
    @POST("app/reintento/telefono")
    @FormUrlEncoded
    fun reintentoSMS(@Field("telefono") telefono: String): Single<ModeloReintentoSMS>

    // VERIFICAR CODIGO
    @POST("app/verificarcodigo/telefono")
    @FormUrlEncoded
    fun verificarCodigo(@Field("telefono") telefono: String,
                        @Field("codigo") codigo: String,
                        @Field("idonesignal") idonesignal: String? = null): Single<ModeloVerificarCodigo>
}


