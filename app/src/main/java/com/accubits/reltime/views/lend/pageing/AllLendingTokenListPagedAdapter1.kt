package com.accubits.reltime.views.lend.pageing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.RowAllLending


object ContactsDiffUtilsAll1 : DiffUtil.ItemCallback<RowAllLending>() {
    override fun areItemsTheSame(oldItem: RowAllLending, newItem: RowAllLending): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: RowAllLending,
        newItem: RowAllLending
    ): Boolean {
        return false
    }

}

class AllLendingTokenListPagedAdapter1(
) : PagingDataAdapter<RowAllLending, AllLendingTokenListPagedAdapter1.ViewHolder>(
    ContactsDiffUtilsAll1
) {

    private var onItemClickListener: ((RowAllLending) -> Unit)? = null
    private var onBorrowBtnClickListener: ((RowAllLending) -> Unit)? = null
    private var onAcceptBtnClickListener: ((RowAllLending) -> Unit)? = null
    private var onRejectBtnClickListener: ((RowAllLending) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIntrest: TextView = view.findViewById(R.id.tv_interest)
        val tvLoanTerm: TextView = view.findViewById(R.id.tv_loanTerm)
        val container: ConstraintLayout = view.findViewById(R.id.container)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
        val tvRto: TextView = view.findViewById(R.id.tv_rto)
        val tvRto1: TextView = view.findViewById(R.id.tv_rto1)
        val btnBorrow: Button = view.findViewById(R.id.btn_borrow)
        val tvPayBack: TextView = view.findViewById(R.id.tv_paybackPeriod)
        val tvRtoHeader: TextView = view.findViewById(R.id.tv_rtoLable)
        val tvLoanType: TextView = view.findViewById(R.id.tv_loan_type)
        val btnAccept: Button = view.findViewById(R.id.btn_accept)
        val btnReject: Button = view.findViewById(R.id.btn_reject)
        val tvCollatral: TextView = view.findViewById(R.id.tv_collatral)

    }

    fun setOnItemClickListener(listener: (RowAllLending) -> Unit) {
        onItemClickListener = listener
    }

    fun clickOnBorrowbtn(listener: (RowAllLending) -> Unit) {
        onBorrowBtnClickListener = listener
    }

    fun clickOnAcceptbtn(listener: (RowAllLending) -> Unit) {
        onAcceptBtnClickListener = listener
    }

    fun clickOnRejectbtn(listener: (RowAllLending) -> Unit) {
        onRejectBtnClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        if (position == itemCount - 1) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 100
            holder.itemView.layoutParams = params
        }
        val currentItem = getItem(position)
        currentItem?.let {
            val data = it
            when (data.lendingTypes) {
                "listing" -> {
                    holder.tvLoanType.text = context.resources.getText(R.string.normal)
                    holder.btnAccept.visibility = View.GONE
                    holder.btnReject.visibility = View.GONE
                    holder.btnBorrow.visibility = View.VISIBLE
                }
                "directLending" -> {
                    holder.tvLoanType.text = context.resources.getText(R.string.direct)
                    holder.btnAccept.visibility = View.VISIBLE
                    holder.btnReject.visibility = View.VISIBLE
                    holder.btnBorrow.visibility = View.INVISIBLE
                }
            }
            holder.tvCollatral.text = Utils.getCollateralValue(
                holder.tvCollatral.context,
                it.collateral,
                null,
                null
            )//it.collateral_amount,it.coinCode)
            holder.tvIntrest.text = it.interest_rate + "%"
            holder.tvLoanTerm.text =Utils.setFormattedMonth(holder.tvLoanTerm.context,it.installments)
            holder.tvPayBack.text = holder.tvPayBack.context.getString(R.string.every_month)

            holder.tvStatus.text = it.published_by
            val splitDecimalValue = it.rto_amount.split(".")
            holder.tvRtoHeader.text =  Utils.coinCodeWithSymbol(holder.tvRtoHeader.context,it.coinCode,it.symbol)
            holder.tvRto.text = splitDecimalValue[0]
            holder.tvRto1.text = "." + splitDecimalValue[1]

//            holder.container.setOnClickListener {
//                onItemClickListener?.let { it1 -> it1(data) }
//            }
            holder.btnBorrow.setOnClickListener {

                onBorrowBtnClickListener?.let { it1 -> it1(data) }
            }
            holder.btnAccept.setOnClickListener {

                onAcceptBtnClickListener?.let { it1 -> it1(data) }
            }
            holder.btnReject.setOnClickListener {

                onRejectBtnClickListener?.let { it1 -> it1(data) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.borrow_item1, parent, false)
        )
    }
}