package com.nakharin.placesapp.view.fragment.nearby

import android.location.Location
import io.reactivex.disposables.Disposable

interface ContractPresenter {
    fun getNearbyPlaces(type: String, latLng: Location) : Disposable
}