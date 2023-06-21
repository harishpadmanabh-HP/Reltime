package com.accubits.reltime.activity.v2.myloan

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.accubits.reltime.R
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.RowMyBorrowings
import com.accubits.reltime.utils.CustomEvent
import com.accubits.reltime.utils.twoDecimalPointFormat
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.model.ViewEvent
import smartadapter.viewevent.viewholder.CustomViewEventListenerHolder
import smartadapter.viewholder.SmartAdapterHolder
import smartadapter.viewholder.SmartViewHolder
import kotlin.math.roundToInt

open class LoanViewHolder(parentView: ViewGroup) :
    SmartViewHolder<RowMyBorrowings>(parentView, R.layout.item_loan),
    CustomViewEventListenerHolder,
    SmartAdapterHolder {

    override lateinit var customViewEventListener: (ViewEvent) -> Unit
    override var smartRecyclerAdapter: SmartRecyclerAdapter? = null

    override fun bind(item: RowMyBorrowings) {
        val context=itemView.context
        val tvBorrowingIdValue: TextView = itemView.findViewById(R.id.tvBorrowingIdValue)
        val tvContractDateValue: TextView = itemView.findViewById(R.id.tvContractDateValue)
        val tvBorrowedAmountValue: TextView = itemView.findViewById(R.id.tvBorrowedAmountValue)
        val tvMonthlyInstallmentsValue: TextView =
            itemView.findViewById(R.id.tvMonthlyInstallmentsValue)
        val tvOutstandingBalanceValue: TextView =
            itemView.findViewById(R.id.tvOutstandingBalanceValue)
        val tvInterestRateValue: TextView = itemView.findViewById(R.id.tvInterestRateValue)
        val tvLoanTermValue: TextView = itemView.findViewById(R.id.tvLoanTermValue)
        val tvCollateralValue: TextView = itemView.findViewById(R.id.tvCollateralValue)
        val btnPayNow: AppCompatButton = itemView.findViewById(R.id.btnPayNow)
        val btnCloseNow: TextView = itemView.findViewById(R.id.btnPaymentHistory)
        val clickHere: TextView = itemView.findViewById(R.id.tvClickHere)

        tvContractDateValue.text =
            item.timestamp.let { Utils.getDateCurrentTimeZone1(it.toDouble()) }// item.created_at
        tvBorrowingIdValue.text = "#${item.id}"//"#${item.lending}"

        tvBorrowedAmountValue.text =
            context.resources.getString(
                R.string.n_n, Utils.coinCodeWithSymbol(
                    context,
                    item.coinCode,
                    item.symbol
                ), item.rto_amount
            )


        item.instalment_amount?.let {
            tvMonthlyInstallmentsValue.text =
                context.resources.getString(
                    R.string.n_n, Utils.coinCodeWithSymbol(
                        context,
                        item.coinCode,
                        item.symbol
                    ), it
                )
        }



        tvOutstandingBalanceValue.text =
            context.resources.getString(
                R.string.n_n, Utils.coinCodeWithSymbol(
                    context,
                    item.coinCode,
                    item.symbol
                ), item.pending_amount
            )
        tvInterestRateValue.text =
            "${item.interest_rate.toDouble()}%"
        tvLoanTermValue.text = "${item.instalments} months"
        tvCollateralValue.text = Utils.getCollateralValue(
            tvCollateralValue.context, item.collateral, item.collateral_amount,
            Utils.coinCodeWithSymbol(tvCollateralValue.context, item.coinCode, item.symbol)
        )

        if (item.status == "Active") {
            btnCloseNow.visibility = View.VISIBLE
            btnPayNow.visibility = View.VISIBLE
        } else {
            btnCloseNow.visibility = View.GONE
            btnPayNow.visibility = View.GONE
        }

        btnPayNow.setOnClickListener {
            customViewEventListener.invoke(
                CustomEvent(smartRecyclerAdapter!!, this, adapterPosition, it)
            )
        }
        btnCloseNow.setOnClickListener {
            customViewEventListener.invoke(
                CustomEvent(smartRecyclerAdapter!!, this, adapterPosition, it)
            )
        }
        clickHere.setOnClickListener {
            customViewEventListener.invoke(
                CustomEvent(smartRecyclerAdapter!!, this, adapterPosition, it)
            )
        }

    }
}