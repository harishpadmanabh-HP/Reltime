package com.accubits.reltime.activity.notification

import android.view.ViewGroup
import android.widget.TextView
import com.accubits.reltime.R
import com.accubits.reltime.helpers.Utils
import smartadapter.viewholder.SmartViewHolder

open class NotificationHeaderViewHolder(parentView: ViewGroup) :
    SmartViewHolder<String>(parentView, R.layout.item_notification_header) {


    override fun bind(item: String) {
        val textView = itemView.findViewById(R.id.tvTitle) as TextView
        textView.text = Utils.getDatesInString(item)

    }
}
