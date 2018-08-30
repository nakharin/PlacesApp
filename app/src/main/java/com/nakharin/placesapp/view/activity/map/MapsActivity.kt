package com.nakharin.placesapp.view.activity.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
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

        presenter = MapsPresenter(this)

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

    /********************************************************************************************
     ************************************ Listener **********************************************
     ********************************************************************************************/

    @SuppressLint("MissingPermission")
    private val onMapReadyCallback = OnMapReadyCallback {
        mMap = it

        mMap.uiSettings!!.isZoomControlsEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        presenter.checkPermissionLocation(this)
    }

    private val onCameraIdleListener = GoogleMap.OnCameraIdleListener {
        presenter.getAddressFromLatLng(this)
    }

    private val onClickListener = View.OnClickListener {
        presenter.getSelectedLocation()
    }

    /********************************************************************************************
     ************************************ ContactView *******************************************
     ********************************************************************************************/

    override fun setUpToolbar() {
        toolbar.title = getString(R.string.str_select_location)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onLocationPermissionGranted(isGranted: Boolean, errorMessage: String) {
        if (isGranted) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            presenter.setGoogleMap(mMap)
            presenter.setFusedLocationProviderClient(fusedLocationClient)
            presenter.getLastLocation()
        } else {
            longToast(errorMessage)
        }
    }

    override fun onMoveToCurrentLocation(currentLatLng: LatLng, zoomLV: Float) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLV))
        mMap.setOnCameraIdleListener(onCameraIdleListener)
    }

    override fun onGotCenterLocation(latLng: LatLng) {
        val e = EventSendSelectedLocation()
        e.latLng = latLng
        BusProvider.getInstance().post(e)
        finish()
    }

    override fun setAddressName(addressName: String) {
        txtAddressName.text = addressName
    }
}
