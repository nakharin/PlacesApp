package com.nakharin.placesapp.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class PlaceFavorite : RealmObject() {

    @PrimaryKey
    @Required
    var uuId: String = ""

    @Required
    var id: String = ""

    var icon: String = ""

    @Required
    var name: String = ""

    var url: String = ""

    var lat: Double = 0.0

    var lng: Double = 0.0

    var isFavorite: Boolean = false
}