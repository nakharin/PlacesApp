package com.nakharin.placesapp.view.fragment.nearby

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(rootView.context)
        presenter.setFusedLocationProviderClient(fusedLocationClient)
        presenter.checkPermissionLocation(activity!!)

        rootView.fabMap.setOnClickListener(onClickListener)
        rootView.recyclerNearBy.addOnItemClickListener(onItemClickListener)
        nearByAdapter.setOnFavoriteListener(onFavoriteListener)
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun init(savedInstanceState: Bundle?) {
        // Init Fragment level's variable(s) here
    }

    private fun initInstances(savedInstanceState: Bundle?) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState

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

    /********************************************************************************************
     ************************************ Listener *********************************************
     ********************************************************************************************/

    private val onClickListener = View.OnClickListener {
        presenter.goToMap()
    }

    private val onItemClickListener: RecyclerItemClickListener.OnClickListener = object : RecyclerItemClickListener.OnClickListener {
        override fun onItemClick(position: Int, view: View) {
        }
    }

    private val onFavoriteListener = object : NearByAdapter.OnFavoriteListener {
        override fun onFavorite(position: Int, isFavorite: Boolean) {
            nearByAdapter.updateFavoriteItem(position, isFavorite)
            presenter.addFavoritePlace(position, isFavorite)
        }
    }

    /********************************************************************************************
     ************************************ ContactView ********************************************
     ********************************************************************************************/

    override fun onLocationPermissionGranted(isGranted: Boolean, errorMessage: String) {
        if (isGranted) {
            presenter.getLastLocation(activity!!)
        } else {
            longToast(errorMessage)
        }
    }

    override fun onGotLastLocation(latLng: LatLng) {
        val disposable = presenter.getNearbyPlaces("restaurant", latLng.latitude, latLng.longitude)
        compositeDisposable.add(disposable)
    }

    override fun onShowLoading() {
        rootView.loadingView.visibility = View.VISIBLE
    }

    override fun onHideLoading() {
        rootView.loadingView.visibility = View.GONE
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
