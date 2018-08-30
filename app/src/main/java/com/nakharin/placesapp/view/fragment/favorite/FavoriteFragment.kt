package com.nakharin.placesapp.view.fragment.favorite

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nakharin.placesapp.R
import com.nakharin.placesapp.adapter.NearByAdapter
import com.nakharin.placesapp.model.NearByItem
import com.nakharin.placesapp.utility.BusProvider
import com.nakharin.placesapp.view.fragment.nearby.event.EventSendReloadFavorite
import com.pawegio.kandroid.toast
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.fragment_favorite.view.*

class FavoriteFragment : Fragment(), FavoriteContact.View {

    companion object {

        fun newInstance(): FavoriteFragment {
            val fragment = FavoriteFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var rootView: View

    private lateinit var presenter: FavoriteContact.UserActionListener

    private lateinit var nearByAdapter: NearByAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)

        presenter = FavoritePresenter(this)

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_favorite, container, false)
        initInstances(savedInstanceState)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        presenter.reloadFromRealm()

        rootView.swipeRefresh.setOnRefreshListener(onRefreshListener)

        nearByAdapter.setOnFavoriteListener(onFavoriteListener)
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    private fun init(savedInstanceState: Bundle?) {
        // Init Fragment level's variable(s) here
    }

    private fun initInstances(savedInstanceState: Bundle?) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState

        val linearLayoutManager = LinearLayoutManager(context)
        rootView.recyclerFavorite.layoutManager = linearLayoutManager

        nearByAdapter = NearByAdapter(arrayListOf())
        rootView.recyclerFavorite.adapter = nearByAdapter
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save Instance (Fragment level's variables) State here
    }

    private fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // Restore Instance (Fragment level's variables) State here
    }

    /********************************************************************************************
     ************************************ Listener **********************************************
     ********************************************************************************************/

    private val onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        presenter.reloadFromRealm()
    }

    private val onFavoriteListener = object : NearByAdapter.OnFavoriteListener {
        override fun onFavorite(position: Int, isFavorite: Boolean) {
            presenter.removeFavorite(position)
        }
    }

    /********************************************************************************************
     ************************************ ContactView *******************************************
     ********************************************************************************************/

    override fun onResponseFromRealm(nearByItemList: ArrayList<NearByItem>) {
        rootView.swipeRefresh.isRefreshing = false
        nearByAdapter.addAllItem(nearByItemList)
    }

    override fun onRemoveFromRealmSuccessful(position: Int) {
        nearByAdapter.removeItem(position)
    }

    override fun showToast(message: String) {
        toast(message)
    }

    /********************************************************************************************
     ************************************ Event Bus *********************************************
     ********************************************************************************************/

    @Subscribe
    fun onRecivedReloadFavorite(event: EventSendReloadFavorite) {
        presenter.reloadFromRealm()
    }
}
