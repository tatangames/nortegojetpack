package com.alcaldiasantaananorte.nortegojetpackcompose.model.datos

import com.google.android.gms.maps.model.LatLng

data class DriverLocation(
    val id: String,
    val latlng: LatLng? = null,
    val descripcion: String? = null,
    val nombre: String? = null,
    val tipo: Int? = null
) {

}