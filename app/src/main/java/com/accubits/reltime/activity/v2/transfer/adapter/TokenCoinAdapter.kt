package com.accubits.reltime.activity.v2.transfer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.common.walletpicker.model.WalletValidationResponse
import com.accubits.reltime.utils.Extensions.getCoinCode
import com.accubits.reltime.utils.convertRTOtoEURO

class TokenCoinAdapter(
    private val context: Context,
    private val arrayList: ArrayList<String>
) : BaseAdapter() {
    override fun getCount(): Int {
        return arrayList.size

    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_token_coin, parent, false)
        val tvToken: TextView = view.findViewById(R.id.tv_token)
        tvToken.text =tvToken.context.resources.getString(R.string.reltime_n,arrayList[position]).convertRTOtoEURO()
        return view
    }

}