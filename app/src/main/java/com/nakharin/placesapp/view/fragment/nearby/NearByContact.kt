package com.nakharin.placesapp.view.fragment.nearby

import android.location.Location
import io.reactivex.disposables.Disposable

class NearByContact {

    interface View {
        fun onShowLoading()
        fun onHideLoading()
        fun onResponseSuccess(name: String?, vicinity: String?)
        fun onResponseError(localizedMessage: String)
    }

    interface UserActionListener {
        fun getNearbyPlaces(type: String, location: Location) : Disposable
    }
}