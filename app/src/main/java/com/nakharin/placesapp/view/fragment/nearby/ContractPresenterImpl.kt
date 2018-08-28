package com.nakharin.placesapp.view.fragment.nearby

import android.location.Location
import com.nakharin.placesapp.network.ConnectionService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ContractPresenterImpl(val view: ContractView) : ContractPresenter {

    override fun getNearbyPlaces(type: String, location: Location): Disposable {
        view.onShowLoading()
        return ConnectionService.getApiService().getNearbyPlaces(type, "${location.latitude},${location.longitude}", 1000)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    view.onHideLoading()
                    if (it.status == "OK") {

                        val name = it.results[0].name
                        val vicinity = it.results[0].vicinity

                        view.onResponseSuccess(name, vicinity)
                    }
                }, {
                    view.onHideLoading()
                    view.onResponseError(it.localizedMessage)
                })
    }
}