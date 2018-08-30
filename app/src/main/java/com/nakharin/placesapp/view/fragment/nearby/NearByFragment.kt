package com.nakharin.placesapp.view.fragment.nearby

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
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
import com.nakharin.placesapp.adapter.NearByAdapter
import com.nakharin.placesapp.model.NearByItem
import com.nakharin.placesapp.view.fragment.nearby.event.EventSendReloadFavorite
import com.pawegio.kandroid.longToast
import com.pawegio.kandroid.toast
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_nearby.view.*

class NearByFragment : Fragment(), NearByContact.View {

    companion object {

        public const val RESULT_LOCATION_LAT = "RESULT_LOCATION_LAT"
        public const val RESULT_LOCATION_LNG = "RESULT_LOCATION_LNG"
        private const val RESULT_LOCATION_CODE = 1

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
        rootView.swipeRefresh.setOnRefreshListener(onRefreshListener)

        nearByAdapter.setOnFavoriteListener(onFavoriteListener)
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOCATION_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    onShowLoading()
                    val latitude = it.getDoubleExtra(RESULT_LOCATION_LAT, 0.0)
                    val longitude = it.getDoubleExtra(RESULT_LOCATION_LNG, 0.0)
                    val disposable = presenter.getNearbyPlaces("restaurant", latitude, longitude)
                    compositeDisposable.add(disposable)
                }
            }
        }
    }

    /********************************************************************************************
     ************************************ Listener **********************************************
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

    private val onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        presenter.getLastLocation(activity!!)
    }

    /********************************************************************************************
     ************************************ ContactView *******************************************
     ********************************************************************************************/

    override fun onLocationPermissionGranted(isGranted: Boolean, errorMessage: String) {
        if (isGranted) {
            onShowLoading()
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
        rootView.fabMap.visibility = View.VISIBLE
    }

    override fun onHideLoading() {
        rootView.loadingView.visibility = View.GONE
        rootView.swipeRefresh.isRefreshing = false
    }

    override fun onResponseSuccess(nearByItemList: ArrayList<NearByItem>) {
        nearByAdapter.addAllItem(nearByItemList)
    }

    override fun onResponseError(localizedMessage: String) {
        longToast(localizedMessage)
    }

    override fun onIntentToMap() {
        val i = Intent(context, MapsActivity::class.java)
        startActivityForResult(i, RESULT_LOCATION_CODE)
    }

    override fun showToast(message: String) {
        toast(message)
    }

    override fun sendReloadFavorite() {
        val e = EventSendReloadFavorite()
        BusProvider.getInstance().post(e)
    }
}
