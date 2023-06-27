package com.accubits.reltime.activity.v2.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.TransactionItem
import com.accubits.reltime.utils.convertRTOtoEURO
import com.accubits.reltime.utils.convertReltimeToNagra

object ContactsDiffUtilBorrows : DiffUtil.ItemCallback<TransactionItem>() {
    override fun areItemsTheSame(
        oldItem: TransactionItem,
        newItem: TransactionItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: TransactionItem,
        newItem: TransactionItem
    ): Boolean {
        return false
    }

}

class TransactionPagedV2Adapter(
    private var context: Context
) : PagingDataAdapter<TransactionItem, TransactionPagedV2Adapter.ViewHolder>(
    ContactsDiffUtilBorrows
) {

    private var onItemClickListener: ((TransactionItem) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTransactionType: TextView = view.findViewById(R.id.tvTransactionType)
        val imgTransaction: ImageView = view.findViewById(R.id.imgTransaction)
        val container: ConstraintLayout = view.findViewById(R.id.container)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvRto: TextView = view.findViewById(R.id.tvRto)
        val tvPending: TextView = view.findViewById(R.id.tvPending)

        fun setData(transactionItem: TransactionItem) {
            val context = tvTransactionType.context
            tvTransactionType.text = transactionItem.type
            tvDate.text = Utils.getDateCurrentTimeZone1(transactionItem.timestamp.toDouble())
            if(!transactionItem.status.isNullOrBlank()) {
                tvPending.visibility=View.VISIBLE
                tvPending.text=transactionItem.status//context.resources.getString(R.string.c_n_c,context.resources.getString(R.string.pending) )
            }
            else tvPending.visibility=View.GONE


            if (transactionItem.type == "Received") {
                tvRto.text = transactionItem.receiver.let {
                    context.resources.getString(
                        R.string.n_n, Utils.coinCodeWithSymbol(
                            context,
                            it?.coinCode?:"", it?.symbol
                        ), it?.amount
                    )
                }
                tvName.text = transactionItem.sender?.let {
                    context.resources.getString(R.string.from_n, it.name).convertRTOtoEURO().convertReltimeToNagra()
                }
            } else {
                tvRto.text = transactionItem.sender?.let {
                    context.resources.getString(
                        R.string.n_n, Utils.coinCodeWithSymbol(
                            context,
                            it.coinCode, it.symbol
                        ), it.amount
                    )
                }
                tvName.text = transactionItem.receiver?.let {
                    context.resources.getString(R.string.to_n, it.name).convertRTOtoEURO().convertReltimeToNagra()
                }
            }
            imgTransaction.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    when (transactionItem.type) {
                        "Deposited" -> R.drawable.ic_deposit_wallet
                        "Transferred" -> R.drawable.ic_transfer
                        "Received" -> R.drawable.ic_received_v2
                        "Sent" -> R.drawable.ic_transfer
                        "Swap" -> R.drawable.ic_swapping
                        "Bridging" -> R.drawable.ic_bridging
                        "Moved" -> R.drawable.ic_move_v2
                        "Paid" -> R.drawable.ic_transfer
                        else -> R.drawable.ic_received_v2
                    }
                )
            )

        }
    }

    /*
           ("Deposited", "Deposited"),
           ("Transferred", "Transferred"),
           ("Received", "Received"),
           ("Sent", "Sent"),
           ("Swap", "Swap"),
           ("Bridging", "Bridging"),
           ("Moved", "Moved")*/
    fun setOnItemClickListener(listener: (TransactionItem) -> Unit) {
        onItemClickListener = listener
    }


    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == itemCount - 1) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 0
            holder.itemView.layoutParams = params
        }
        val currentItem = getItem(position)
        currentItem?.let {
            holder.setData(it)
            holder.container.setOnClickListener {
                onItemClickListener?.let { it1 -> it1(currentItem) }
            }
            /*
                       holder.tvDate.text = it.timestamp.let { it1 -> Utils.getDateCurrentTimeZone1(it1.toDouble(), "MMM dd, yyyy") }
                       holder.tvTransactionType.text = it.type
                     when (it.type) {
                           "Deposit" -> {
           //                    if (it.checkoutId != null && it.status.uppercase() == "PENDING") {
           //                        holder.ivPending.visibility = View.VISIBLE
           //                    } else holder.ivPending.visibility = View.GONE
                               holder.imgTransaction.setImageDrawable(context.getDrawable(R.drawable.ic_deposit))
                               if (it.sender?.name.isNullOrEmpty()) {
                                   holder.tvName.text = "--"
                               } else {
                                   holder.tvName.text = it.sender!!.name
                               }
                           }
                           "Sent" -> {
                               holder.ivPending.visibility = View.GONE
                               holder.imgTransaction.setImageDrawable(context.getDrawable(R.drawable.ic_up))
                               if (it.receiver?.name.isNullOrEmpty()) {
                                   holder.tvName.text = "--"
                               } else {
                                   holder.tvName.text = it.receiver?.name
                               }
                           }
                           "Received" -> {
                               holder.ivPending.visibility = View.GONE
                               holder.imgTransaction.setImageDrawable(context.getDrawable(R.drawable.ic_down))
                               if (it.sender?.name.isNullOrEmpty()) {
                                   holder.tvName.text = "--"
                               } else {
                                   holder.tvName.text = it.sender!!.name
                               }
                           }
                           "Withdraw" -> {
                               holder.ivPending.visibility = View.GONE
                               holder.imgTransaction.setImageDrawable(context.getDrawable(R.drawable.ic_withdraw))
                               if (it.sender?.name.isNullOrEmpty()) {
                                   holder.tvName.text = "--"
                               } else {
                                   holder.tvName.text = it.sender!!.name
                               }
                           }
                           "Swap" -> {
                               holder.ivPending.visibility = View.GONE
                               holder.imgTransaction.setImageDrawable(context.getDrawable(R.drawable.ic_swap_transaction))
                               if (it.sender?.name.isNullOrEmpty()) {
                                   holder.tvName.text = "--"
                               } else {
                                   holder.tvName.text = it.sender!!.name
                               }
                           }
                           else -> {
                               holder.ivPending.visibility = View.GONE
                               holder.imgTransaction.setImageDrawable(context.getDrawable(R.drawable.ic_swap_transaction))
                               if (it.sender?.name.isNullOrEmpty()) {
                                   holder.tvName.text = "--"
                               } else {
                                   holder.tvName.text = it.sender!!.name
                               }
                           }
                       }
            holder.tvRto.text = "${it.sender!!.amount}  ${it.sender.coinCode}"
             */
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_transaction_history, parent, false)
        )
    }
}