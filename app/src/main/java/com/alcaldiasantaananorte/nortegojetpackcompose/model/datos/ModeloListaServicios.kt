package com.alcaldiasantaananorte.nortegojetpackcompose.model.datos

import com.google.gson.annotations.SerializedName

data class ModeloListaServicios(
    @SerializedName("success") val success: Int,
    @SerializedName("modalandroid") val modalandroid: Int,
    @SerializedName("versionandroid") val versionandroid: String,
    @SerializedName("slider") val slider: List<Slider>,
    @SerializedName("tiposervicio") val tiposervicio: List<TipoServicio>
)

data class Slider(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("imagen") val imagen: String,
    @SerializedName("activo") val activo: Int,
    @SerializedName("posicion") val posicion: Int
)

data class TipoServicio(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("posicion") val posicion: Int,
    @SerializedName("activo") val activo: Int,
    @SerializedName("lista") val lista: List<ListaServicio>
)

data class ListaServicio(
    @SerializedName("id") val id: Int,
    @SerializedName("id_cateservicio") val idCateservicio: Int,
    @SerializedName("tiposervicio") val tiposervicio: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("imagen") val imagen: String,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("activo") val activo: Int,
    @SerializedName("posicion") val posicion: Int,
    @SerializedName("bloqueo_gps") val bloqueoGps: Int
)
