package com.nakharin.placesapp.view.activity.map

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.nakharin.placesapp.R
import com.nakharin.placesapp.utility.BusProvider
import com.nakharin.placesapp.view.fragment.nearby.event.EventSendSelectedLocation
import com.pawegio.kandroid.longToast
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), MapsContact.View {

    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private lateinit var presenter: MapsContact.UserActionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setUpToolbar()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(onMapReadyCallback)
        imgSelectLocation.setOnClickListener(onClickListener)
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

    override fun setUpToolbar() {
        toolbar.title = getString(R.string.str_select_location)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onMoveToCurrentLocation(currentLatLng: LatLng, zoomLV: Float) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLV))
    }

    override fun onGotCenterLocation(latLng: LatLng) {
        val e = EventSendSelectedLocation()
        e.latLng = latLng
        BusProvider.getInstance().post(e)
        finish()
    }

    @SuppressLint("MissingPermission")
    private fun checkPermissionLocation() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        mMap.isMyLocationEnabled = true
                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MapsActivity)
                        presenter = MapsPresenter(this@MapsActivity, mMap, fusedLocationClient)
                        presenter.getLastLocation()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        if (response.isPermanentlyDenied) {
                            AlertDialog.Builder(this@MapsActivity)
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
                    longToast(it.toString())
                }.onSameThread()
                .check()
    }

    @SuppressLint("MissingPermission")
    private val onMapReadyCallback = OnMapReadyCallback {
        mMap = it

        mMap.uiSettings!!.isZoomControlsEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        checkPermissionLocation()
    }

    private val onClickListener = View.OnClickListener {
        if (it == imgSelectLocation) {
            presenter.getSelectedLocation()
        }
    }
}
