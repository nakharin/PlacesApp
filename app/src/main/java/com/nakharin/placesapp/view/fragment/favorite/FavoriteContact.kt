package com.nakharin.placesapp.view.fragment.favorite

import com.nakharin.placesapp.model.NearByItem

class FavoriteContact {

    interface View {
        fun onResponseFromRealm(nearByItemList: ArrayList<NearByItem>)
        fun onRemoveFromRealmSuccessful(position: Int)
        fun showToast(message: String)
    }

    interface UserActionListener {
        fun reloadFromRealm()
        fun removeFavorite(position: Int)
    }
}