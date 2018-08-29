package com.nakharin.placesapp.view.fragment.nearby

import android.support.v4.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.nakharin.placesapp.view.fragment.nearby.model.NearByItem
import io.reactivex.disposables.Disposable

class NearByContact {

    interface View {
        fun onShowLoading()
        fun onHideLoading()
        fun onResponseSuccess(nearByItemList: ArrayList<NearByItem>)
        fun onResponseError(localizedMessage: String)
        fun onIntentToMap()
        fun onGotLastLocation(latLng: LatLng)
        fun onLocationPermissionGranted( isGranted: Boolean, errorMessage: String)
    }

    interface UserActionListener {
        fun setFusedLocationProviderClient(fusedLocationClient: FusedLocationProviderClient)
        fun checkPermissionLocation(activity: FragmentActivity)
        fun getLastLocation(activity: FragmentActivity)
        fun getNearbyPlaces(type: String, lat: Double, lng: Double) : Disposable
        fun addFavoritePlace(position: Int, isFavorite: Boolean)
        fun goToMap()
    }
}