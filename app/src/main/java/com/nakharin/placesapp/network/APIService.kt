package com.nakharin.placesapp.network

import com.nakharin.placesapp.network.model.NearLocation
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface APIService {
    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyDN7RJFmImYAca96elyZlE5s_fhX-MMuhk")
    fun getNearbyPlaces(@Query("type") type: String, @Query("location") location: String, @Query("radius") radius: Int): Observable<NearLocation>
}