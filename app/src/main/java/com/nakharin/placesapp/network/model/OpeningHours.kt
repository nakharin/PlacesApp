package com.nakharin.placesapp.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class OpeningHours {

    /**
     *
     * @return
     * The openNow
     */
    /**
     *
     * @param openNow
     * The open_now
     */
    @SerializedName("open_now")
    @Expose
    var openNow: Boolean? = null
    /**
     *
     * @return
     * The weekdayText
     */
    /**
     *
     * @param weekdayText
     * The weekday_text
     */
    @SerializedName("weekday_text")
    @Expose
    var weekdayText: List<Any> = ArrayList()
}
