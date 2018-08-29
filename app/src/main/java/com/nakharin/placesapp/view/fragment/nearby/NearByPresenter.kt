package com.nakharin.placesapp.view.fragment.nearby

import android.Manifest
import android.annotation.SuppressLint
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.nakharin.placesapp.network.ConnectionService
import com.nakharin.placesapp.view.fragment.nearby.model.NearByItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NearByPresenter constructor(private val view: NearByContact.View) : NearByContact.UserActionListener {

    private var nearByItemList: ArrayList<NearByItem> = arrayListOf()
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    override fun setFusedLocationProviderClient(fusedLocationClient: FusedLocationProviderClient) {
        mFusedLocationClient= fusedLocationClient
    }

    @SuppressLint("MissingPermission")
    override fun checkPermissionLocation(activity: FragmentActivity) {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        view.onLocationPermissionGranted(true, "")
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        if (response.isPermanentlyDenied) {
                            AlertDialog.Builder(activity)
                                    .setTitle("Need Permissions")
                                    .setMessage("This app needs permission to use this feature. You can grant them in app settings.")
                                    .setPositiveButton("GOTO SETTINGS") { d, _ ->
                                        d.dismiss()
                                    }.setNegativeButton("Cancel") { d, _ ->
                                        d.dismiss()
                                    }.show()
                        }
                    }

                })
                .withErrorListener {
                    view.onLocationPermissionGranted(false, it.toString())
                }.onSameThread()
                .check()
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation(activity: FragmentActivity) {
        mFusedLocationClient?.lastLocation?.addOnSuccessListener(activity) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                view.onGotLastLocation(latLng)
            }
        }
    }

    override fun getNearbyPlaces(type: String, lat: Double, lng: Double): Disposable {
        view.onShowLoading()

        return ConnectionService.getApiService().getNearbyPlaces(type, "$lat,$lng", 1000)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ it ->
                    view.onHideLoading()
                    if (it.status == "OK") {
                        nearByItemList.clear()
                        it.results.forEach {
                            val nearByItem = NearByItem(it.id!!, it.icon!!, "${it.name} ${it.vicinity}", "URL Link", it.geometry?.location?.lat!!, it.geometry?.location?.lng!!, false)
                            nearByItemList.add(nearByItem)
                        }

                        view.onResponseSuccess(nearByItemList)
                    } else {
                        view.onResponseError(it.status!!)
                    }
                }, {
                    view.onHideLoading()
                    view.onResponseError(it.localizedMessage)
                })
    }

    override fun addFavoritePlace(position: Int, isFavorite: Boolean) {
    }

    override fun goToMap() {
        view.onIntentToMap()
    }
}