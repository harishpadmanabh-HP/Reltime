package com.accubits.reltime.activity.v2.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.lending.DirectLendingV2Activity
import com.accubits.reltime.activity.v2.transfer.TransferActivity
import com.accubits.reltime.activity.v2.wallet.bridge.BridgeActivity
import com.accubits.reltime.activity.v2.wallet.move.MoveComposeActivity
import com.accubits.reltime.activity.v2.wallet.mywallet.WalletDetailV2Activity
import com.accubits.reltime.activity.v2.wallet.swap.SwapActivity
import com.accubits.reltime.databinding.RvItemHomeControlsBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.lend.LendSuccessActivity
import com.accubits.reltime.views.rto.BuyRTOActivity

class HomeControlsAdapter constructor(
    private val preferenceManager: PreferenceManager, private val isForHomePage: Boolean = true
) :
    RecyclerView.Adapter<HomeControlsAdapter.ControlsCViewHolder>() {


    val list =
        arrayListOf(
            ControlsItem(id = 0, icon = R.drawable.ic_transfer, text = R.string.transfer),
            if (isForHomePage) ControlsItem(
                id = 6,
                icon = R.drawable.ic_loan,
                text = R.string.loans
            ) else ControlsItem(id = 1, icon = R.drawable.ic_receive, text = R.string.receive),
            ControlsItem(id = 2, icon = R.drawable.ic_deposit_wallet, text = R.string.deposit),
            ControlsItem(id = 3, icon = R.drawable.ic_move, text = R.string.move),
            ControlsItem(id = 4, icon = R.drawable.ic_bridging, text = R.string.bridge),
            ControlsItem(id = 5, icon = R.drawable.ic_swapping, text = R.string.swap),
        )


    override fun onBindViewHolder(
        holder: HomeControlsAdapter.ControlsCViewHolder,
        position: Int
    ) {
        holder.setData(list[position])
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeControlsAdapter.ControlsCViewHolder {
        return ControlsCViewHolder(
            RvItemHomeControlsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    inner class ControlsCViewHolder(private val binder: RvItemHomeControlsBinding) :
        RecyclerView.ViewHolder(binder.root) {
        fun setData(item: ControlsItem) {
            val context = binder.root.context
            binder.tvLabel.text = context.getString(item.text)
            binder.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, item.icon))
            binder.root.setOnClickListener {
                onItemClicked(context, item.id)
            }
        }
    }

    private fun onItemClicked(context: Context, id: Int) {
        if (!preferenceManager.isKycApproved())
            context.showToast(context.resources.getString(R.string.kyc_not_available_error))
        else if (!preferenceManager.getMomic())
            context.showToast(context.resources.getString(R.string.wallet_not_available_error))
        else
            when (id) {
                0 -> context.openActivity(TransferActivity::class.java)
                1 -> context.openActivity(WalletDetailV2Activity::class.java){
                    this.putString(WalletDetailV2Activity.TITLE,context.resources.getString(R.string.receive))
                }
                2 -> context.openActivity(BuyRTOActivity::class.java)
                3 -> context.openActivity(MoveComposeActivity::class.java)
                4 -> context.openActivity(BridgeActivity::class.java)
                5 -> context.openActivity(SwapActivity::class.java)
                6 -> context.openActivity(DirectLendingV2Activity::class.java, extras = {
                    this.putString( LendSuccessActivity.DONE_BUTTON_LABEL,context.resources.getString(R.string.go_to_home))
                })
            }
    }

    data class ControlsItem(val id: Int, val icon: Int, val text: Int)
}

