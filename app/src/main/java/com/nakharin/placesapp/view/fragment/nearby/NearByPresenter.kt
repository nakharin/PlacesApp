package com.nakharin.placesapp.view.fragment.nearby

import android.location.Location
import com.nakharin.placesapp.network.ConnectionService
import com.nakharin.placesapp.view.fragment.nearby.model.NearByItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NearByPresenter constructor(private val view: NearByContact.View) : NearByContact.UserActionListener {

    private var nearByItemList: ArrayList<NearByItem> = arrayListOf()

    override fun getNearbyPlaces(type: String, location: Location): Disposable {
        view.onShowLoading()
        return ConnectionService.getApiService().getNearbyPlaces(type, "${location.latitude},${location.longitude}", 1000)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ it ->
                    view.onHideLoading()
                    if (it.status == "OK") {
                        it.results.forEach {
                            val nearByItem = NearByItem(it.id!!, it.icon!!, "${it.name} ${it.vicinity}", "URL Link", it.geometry?.location?.lat!!, it.geometry?.location?.lng!!, false)
                            nearByItemList.add(nearByItem)
                        }

                        view.onResponseSuccess(nearByItemList)
                    }
                }, {
                    view.onHideLoading()
                    view.onResponseError(it.localizedMessage)
                })
    }
}