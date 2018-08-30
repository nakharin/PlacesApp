package com.nakharin.placesapp.realm

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class PlaceFavorite : RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    @Required
    var id: String = ""

    @SerializedName("icon")
    var icon: String = ""

    @SerializedName("name")
    @Required
    var name: String = ""

    @SerializedName("url")
    var url: String = ""

    @SerializedName("lat")
    var lat: Double = 0.0

    @SerializedName("lng")
    var lng: Double = 0.0

    @SerializedName("isFavorite")
    var isFavorite: Boolean = false
}