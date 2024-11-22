package com.alcaldiasantaananorte.nortegojetpackcompose.model.datos

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.GeoQuery

class GeoProvider {

    val collection = FirebaseFirestore.getInstance().collection("Locations")
    val geoFirestore = GeoFirestore(collection)

    fun getNearbyDrivers(position: LatLng, radius: Double): GeoQuery {
        val query = geoFirestore.queryAtLocation(GeoPoint(position.latitude, position.longitude), radius)
        query.removeAllListeners()
        return query
    }

}