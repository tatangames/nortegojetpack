package com.alcaldiasantaananorte.nortegojetpackcompose.model.datos

import com.google.gson.annotations.SerializedName

data class ModeloSolicitudes(
    @SerializedName("success") val success: Int,
    @SerializedName("haydatos") val haydatos: Int,
    @SerializedName("listado") val listado: List<MSolicitudesListado>,
)

data class MSolicitudesListado(
    @SerializedName("id") val id: Int,
    @SerializedName("tipo") val tipo: Int,
    @SerializedName("nombretipo") val nombretipo: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("nota") val nota: String?,
    @SerializedName("fecha") val fecha: String?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("direccion") val direccion: String?,
    @SerializedName("escritura") val escritura: String?,
    @SerializedName("dui") val dui: String?,
    @SerializedName("imagen") val imagen: String?
)


//****************** AGENDA **************************

data class ModeloAgenda(
    @SerializedName("success") val success: Int,
    @SerializedName("listado") val listado: List<ModeloAgendaArray>,
)

data class ModeloAgendaArray(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("imagen") val imagen: String
)