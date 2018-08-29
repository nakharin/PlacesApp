package com.nakharin.placesapp.view.activity.map

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

class MapsContact {

    interface View {
        fun setUpToolbar()
        fun onMoveToCurrentLocation(currentLatLng: LatLng, zoomLV: Float)
        fun onGotCenterLocation(latLng: LatLng)
        fun setAddressName(addressName: String)
        fun onLocationPermissionGranted( isGranted: Boolean, errorMessage: String)
    }

    interface UserActionListener {
        fun getSelectedLocation()
        fun getLastLocation()
        fun setGoogleMap(map: GoogleMap)
        fun setFusedLocationProviderClient(fusedLocationClient: FusedLocationProviderClient)
        fun getAddressFromLatLng(mapsActivity: MapsActivity)
        fun checkPermissionLocation(mapsActivity: MapsActivity)
    }
}