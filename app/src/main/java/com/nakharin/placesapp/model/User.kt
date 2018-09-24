package com.nakharin.placesapp.model

data class User(var name: String = "") {

    var surname: String = ""
    get() {
        return field + "555"
    }

    set(value) {
        field = value + "6666"
    }

}