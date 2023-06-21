package com.accubits.reltime.activity.v2.transfer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.databinding.ItemRecentTransactionBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.RowData


object ContactsDiffUtilBorrows : DiffUtil.ItemCallback<RowData>() {
    override fun areItemsTheSame(
        oldItem: RowData,
        newItem: RowData
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: RowData,
        newItem: RowData
    ): Boolean {
        return false
    }

}

class RecentTransactionAccountsAdapter(
    private var context: Context, var onItemClickListener: OnItemClickListener
) : PagingDataAdapter<RowData, RecentTransactionViewHolder>(
    ContactsDiffUtilBorrows
) {


    override fun onBindViewHolder(holder: RecentTransactionViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.setData(currentItem,onItemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentTransactionViewHolder {
        context = parent.context
        return RecentTransactionViewHolder(
            ItemRecentTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    interface OnItemClickListener {

        fun OnRecentItemClickListener(rowData: RowData)
    }
}

class RecentTransactionViewHolder(private val binder: ItemRecentTransactionBinding) : RecyclerView.ViewHolder(binder.root) {


    fun setData(currentItem: RowData?,onItemClickListener: RecentTransactionAccountsAdapter.OnItemClickListener) {
        currentItem?.let {
            binder.tvContactName.text = it.userName

            binder.clRoot.setOnClickListener {
                onItemClickListener.OnRecentItemClickListener(rowData = currentItem)
            }

            when (it.userImage) {
                null -> {
                    binder.ivProfileImage.visibility = View.GONE
                    binder.tvFirstLetter.visibility = View.VISIBLE
                    binder.tvFirstLetter.text = Utils.firstLetter(it.userName)

                }

                else -> {
                    binder.ivProfileImage.visibility = View.VISIBLE
                    binder.tvFirstLetter.visibility = View.GONE
                  //  setUserImage(it.userImage.toString(), holder.iv_profileImage)

                }
            }

            /**
             *  need to check wether image is there or not if image present
             *  holder.tv_firstLetter.visibility=View.GONE
             *  holder.iv_profileImage.visibility=View.VISIBLE
             *  ELSE
             *  holder.tv_firstLetter.visibility=View.VISIBLE
             *  holder.iv_profileImage.visibility=View.GONE
             */


        }
    }
}