package com.nakharin.placesapp.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class Photo {

    /**
     *
     * @return
     * The height
     */
    /**
     *
     * @param height
     * The height
     */
    @SerializedName("height")
    @Expose
    var height: Int? = null
    /**
     *
     * @return
     * The htmlAttributions
     */
    /**
     *
     * @param htmlAttributions
     * The html_attributions
     */
    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<String> = ArrayList()
    /**
     *
     * @return
     * The photoReference
     */
    /**
     *
     * @param photoReference
     * The photo_reference
     */
    @SerializedName("photo_reference")
    @Expose
    var photoReference: String? = null
    /**
     *
     * @return
     * The width
     */
    /**
     *
     * @param width
     * The width
     */
    @SerializedName("width")
    @Expose
    var width: Int? = null

}
