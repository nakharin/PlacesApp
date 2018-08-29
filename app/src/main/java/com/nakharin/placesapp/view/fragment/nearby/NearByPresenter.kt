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
import com.nakharin.placesapp.network.model.NearLocation
import com.nakharin.placesapp.realm.PlaceFavorite
import com.nakharin.placesapp.model.NearByItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.util.*

class NearByPresenter constructor(private val view: NearByContact.View) : NearByContact.UserActionListener {

    private var mRealm: Realm? = null
    private var nearByItemList: ArrayList<NearByItem> = arrayListOf()
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    override fun setUpRealm() {
        mRealm = Realm.getDefaultInstance()
    }

    override fun closeRealm() {
        mRealm?.close()
    }

    override fun setFusedLocationProviderClient(fusedLocationClient: FusedLocationProviderClient) {
        mFusedLocationClient = fusedLocationClient
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

    private fun convertModel(it: NearLocation): ArrayList<NearByItem> {
        return if (it.status == "OK") {
            nearByItemList.clear()
            it.results.forEach {
                val nearByItem = NearByItem(it.id!!, it.icon!!, "${it.name} ${it.vicinity}", "URL Link", it.geometry?.location?.lat!!, it.geometry?.location?.lng!!, false)
                nearByItemList.add(nearByItem)
            }

            nearByItemList
        } else {
            view.onResponseError(it.status!!)
            arrayListOf()
        }
    }

    override fun getNearbyPlaces(type: String, lat: Double, lng: Double): Disposable {

        val observable = ConnectionService.getApiService().getNearbyPlaces(type, "$lat,$lng", 1000)

        val subscription = observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map { convertModel(it) }

        return subscription.subscribe({ it ->
                    view.onHideLoading()
                    if (it.isNotEmpty()) {
                        view.onResponseSuccess(it)
                    }
                }, {
                    view.onHideLoading()
                    view.onResponseError(it.localizedMessage)
                })
    }

    override fun addFavoritePlace(position: Int, isFavorite: Boolean) {
        val nearByItem = nearByItemList[position]
        nearByItem.isFavorite = isFavorite
        if (nearByItem.isFavorite) {
            mRealm?.let {
                it.executeTransaction { realm ->
                    val placeFavorite = realm.createObject(PlaceFavorite::class.java, UUID.randomUUID().toString())
                    placeFavorite.id = nearByItem.id
                    placeFavorite.icon = nearByItem.icon
                    placeFavorite.name = nearByItem.name
                    placeFavorite.url = nearByItem.url
                    placeFavorite.lat = nearByItem.lat
                    placeFavorite.lng = nearByItem.lng
                    placeFavorite.isFavorite = nearByItem.isFavorite

                    realm.copyToRealmOrUpdate(placeFavorite)

                    view.showToast("Saved")
                }
            }
        } else {
            mRealm?.let {
                val result = it.where(PlaceFavorite::class.java).findAllAsync()
                result.where().equalTo("isFavorite", false)
                val isDeleted = result.deleteAllFromRealm()
                if (isDeleted) {
                    view.showToast("Canceled")
                } else {
                    view.showToast("Delete Failed")
                }
            }
        }
    }

    override fun goToMap() {
        view.onIntentToMap()
    }
}
