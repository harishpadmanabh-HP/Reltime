package com.accubits.reltime.activity.v2.welcomeScreen.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageAdapter(fm: FragmentManager, val list: List<Fragment>, val title: List<String>? = null) :
    FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        title?.let {
            return it[position]
        }?:return super.getPageTitle(position)
    }

}