package com.nakharin.placesapp.view.fragment.favorite

import com.nakharin.placesapp.model.NearByItem
import com.nakharin.placesapp.realm.PlaceFavorite
import io.realm.Realm
import java.util.ArrayList

class FavoritePresenter(private val view: FavoriteContact.View) : FavoriteContact.UserActionListener {

    private var mRealm: Realm? = null
    private var nearByItemList: ArrayList<NearByItem> = arrayListOf()

    override fun reloadFromRealm() {
        mRealm = Realm.getDefaultInstance()
        mRealm?.let { realm ->
            val result = realm.where(PlaceFavorite::class.java).findAllAsync()
            nearByItemList.clear()
            result.forEach {
                val nearByItem = NearByItem(it.id, it.icon, it.name, it.url, it.lat, it.lng, it.isFavorite)
                nearByItemList.add(nearByItem)
            }

            view.onResponseFromRealm(nearByItemList)
        }
    }

    override fun removeFavorite(position: Int) {
        val id = nearByItemList[position].id
        mRealm?.let { realm ->
            val result = realm.where(PlaceFavorite::class.java).findAllAsync()
            result.where().equalTo("id", id)
            val isDeleted = result.deleteAllFromRealm()
            if (isDeleted) {
                view.onRemoveFromRealmSuccessful(position)
            } else {
                view.showToast("Delete Failed")
            }
        }
    }
}