package com.nakharin.placesapp.view.activity.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ViewPagerMainActivityAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val tabNames: ArrayList<String> = ArrayList()
    private val fragments: ArrayList<Fragment> = ArrayList()

    fun add(fragment: Fragment, title: String) {
        tabNames.add(title)
        fragments.add(fragment)
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabNames[position]
    }
}