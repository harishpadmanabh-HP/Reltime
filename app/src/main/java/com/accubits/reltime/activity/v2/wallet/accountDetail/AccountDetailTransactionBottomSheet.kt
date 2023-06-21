package com.accubits.reltime.activity.v2.wallet.accountDetail

import android.content.Context
import android.view.View
import android.view.Window
import com.accubits.reltime.R
import com.accubits.reltime.databinding.BottomSheetTransactionHistoryBinding
import com.accubits.reltime.helpers.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog

class AccountDetailTransactionBottomSheet(
    context: Context, private val onFilter: (BottomSheetDialog) -> Unit,
    private val onClear: (BottomSheetDialog) -> Unit,private val isPaidRequired:Boolean=false
) : BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme) {
    private var binding: BottomSheetTransactionHistoryBinding =
        BottomSheetTransactionHistoryBinding.inflate(layoutInflater)

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        init()
        setListeners()
    }

    private fun init() {
        hidePaidFilter()
        if( Utils.account_transaction_status.isEmpty()){
            binding.checkReceived.isChecked = false
            binding.checkTransferred.isChecked = false
            binding.checkDeposit.isChecked = false
            binding.checkMoved.isChecked = false
            binding.checkBridge.isChecked = false
            binding.checkSwap.isChecked = false
            binding.checkPaid.isChecked = false
        } else if( Utils.account_transaction_status == "Received"){
            binding.checkReceived.isChecked = true
        } else if( Utils.account_transaction_status == "Transferred"){
            binding.checkTransferred.isChecked = true
        } else if( Utils.account_transaction_status == "Deposited"){
            binding.checkDeposit.isChecked = true
        } else if( Utils.account_transaction_status == "Moved"){
            binding.checkMoved.isChecked = true
        }else if( Utils.account_transaction_status == "Bridging"){
            binding.checkBridge.isChecked = true
        }else if( Utils.account_transaction_status == "Swap"){
            binding.checkSwap.isChecked = true
        }else if( Utils.account_transaction_status == "Paid"){
            binding.checkPaid.isChecked = true
        }

        when (Utils.account_date_type) {
            null -> binding.tvAllTime.isChecked = true
            1 -> binding.tvLastMonth.isChecked = true
            2 -> binding.tvLastThreeMonth.isChecked = true
            3 -> binding.tvLastSixMonth.isChecked = true
            4 -> binding.tvLastYear.isChecked = true
            5 -> binding.tvLastWeek.isChecked = true
        }
    }

    private fun hidePaidFilter(){
        binding.checkPaid.visibility= if (isPaidRequired) View.VISIBLE else View.GONE
    }

    private fun setListeners() {
        binding.ivClose.setOnClickListener { dismiss() }
        binding.checkReceived.setOnClickListener {
            binding.checkDeposit.isChecked = false
            binding.checkTransferred.isChecked = false
            binding.checkMoved.isChecked = false
            binding.checkBridge.isChecked = false
            binding.checkSwap.isChecked = false
            binding.checkPaid.isChecked = false
        }

        binding.checkTransferred.setOnClickListener {
            binding.checkDeposit.isChecked = false
            binding.checkReceived.isChecked = false
            binding.checkMoved.isChecked = false
            binding.checkBridge.isChecked = false
            binding.checkSwap.isChecked = false
            binding.checkPaid.isChecked = false
        }

        binding.checkDeposit.setOnClickListener {
            binding.checkReceived.isChecked = false
            binding.checkTransferred.isChecked = false
            binding.checkMoved.isChecked = false
            binding.checkBridge.isChecked = false
            binding.checkSwap.isChecked = false
            binding.checkPaid.isChecked = false
        }

        binding.checkMoved.setOnClickListener {
            binding.checkDeposit.isChecked = false
            binding.checkTransferred.isChecked = false
            binding.checkReceived.isChecked = false
            binding.checkBridge.isChecked = false
            binding.checkSwap.isChecked = false
            binding.checkPaid.isChecked = false
        }

        binding.checkBridge.setOnClickListener {
            binding.checkDeposit.isChecked = false
            binding.checkTransferred.isChecked = false
            binding.checkReceived.isChecked = false
            binding.checkMoved.isChecked = false
            binding.checkSwap.isChecked = false
            binding.checkPaid.isChecked = false
        }

        binding.checkSwap.setOnClickListener {
            binding.checkDeposit.isChecked = false
            binding.checkTransferred.isChecked = false
            binding.checkReceived.isChecked = false
            binding.checkMoved.isChecked = false
            binding.checkBridge.isChecked = false
            binding.checkPaid.isChecked = false
        }

        binding.checkPaid.setOnClickListener {
            binding.checkDeposit.isChecked = false
            binding.checkTransferred.isChecked = false
            binding.checkReceived.isChecked = false
            binding.checkMoved.isChecked = false
            binding.checkBridge.isChecked = false
            binding.checkSwap.isChecked = false
        }

        binding.tvClear.setOnClickListener {
            binding.checkReceived.isChecked = false
            binding.checkTransferred.isChecked = false
            binding.checkDeposit.isChecked = false
            binding.checkMoved.isChecked = false
            binding.checkBridge.isChecked = false
            binding.checkSwap.isChecked = false
            binding.checkPaid.isChecked = false

            binding.tvLastWeek.isChecked = false
            binding.tvLastMonth.isChecked = false
            binding.tvLastThreeMonth.isChecked = false
            binding.tvLastSixMonth.isChecked = false
            binding.tvLastYear.isChecked = false

            binding.tvAllTime.isChecked = true

            Utils.account_transaction_status = ""
            Utils.account_date_type = null

        }

        binding.tvLastWeek.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.tvLastMonth.isChecked = false
                binding.tvLastThreeMonth.isChecked = false
                binding.tvLastSixMonth.isChecked = false
                binding.tvLastYear.isChecked = false
                binding.tvAllTime.isChecked = false
            }
        }

        binding.tvLastMonth.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.tvLastWeek.isChecked = false
                binding.tvLastThreeMonth.isChecked = false
                binding.tvLastSixMonth.isChecked = false
                binding.tvLastYear.isChecked = false
                binding.tvAllTime.isChecked = false
            }
        }

        binding.tvLastThreeMonth.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.tvLastWeek.isChecked = false
                binding.tvLastMonth.isChecked = false
                binding.tvLastSixMonth.isChecked = false
                binding.tvLastYear.isChecked = false
                binding.tvAllTime.isChecked = false
            }
        }

        binding.tvLastSixMonth.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.tvLastWeek.isChecked = false
                binding.tvLastMonth.isChecked = false
                binding.tvLastThreeMonth.isChecked = false
                binding.tvLastYear.isChecked = false
                binding.tvAllTime.isChecked = false
            }
        }

        binding.tvLastYear.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.tvLastWeek.isChecked = false
                binding.tvLastMonth.isChecked = false
                binding.tvLastThreeMonth.isChecked = false
                binding.tvLastSixMonth.isChecked = false
                binding.tvAllTime.isChecked = false
            }
        }

        binding.tvAllTime.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.tvLastWeek.isChecked = false
                binding.tvLastMonth.isChecked = false
                binding.tvLastThreeMonth.isChecked = false
                binding.tvLastSixMonth.isChecked = false
                binding.tvLastYear.isChecked = false
            }
        }

        binding.btnApply.setOnClickListener {
            if (binding.checkReceived.isChecked)
                Utils.account_transaction_status = "Received"
            else if (binding.checkTransferred.isChecked)
                Utils.account_transaction_status = "Transferred"
            else if (binding.checkDeposit.isChecked)
                Utils.account_transaction_status = "Deposited"
            else if (binding.checkMoved.isChecked)
                Utils.account_transaction_status = "Moved"
            else if (binding.checkBridge.isChecked)
                Utils.account_transaction_status = "Bridging"
            else if (binding.checkSwap.isChecked)
                Utils.account_transaction_status = "Swap"
            else if (binding.checkPaid.isChecked)
                Utils.account_transaction_status = "Paid"
            else Utils.account_transaction_status = ""

            if (binding.tvLastMonth.isChecked)
                Utils.account_date_type = 1
            else if (binding.tvLastThreeMonth.isChecked)
                Utils.account_date_type = 2
            else if (binding.tvLastSixMonth.isChecked)
                Utils.account_date_type = 3
            else if (binding.tvLastYear.isChecked)
                Utils.account_date_type = 4
            else if (binding.tvLastWeek.isChecked)
                Utils.account_date_type = 5
            else if (binding.tvAllTime.isChecked)
                Utils.account_date_type = null

            dismiss()
            onFilter.invoke(this)
        }
    }

}