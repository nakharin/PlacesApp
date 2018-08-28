package com.nakharin.placesapp.view.fragment.nearby

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.support.annotation.RequiresPermission
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nakharin.placesapp.R
import com.pawegio.kandroid.toast
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_nearby.view.*

class NearByFragment : Fragment(), ContractView {

    companion object {

        fun newInstance(): NearByFragment {
            val fragment = NearByFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var rootView: View

    private val presenter : ContractPresenter by lazy {
        ContractPresenterImpl(this)
    }

    private val compositeDisposable = CompositeDisposable()

    private lateinit var progressDialog: ProgressDialog

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)

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
    @RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    private fun checkPermissionLocation() {
        askPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
            if (it.isAccepted) {
                fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        val disposable = presenter.getNearbyPlaces("restaurant", location)
                        compositeDisposable.add(disposable)
                    }
                }

            } else {
                if (it.hasForeverDenied()) {
                    AlertDialog.Builder(context!!)
                            .setTitle("Need Permissions")
                            .setMessage("This app needs permission to use this feature. You can grant them in app settings.")
                            .setPositiveButton("GOTO SETTINGS") { d, _ ->
                                d.dismiss()
                                it.goToSettings()
                            }.setNegativeButton("Cancel") { d, _ ->
                                d.dismiss()
                            }.show()
                } else {
                    toast("Permission Denied.")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
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
