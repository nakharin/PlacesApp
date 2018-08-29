package com.nakharin.placesapp.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

class PlaceFavorite : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    var icon: String = ""

    @Required
    var name: String = ""

    var url: String = ""

    @Required
    var lat: Double = 0.0

    @Required
    var lng: Double = 0.0

    var isFavorite: Boolean = false
}