package com.alcaldiasantaananorte.nortegojetpackcompose.model.datos

import com.google.gson.annotations.SerializedName

data class ModeloVerificacion(
    @SerializedName("success")
    val success: Int,

    @SerializedName("canretry")
    val canRetry: Int? = null,

    @SerializedName("segundos")
    val segundos: Int? = null
)


data class ModeloReintentoSMS (
    @SerializedName("success")
    val success: Int,

    @SerializedName("segundos")
    val segundos: Int? = null
)



data class ModeloVerificarCodigo (
    @SerializedName("success")
    val success: Int,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("id")
    val id: Int? = null

)