package com.alcaldiasantaananorte.nortegojetpackcompose.model.datos

import com.google.gson.annotations.SerializedName


data class ModeloVerificacion(
    @SerializedName("success")
    val success: Int,

    @SerializedName("canretry")
    val canRetry: Int,

    @SerializedName("segundos")
    val segundos: Int
)

