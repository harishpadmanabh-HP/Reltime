package com.accubits.reltime.activity.v2.wallet.qrCode

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class QrCodeAdapterV2(@NonNull fragmentActivity: FragmentActivity?,val list: List<Fragment>) :
    FragmentStateAdapter(fragmentActivity!!) {

    @NonNull
    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }
}