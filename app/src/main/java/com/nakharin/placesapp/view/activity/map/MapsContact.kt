package com.nakharin.placesapp.view.activity.map

import com.google.android.gms.maps.model.LatLng

class MapsContact {

    interface View {
        fun setUpToolbar()
        fun onMoveToCurrentLocation(currentLatLng: LatLng, zoomLV: Float)
        fun onGotCenterLocation(latLng: LatLng)
    }

    interface UserActionListener {
        fun getSelectedLocation()
        fun getLastLocation()
    }
}