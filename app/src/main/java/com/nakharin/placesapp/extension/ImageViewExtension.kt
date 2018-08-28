package com.nakharin.placesapp.extension

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.load(s: String) {
    Glide.with(this.context)
            .load(s)
            .into(this)
}