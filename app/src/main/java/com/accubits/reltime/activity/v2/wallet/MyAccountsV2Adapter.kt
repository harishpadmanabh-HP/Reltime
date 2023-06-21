package com.accubits.reltime.activity.v2.wallet

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.wallet.home.model.AccountItem
import com.accubits.reltime.databinding.RowMyAccountBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.convertRTOtoEURO


class MyAccountsV2Adapter(private val onItemClicked: (AccountItem) -> Unit) :
    RecyclerView.Adapter<MyAccountsV2Adapter.MyAccountDataHolder>() {
    val list = arrayListOf<AccountItem>()

    fun setItems(accounts: List<AccountItem?>) {
        list.clear()
        accounts.forEach {
            it?.let { account -> list.add(account) }
        }
        notifyItemRangeChanged(0, list.size)
    }

    override fun onBindViewHolder(
        holder: MyAccountDataHolder,
        position: Int
    ) {
        holder.setData(list[position], position,onItemClicked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAccountDataHolder {
        val binder = RowMyAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyAccountDataHolder(binder)
    }

    override fun getItemCount() = list.size

    inner class MyAccountDataHolder(private val binder: RowMyAccountBinding) :
        RecyclerView.ViewHolder(binder.root) {
        fun setData(item: AccountItem, position: Int,onItemClicked: (AccountItem) -> Unit) {
            val context = binder.root.context

            binder.viewDivider.visibility =
                if (position == list.size - 1)
                    View.GONE
                else View.VISIBLE
            if (item.members != null) {
                createStyledAccountName(
                    binder.tvAccountName,
                    item.name,
                    context.resources.getString(
                        R.string.c_n_c,
                        Utils.getMembers(context, item.members.toInt())
                    )
                )
            } else {
                binder.tvAccountName.text = item.name.convertRTOtoEURO()
            }

            createStyledAmount(
                binder.tvRTOValue, Utils.coinCodeWithSymbol(
                    context = context,
                    coinCode = item.coinCode, symbol = item.symbol
                ), item.balance
            )
            item.statistics?.let {
                binder.tvChangePercentage.text =
                    context.resources.getString(R.string.n_percentage, it)
                when {
                    it.toDouble() > 0.0 -> {
                        binder.tvChangePercentage.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_graph_green
                            ), null, null, null
                        );
                        binder.tvChangePercentage.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.graph_bullish_green
                            )
                        )
                    }
                    it.toDouble() < 0.0 -> {
                        binder.tvChangePercentage.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_graph_red
                            ), null, null, null
                        )
                        binder.tvChangePercentage.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.graph_bearish_red
                            )
                        )
                    }
                }
            }
            binder.root.setOnClickListener { onItemClicked(item) }
        }

    }


    private fun createStyledAccountName(tv: TextView, val1: String, val2: String) {
        val builder = SpannableStringBuilder()
        builder.append(
            val1.convertRTOtoEURO()
        )
        builder.append(" ")
        builder.append(
            buildStyledSpannable(
                val2.convertRTOtoEURO(), size = .8f, color = ContextCompat.getColor(tv.context,R.color.white40)
            )
        )
        tv.setText(builder, TextView.BufferType.SPANNABLE)
    }


    private fun createStyledAmount(tv: TextView, val1: String, val2: String) {
        val builder = SpannableStringBuilder()
        builder.append(
            buildStyledSpannable(
                val1.convertRTOtoEURO(), size = .9f
            )
        )
        builder.append(" ")
        builder.append(val2.convertRTOtoEURO())
        tv.setText(builder, TextView.BufferType.SPANNABLE)
    }

    private fun buildStyledSpannable(
        string: String,
        size: Float? = null,
        color: Int? = null
    ): SpannableString {
        val str = SpannableString(string)
        color?.let {
            str.setSpan(
                ForegroundColorSpan(it),
                0,
                str.length,
                0
            )
        }
        size?.let {
            str.setSpan(
                RelativeSizeSpan(it),
                0,
                str.length,
                0
            )
        }
        return str
    }
}

