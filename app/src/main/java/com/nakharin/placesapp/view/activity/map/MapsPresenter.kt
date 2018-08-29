package com.nakharin.placesapp.view.activity.map

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

class MapsPresenter(private val view: MapsContact.View, private val mMap: GoogleMap, private val fusedLocationClient: FusedLocationProviderClient) : MapsContact.UserActionListener {

    private var latLng: LatLng? = null

    override fun getSelectedLocation() {
        latLng = mMap.cameraPosition.target
        view.onGotCenterLocation(latLng!!)
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                view.onMoveToCurrentLocation(currentLatLng, 18f)
            }
        }
    }
}