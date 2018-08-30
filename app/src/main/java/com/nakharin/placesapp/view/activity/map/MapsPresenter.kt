package com.nakharin.placesapp.view.activity.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.support.v7.app.AlertDialog
import co.metalab.asyncawait.async
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks.await
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException
import java.util.*

class MapsPresenter(private val view: MapsContact.View) : MapsContact.UserActionListener {

    private var mMap: GoogleMap? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLatLng: LatLng? = null

    override fun setGoogleMap(map: GoogleMap) {
        mMap = map
    }

    override fun setFusedLocationProviderClient(fusedLocationClient: FusedLocationProviderClient) {
        mFusedLocationClient = fusedLocationClient
    }

    override fun checkPermissionLocation(mapsActivity: MapsActivity) {
        Dexter.withActivity(mapsActivity)
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
                            AlertDialog.Builder(mapsActivity)
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

    override fun getSelectedLocation() {
        mLatLng = mMap?.cameraPosition?.target
        mLatLng?.let {
            view.onGotCenterLocation(it)
        }
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation() {
        mFusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                view.onMoveToCurrentLocation(currentLatLng, 18f)
            }
        }
    }

    override fun getAddressFromLatLng(mapsActivity: MapsActivity) {
        mLatLng = mMap?.cameraPosition?.target
        mLatLng?.let {
            async {
                try {
                    val geocoder = Geocoder(mapsActivity, Locale.getDefault())
                    val addresses = await { geocoder.getFromLocation(it.latitude, it.longitude, 1) }
                    if (addresses != null && addresses.size > 0) {
                        val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        val city = addresses[0].locality
                        val state = addresses[0].adminArea
                        val country = addresses[0].countryName
                        val postalCode = addresses[0].postalCode
                        val knownName = addresses[0].featureName // Only if available else return NULL

                        view.setAddressName("$address, $city, $state, $postalCode, $knownName, $country")
                    } else {
                        view.setAddressName("Address not found")
                    }
                } catch (e: IOException) {
                    view.setAddressName("Address Failed: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }
}