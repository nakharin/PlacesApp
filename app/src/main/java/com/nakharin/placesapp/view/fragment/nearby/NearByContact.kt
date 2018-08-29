package com.nakharin.placesapp.view.fragment.nearby

import com.nakharin.placesapp.view.fragment.nearby.model.NearByItem
import io.reactivex.disposables.Disposable

class NearByContact {

    interface View {
        fun onShowLoading()
        fun onHideLoading()
        fun onResponseSuccess(nearByItemList: ArrayList<NearByItem>)
        fun onResponseError(localizedMessage: String)
        fun onIntentToMap()
    }

    interface UserActionListener {
        fun getNearbyPlaces(type: String, lat: Double, lng: Double) : Disposable
        fun goToMap()
    }
}