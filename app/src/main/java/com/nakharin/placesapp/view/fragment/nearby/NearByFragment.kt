package com.nakharin.placesapp.view.fragment.nearby

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
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
import com.pawegio.kandroid.longToast
import com.pawegio.kandroid.toast
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
                                val disposable = presenter.getNearbyPlaces("restaurant", location)
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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save Instance (Fragment level's variables) State here
    }

    private fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // Restore Instance (Fragment level's variables) State here
    }

    private val onClickListener = View.OnClickListener {
        toast("imgMap Click !!!")
    }

    override fun onShowLoading() {
        progressDialog.show()
    }

    override fun onHideLoading() {
        progressDialog.hide()
    }

    override fun onResponseSuccess(name: String?, vicinity: String?) {
        rootView.txtText.text = "$name, $vicinity"
    }

    override fun onResponseError(localizedMessage: String) {
        rootView.txtText.text = "$localizedMessage"
    }
}
