package com.nakharin.placesapp

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(realmConfig)
    }
}