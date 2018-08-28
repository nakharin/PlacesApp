package com.nakharin.placesapp.view.activity.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nakharin.placesapp.R
import com.nakharin.placesapp.view.fragment.favorite.FavoriteFragment
import com.nakharin.placesapp.view.fragment.nearby.NearByFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainActivityContact.View {

    private lateinit var viewPagerMainActivityAdapter: ViewPagerMainActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpToolbar()

        setUpViewPager()
    }

    override fun setUpToolbar() {
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
    }

    override fun setUpViewPager() {
        viewPagerMainActivityAdapter = ViewPagerMainActivityAdapter(supportFragmentManager)
        viewPagerMainActivityAdapter.add(NearByFragment.newInstance(), "NEARBY")
        viewPagerMainActivityAdapter.add(FavoriteFragment.newInstance(), "FAVORITE")

        viewPager.adapter = viewPagerMainActivityAdapter
        tabLayout.setupWithViewPager(viewPager)
    }
}