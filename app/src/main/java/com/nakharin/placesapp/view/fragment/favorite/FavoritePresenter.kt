package com.nakharin.placesapp.view.fragment.favorite

import com.nakharin.placesapp.model.NearByItem
import com.nakharin.placesapp.realm.PlaceFavorite
import io.realm.Realm
import java.util.*

class FavoritePresenter(private val view: FavoriteContact.View) : FavoriteContact.UserActionListener {

    private var nearByItemList: ArrayList<NearByItem> = arrayListOf()

    override fun reloadFromRealm() {
        val mRealm = Realm.getDefaultInstance()
        val result = mRealm.where(PlaceFavorite::class.java).findAllAsync()
        nearByItemList.clear()
        result.forEach {
            val nearByItem = NearByItem(it.id, it.icon, it.name, it.url, it.lat, it.lng, it.isFavorite)
            nearByItemList.add(nearByItem)
        }
        mRealm.close()
        view.onResponseFromRealm(nearByItemList)
    }

    private fun deleteFromRealm(id: String, position: Int) {
        Realm.getDefaultInstance().use { r ->
            r.executeTransaction {
                val isDeleted = it.where(PlaceFavorite::class.java).equalTo("id", id).findAll().deleteAllFromRealm()
                if (isDeleted) {
                    view.onRemoveFromRealmSuccessful(position)
                } else {
                    view.showToast("Delete Failed")
                }
            }
        }
    }

    override fun removeFavorite(position: Int) {
        val id = nearByItemList[position].id
        deleteFromRealm(id, position)
    }
}