package com.accubits.reltime.activity.v2.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.transfer.adapter.RecentTransactionAccountsAdapter
import com.accubits.reltime.activity.v2.transfer.adapter.RecentTransactionViewHolder
import com.accubits.reltime.activity.v2.wallet.home.model.AccountItem
import com.accubits.reltime.databinding.ItemRecentTransactionBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.RowData

class HomeRecentTransferAdapter constructor(
  val  onItemClickListener: RecentTransactionAccountsAdapter.OnItemClickListener
) :
    RecyclerView.Adapter<RecentTransactionViewHolder>() {


    val list = arrayListOf<RowData>()

    override fun onBindViewHolder(holder: RecentTransactionViewHolder, position: Int) {
        val currentItem = list[position]
        holder.setData(currentItem,onItemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentTransactionViewHolder {
        return RecentTransactionViewHolder(
            ItemRecentTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

 fun setItems(rowItems: List<RowData>){
     list.clear()
     list.addAll(rowItems)
     notifyItemRangeChanged(0, list.size) }

}

