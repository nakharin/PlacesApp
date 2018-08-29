package com.nakharin.placesapp.view.fragment.nearby

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.nakharin.placesapp.R
import com.nakharin.placesapp.extension.RecyclerItemClickListener
import com.nakharin.placesapp.extension.addOnItemClickListener
import com.nakharin.placesapp.utility.BusProvider
import com.nakharin.placesapp.view.activity.map.MapsActivity
import com.nakharin.placesapp.view.fragment.nearby.adapter.NearByAdapter
import com.nakharin.placesapp.view.fragment.nearby.event.EventSendSelectedLocation
import com.nakharin.placesapp.view.fragment.nearby.model.NearByItem
import com.pawegio.kandroid.longToast
import com.squareup.otto.Subscribe
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_nearby.view.*

class NearByFragment : Fragment(), NearByContact.View {

    companion object {

        fun newInstance(): NearByFragment {
            val fragment = NearByFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var rootView: View

    private lateinit var presenter: NearByContact.UserActionListener

    private val compositeDisposable = CompositeDisposable()

    private lateinit var progressDialog: ProgressDialog

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var nearByAdapter: NearByAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)

        presenter = NearByPresenter(this)

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_nearby, container, false)
        initInstances(savedInstanceState)
        return rootView
    }

    @SuppressLint("MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        checkPermissionLocation()

        rootView.imgMap.setOnClickListener(onClickListener)
        rootView.recyclerNearBy.addOnItemClickListener(onItemClickListener)
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

    @SuppressLint("MissingPermission")
    private fun checkPermissionLocation() {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                val disposable = presenter.getNearbyPlaces("restaurant", location.latitude, location.longitude)
                                compositeDisposable.add(disposable)
                            }
                        }.addOnFailureListener {
                            longToast(it.localizedMessage)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        if (response.isPermanentlyDenied) {
                            AlertDialog.Builder(context!!)
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun init(savedInstanceState: Bundle?) {
        // Init Fragment level's variable(s) here
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("")
        progressDialog.setMessage("Connecting, Please wait...")
    }

    private fun initInstances(savedInstanceState: Bundle?) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(rootView.context)

        val linearLayoutManager = LinearLayoutManager(context)
        rootView.recyclerNearBy.layoutManager = linearLayoutManager

        nearByAdapter = NearByAdapter(arrayListOf())
        rootView.recyclerNearBy.adapter = nearByAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save Instance (Fragment level's variables) State here
    }

    private fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // Restore Instance (Fragment level's variables) State here
    }

    override fun onShowLoading() {
        progressDialog.show()
    }

    override fun onHideLoading() {
        progressDialog.hide()
    }

    override fun onResponseSuccess(nearByItemList: ArrayList<NearByItem>) {
        nearByAdapter.addAllItem(nearByItemList)
    }

    override fun onResponseError(localizedMessage: String) {
        longToast(localizedMessage)
    }

    override fun onIntentToMap() {
        val i = Intent(context, MapsActivity::class.java)
        startActivity(i)
    }

    private val onClickListener = View.OnClickListener {
        presenter.goToMap()
    }

    private val onItemClickListener: RecyclerItemClickListener.OnClickListener = object : RecyclerItemClickListener.OnClickListener {
        override fun onItemClick(position: Int, view: View) {
        }
    }

    /********************************************************************************************
     ************************************ Event Bus *********************************************
     ********************************************************************************************/

    @Subscribe
    fun onRecivedSelectedLocation(event: EventSendSelectedLocation) {
        Log.i("NearByFragment","5555555")
        val latLng = event.latLng
        latLng?.let {
            val disposable = presenter.getNearbyPlaces("restaurant", it.latitude, it.longitude)
            compositeDisposable.add(disposable)
            longToast("${latLng.latitude}, ${latLng.longitude}")
        }
    }
}
