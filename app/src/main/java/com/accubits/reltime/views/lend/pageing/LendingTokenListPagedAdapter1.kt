package com.accubits.reltime.views.lend.pageing

import android.content.Intent
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
import com.accubits.reltime.activity.v2.lending.DirectLendingV2Activity
import com.accubits.reltime.activity.v2.loan.LendingDetailActivity1
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.Row


object ContactsDiffUtils : DiffUtil.ItemCallback<Row>() {
    override fun areItemsTheSame(oldItem: Row, newItem: Row): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Row,
        newItem: Row
    ): Boolean {
        return false
    }

}

class LendingTokenListPagedAdapter1(
) : PagingDataAdapter<Row, LendingTokenListPagedAdapter1.ViewHolder>(ContactsDiffUtils) {

    private var onItemClickListener: ((Row) -> Unit)? = null
    private var onRemoveBtnClickListener: ((Row) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLoanType: TextView = view.findViewById(R.id.tvLoanType)
        val tvIntrest: TextView = view.findViewById(R.id.tv_interest)
        val tvLoanTerm: TextView = view.findViewById(R.id.tv_loanTerm)
        val tvCollatral: TextView = view.findViewById(R.id.tv_collatral)
        val tvExpectedReturn: TextView = view.findViewById(R.id.tv_expactedReturn)
        val container: ConstraintLayout = view.findViewById(R.id.container)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
        val tvRto: TextView = view.findViewById(R.id.tv_rto)
        val tvRto1: TextView = view.findViewById(R.id.tv_rto1)
        val btnModify: Button = view.findViewById(R.id.btn_modify)
        val btnRemove: Button = view.findViewById(R.id.btn_remove)
        val tvRtolabel: TextView = view.findViewById(R.id.tv_rtoLable)
    }

    fun setOnItemClickListener(listener: (Row) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == itemCount - 1) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 100
            holder.itemView.layoutParams = params
        }
        val context = holder.itemView.context
        val currentItem = getItem(position)
        currentItem?.let {

            val data = it
//            holder.tvDate.text = it.date + ", " + it.time
            holder.tvStatus.text = it.status
            holder.tvCollatral.text = Utils.getCollateralValue(
                holder.tvCollatral.context, it.collateral, null, null
            )//it.collateral_amount,it.coinCode)
            holder.tvExpectedReturn.text =
                context.resources.getString(R.string.n_n,  Utils.coinCodeWithSymbol(holder.tvExpectedReturn.context,it.coinCode,it.symbol), it.expectedReturn.toString())

            holder.tvIntrest.text = it.interest_rate + "%"
            holder.tvLoanTerm.text =
                com.accubits.reltime.helpers.Utils.setFormattedMonth(
                    holder.tvLoanTerm.context,
                    it.installments
                )

            val rtoAmount = it.rto_amount.split(".")
            holder.tvRtolabel.text = Utils.coinCodeWithSymbol(holder.tvRtolabel.context,it.coinCode,it.symbol)
            holder.tvRto.text = " " + rtoAmount[0]
            holder.tvRto1.text = "." + rtoAmount[1]
            holder.tvLoanType.text =
                if (it.lendingTypes == "listing") context.resources.getText(R.string.normal) else context.resources.getText(
                    R.string.direct
                )

            when (it.status) {
                "0" -> {
                    holder.tvStatus.text = context.resources.getText(R.string.open)
                    holder.tvStatus.setTextColor(
                        context.resources.getColorStateList(
                            R.color.lend_status0,
                            null
                        )
                    )
                    holder.tvStatus.backgroundTintList =
                        context.resources.getColorStateList(R.color.bg_lend_status0, null)
                    holder.btnModify.text = context.resources.getText(R.string.modify)
                    holder.btnRemove.visibility = View.VISIBLE
                }
                "1" -> {
                    holder.tvStatus.text = context.resources.getText(R.string.pending)
                    holder.tvStatus.setTextColor(
                        context.resources.getColorStateList(
                            R.color.lend_status1,
                            null
                        )
                    )
                    holder.tvStatus.backgroundTintList =
                        context.resources.getColorStateList(R.color.bg_lend_status1, null)
                    holder.btnModify.text = context.resources.getText(R.string.see_detail)
                    holder.btnModify.visibility = View.GONE
                    holder.btnRemove.visibility = View.GONE
                }
                "2" -> {
                    holder.tvStatus.text = context.resources.getText(R.string.ongoing)
                    holder.tvStatus.setTextColor(
                        context.resources.getColorStateList(
                            R.color.lend_status2,
                            null
                        )
                    )
                    holder.tvStatus.backgroundTintList =
                        context.resources.getColorStateList(R.color.bg_lend_status2, null)
                    holder.btnModify.text = context.resources.getText(R.string.see_detail)
                    holder.btnRemove.visibility = View.GONE
                }
                "3" -> {
                    holder.tvStatus.text = context.resources.getText(R.string.closed)
                    holder.tvStatus.setTextColor(
                        context.resources.getColorStateList(
                            R.color.lend_status3,
                            null
                        )
                    )
                    holder.tvStatus.backgroundTintList =
                        context.resources.getColorStateList(R.color.bg_lend_status3, null)
                    holder.btnModify.text = context.resources.getText(R.string.see_detail)
                    holder.btnRemove.visibility = View.GONE
                }
                "4" -> {
                    holder.tvStatus.text = context.resources.getText(R.string.rejected)
                    holder.tvStatus.setTextColor(
                        context.resources.getColorStateList(
                            R.color.lend_status4,
                            null
                        )
                    )
                    holder.tvStatus.backgroundTintList =
                        context.resources.getColorStateList(R.color.bg_lend_status4, null)
                    holder.btnModify.visibility = View.GONE
                    holder.btnRemove.visibility = View.GONE
                }
            }
            holder.btnModify.setOnClickListener {
                if (holder.btnModify.text.equals(context.resources.getText(R.string.modify))) {
                    val intent = Intent(context, DirectLendingV2Activity::class.java)
                    intent.putExtra(DirectLendingV2Activity.EXTRA_LENDING_ITEM, currentItem)

                    intent.putExtra(ReltimeConstants.AMOUNT, currentItem.rto_amount)
                    intent.putExtra(
                        ReltimeConstants.INSTALLMENT,
                        currentItem.installments.toString()
                    )
                    intent.putExtra(ReltimeConstants.DATE, currentItem.date)
                    intent.putExtra(ReltimeConstants.TIME, currentItem.time)
                    intent.putExtra(ReltimeConstants.COLLATRAL, currentItem.collateral)
                    intent.putExtra(ReltimeConstants.STATE, currentItem.status)
                    intent.putExtra(ReltimeConstants.TransactionId, currentItem.id)
                    intent.putExtra(ReltimeConstants.INTEREST, currentItem.interest_rate)
                    context.startActivity(intent)
                } else {

                    val intent = Intent(context, LendingDetailActivity1::class.java)
                    intent.putExtra(ReltimeConstants.AMOUNT, currentItem.rto_amount)
                    intent.putExtra(
                        ReltimeConstants.INSTALLMENT,
                        currentItem.installments
                    )
                    intent.putExtra(ReltimeConstants.DATE, currentItem.date)
                    intent.putExtra(ReltimeConstants.TIME, currentItem.time)
                    intent.putExtra(ReltimeConstants.COLLATRAL, currentItem.collateral)
                    intent.putExtra(ReltimeConstants.STATE, currentItem.status)
                    intent.putExtra(ReltimeConstants.TransactionId, currentItem.id.toString())
                    intent.putExtra(ReltimeConstants.INTEREST, currentItem.interest_rate)
                    context.startActivity(intent)

                }
            }
            holder.btnRemove.setOnClickListener { _ ->
                if (it.status == "0") {
                    onRemoveBtnClickListener?.let { it1 -> it1(data) }
                }
            }
            holder.container.setOnClickListener {
                onItemClickListener?.let { it1 -> it1(data) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.lend_list_item1, parent, false)
        )
    }

    fun clickOnRemoveBtn(listener: (Row) -> Unit) {
        onRemoveBtnClickListener = listener
    }


}