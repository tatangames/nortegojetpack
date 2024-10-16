package com.alcaldiasantaananorte.nortegojetpackcompose.network

import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloBasico
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloListaServicios
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloReintentoSMS
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloSolicitudes
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloVerificacion
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloVerificarCodigo
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    // LISTADO DE SERVICIOS
    @POST("app/principal/listado")
    @FormUrlEncoded
    fun listadoServicios(@Field("id") idusuario: String): Single<ModeloListaServicios>

    // LISTADO DE SOLICITUDES
    @POST("app/solicitudes/listado")
    @FormUrlEncoded
    fun listadoSolicitudes(@Field("iduser") idusuario: String): Single<ModeloSolicitudes>

    // OCULTAR SOLICITUDES
    @POST("app/solicitudes/ocultar")
    @FormUrlEncoded
    fun ocultarSolicitudes(@Field("id") id: Int, @Field("tipo") tipo: Int): Single<ModeloSolicitudes>

    // REGISTRAR DENUNCIA BASICA
    @Multipart
    @POST("app/servicios/basicos/registrar")
    fun registrarDenunciaBasica(
        @Part imagen: MultipartBody.Part,
        @Part("nota") nota: RequestBody,
        @Part("idservicio") idservicio: RequestBody,
        @Part("latitud") latitud: RequestBody,
        @Part("longitud") longitud: RequestBody,
    ): Single<ModeloBasico>

    // REGISTRAR SOLICITUD TALA DE ARBOL
    @Multipart
    @POST("app/servicios/talaarbol-solicitud/registrar")
    fun registrarSolicitudTalaArbol(
        @Part imagen: MultipartBody.Part,
        @Part("nombre") nombre: RequestBody,
        @Part("telefono") telefono: RequestBody,
        @Part("direccion") direccion: RequestBody,
        @Part("escritura") escritura: RequestBody,
        @Part("nota") nota: RequestBody,
        @Part("latitud") latitud: RequestBody,
        @Part("longitud") longitud: RequestBody,
    ): Single<ModeloBasico>





    // REGISTRAR DENUNCIA TALA DE ARBOL
    @Multipart
    @POST("app/servicios/talaarbol-denuncia/registrar")
    fun registrarDenunciaTalaArbol(
        @Part imagen: MultipartBody.Part,
        @Part("iduser") idusuario: String,
        @Part("nota") nota: RequestBody,
        @Part("latitud") latitud: RequestBody,
        @Part("longitud") longitud: RequestBody,
    ): Single<ModeloBasico>


}


