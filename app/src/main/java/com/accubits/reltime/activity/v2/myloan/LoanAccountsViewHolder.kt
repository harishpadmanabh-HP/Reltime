package com.accubits.reltime.activity.v2.myloan

import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import com.accubits.reltime.R
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.GetLoanAccountSuccessModel
import com.accubits.reltime.utils.CustomEvent
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.model.ViewEvent
import smartadapter.viewevent.viewholder.CustomViewEventListenerHolder
import smartadapter.viewholder.SmartAdapterHolder
import smartadapter.viewholder.SmartViewHolder

open class LoanAccountsViewHolder(parentView: ViewGroup) :
    SmartViewHolder<GetLoanAccountSuccessModel.AccountModel>(parentView, R.layout.item_pay_loan_account),
    CustomViewEventListenerHolder,
    SmartAdapterHolder {
    override lateinit var customViewEventListener: (ViewEvent) -> Unit
    override var smartRecyclerAdapter: SmartRecyclerAdapter? = null
    // override lateinit var viewEventListner : OnViewEventListner
    init {
    }

    override fun bind(item: GetLoanAccountSuccessModel.AccountModel) {
        val tvAccountName = itemView.findViewById(R.id.tvAccountName) as AppCompatTextView
        val tvAccountBalance = itemView.findViewById(R.id.tvBalanceValue) as AppCompatTextView
        val ivImageView = itemView.findViewById(R.id.ivIcon) as AppCompatImageView
        val radio = itemView.findViewById(R.id.ivRadio) as AppCompatRadioButton
      //  val tvCoinType = itemView.findViewById(R.id.tvRTO) as AppCompatTextView

        if(item.name.equals("RTO Wallet")){
            ivImageView.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_rto_home))
        }else{
            ivImageView.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_add_joint))
        }

        radio.isChecked = item.isSelected
        tvAccountName.text=item.name
        tvAccountBalance.text="${Utils.coinCodeWithSymbol(tvAccountBalance.context,item.coinCode,item.symbol)} ${item.rto_balance}"
       // tvCoinType.text=
        radio.setOnClickListener {
            customViewEventListener.invoke(
                CustomEvent(smartRecyclerAdapter!!, this, adapterPosition, it)
            )
        }
        itemView.setOnClickListener {
            customViewEventListener.invoke(
                CustomEvent(smartRecyclerAdapter!!, this, adapterPosition, it)
            )
        }
    }
}