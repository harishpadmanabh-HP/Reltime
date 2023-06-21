package com.accubits.reltime.activity.v2.paymentHistory

import android.view.ViewGroup
import android.widget.TextView
import com.accubits.reltime.R
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.MyPaymentHistory
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.model.ViewEvent
import smartadapter.viewevent.viewholder.CustomViewEventListenerHolder
import smartadapter.viewholder.SmartAdapterHolder
import smartadapter.viewholder.SmartViewHolder

open class PaymentHistoryViewHolder(parentView: ViewGroup) :
    SmartViewHolder<MyPaymentHistory>(parentView, R.layout.lending_trans_item),
    CustomViewEventListenerHolder,
    SmartAdapterHolder {

    override lateinit var customViewEventListener: (ViewEvent) -> Unit
    override var smartRecyclerAdapter: SmartRecyclerAdapter? = null

    init {
    }

    override fun bind(item: MyPaymentHistory) {

        val tvIntrest: TextView = itemView.findViewById(R.id.tv_interest)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvRto: TextView = itemView.findViewById(R.id.tv_rto)

        val date = Utils.getDateCurrentTimeZone1(item.created_date)
        tvDate.text = date
        tvIntrest.text =
        itemView.context.resources.getString(R.string.n_n,itemView.context.resources.getText(R.string.interest),
            item.interest_rate + "%")
        tvRto.text = Utils.coinCodeWithSymbol(tvRto.context,item.coinCode,item.symbol) + " " + item.amount


    }
}