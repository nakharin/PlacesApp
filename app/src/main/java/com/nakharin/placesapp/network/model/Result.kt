package com.nakharin.placesapp.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class Result {

    /**
     *
     * @return
     * The geometry
     */
    /**
     *
     * @param geometry
     * The geometry
     */
    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null
    /**
     *
     * @return
     * The icon
     */
    /**
     *
     * @param icon
     * The icon
     */
    @SerializedName("icon")
    @Expose
    var icon: String? = null
    /**
     *
     * @return
     * The id
     */
    /**
     *
     * @param id
     * The id
     */
    @SerializedName("id")
    @Expose
    var id: String? = null
    /**
     *
     * @return
     * The name
     */
    /**
     *
     * @param name
     * The name
     */
    @SerializedName("name")
    @Expose
    var name: String? = null
    /**
     *
     * @return
     * The openingHours
     */
    /**
     *
     * @param openingHours
     * The opening_hours
     */
    @SerializedName("opening_hours")
    @Expose
    var openingHours: OpeningHours? = null
    /**
     *
     * @return
     * The photos
     */
    /**
     *
     * @param photos
     * The photos
     */
    @SerializedName("photos")
    @Expose
    var photos: List<Photo> = ArrayList()
    /**
     *
     * @return
     * The placeId
     */
    /**
     *
     * @param placeId
     * The place_id
     */
    @SerializedName("place_id")
    @Expose
    var placeId: String? = null
    /**
     *
     * @return
     * The rating
     */
    /**
     *
     * @param rating
     * The rating
     */
    @SerializedName("rating")
    @Expose
    var rating: Double? = null
    /**
     *
     * @return
     * The reference
     */
    /**
     *
     * @param reference
     * The reference
     */
    @SerializedName("reference")
    @Expose
    var reference: String? = null
    /**
     *
     * @return
     * The scope
     */
    /**
     *
     * @param scope
     * The scope
     */
    @SerializedName("scope")
    @Expose
    var scope: String? = null
    /**
     *
     * @return
     * The types
     */
    /**
     *
     * @param types
     * The types
     */
    @SerializedName("types")
    @Expose
    var types: List<String> = ArrayList()
    /**
     *
     * @return
     * The vicinity
     */
    /**
     *
     * @param vicinity
     * The vicinity
     */
    @SerializedName("vicinity")
    @Expose
    var vicinity: String? = null
    /**
     *
     * @return
     * The priceLevel
     */
    /**
     *
     * @param priceLevel
     * The price_level
     */
    @SerializedName("price_level")
    @Expose
    var priceLevel: Int? = null
}
