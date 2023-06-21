package com.accubits.reltime.views.lend.pageing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.RowAllLending

object ContactsDiffutilsHistoty : DiffUtil.ItemCallback<RowAllLending>(){
    override fun areItemsTheSame(oldItem: RowAllLending, newItem: RowAllLending): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: RowAllLending, newItem: RowAllLending): Boolean {
        return false
    }

}


class LendingHistoryListPagedAdapter(
) : PagingDataAdapter<RowAllLending, LendingHistoryListPagedAdapter.ViewHolder>(
   ContactsDiffutilsHistoty
) {

    private var onItemClickListener: ((RowAllLending) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIntrest: TextView = view.findViewById(R.id.tv_interest)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvRto: TextView = view.findViewById(R.id.tv_rto)
    }

    fun setOnItemClickListener(listener: (RowAllLending) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        if (position == itemCount - 1) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            holder.itemView.layoutParams = params
        }
        val currentItem = getItem(position)
        currentItem?.let {
            val data = it
            val date = Utils.getDateCurrentTimeZone1(data.created_date.toDouble())
            holder.tvDate.text = date
            holder.tvIntrest.text =context.resources.getString(R.string.n_n,context.resources.getText(R.string.interest_rate),
                data.interest_rate+"%")
            holder.tvRto.text = context.resources.getString(R.string.n_n,Utils.coinCodeWithSymbol(holder.tvRto.context,
                data.coinCode,data.symbol),data.amount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.lending_trans_item, parent, false)
        )
    }
}