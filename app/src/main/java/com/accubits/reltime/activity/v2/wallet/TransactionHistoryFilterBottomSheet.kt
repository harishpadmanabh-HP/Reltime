package com.accubits.reltime.activity.v2.wallet

import android.content.Context
import android.view.Window
import android.widget.CompoundButton
import android.widget.Toast
import com.accubits.reltime.R
import com.accubits.reltime.databinding.BottomSheetTransactionHistoryBinding
import com.accubits.reltime.helpers.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog


class TransactionHistoryFilterBottomSheet(
    context: Context, private val onFilter: (BottomSheetDialog) -> Unit,
    private val onClear: (BottomSheetDialog) -> Unit
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
        if( Utils.transaction_status.isNullOrEmpty()){
            binding.checkReceived.isChecked = false
            binding.checkTransferred.isChecked = false
            binding.checkDeposit.isChecked = false
            binding.checkMoved.isChecked = false
        } else if( Utils.transaction_status == "Received"){
            binding.checkReceived.isChecked = true
        } else if( Utils.transaction_status == "Transferred"){
            binding.checkTransferred.isChecked = true
        } else if( Utils.transaction_status == "Deposited"){
            binding.checkDeposit.isChecked = true
        } else if( Utils.transaction_status == "Moved"){
            binding.checkMoved.isChecked = true
        }

        when (Utils.date_type) {
            null -> binding.tvAllTime.isChecked = true
            1 -> binding.tvLastMonth.isChecked = true
            2 -> binding.tvLastThreeMonth.isChecked = true
            3 -> binding.tvLastSixMonth.isChecked = true
            4 -> binding.tvLastYear.isChecked = true
        }
    }

    private fun setListeners() {
        binding.ivClose.setOnClickListener { dismiss() }
        binding.checkReceived.setOnClickListener {
            if (binding.checkReceived.isChecked) {
                binding.checkDeposit.isChecked = false
                binding.checkTransferred.isChecked = false
                binding.checkMoved.isChecked = false
            } else {
                binding.checkDeposit.isChecked = false
                binding.checkTransferred.isChecked = false
                binding.checkMoved.isChecked = false
            }
        }

        binding.checkTransferred.setOnClickListener {
            if (binding.checkTransferred.isChecked) {
                binding.checkDeposit.isChecked = false
                binding.checkReceived.isChecked = false
                binding.checkMoved.isChecked = false
            } else {
                binding.checkDeposit.isChecked = false
                binding.checkReceived.isChecked = false
                binding.checkMoved.isChecked = false
            }
        }

        binding.checkDeposit.setOnClickListener {
            if (binding.checkDeposit.isChecked) {
                binding.checkReceived.isChecked = false
                binding.checkTransferred.isChecked = false
                binding.checkMoved.isChecked = false
            } else {
                binding.checkReceived.isChecked = false
                binding.checkTransferred.isChecked = false
                binding.checkMoved.isChecked = false
            }
        }

        binding.checkMoved.setOnClickListener {
            if (binding.checkMoved.isChecked) {
                binding.checkDeposit.isChecked = false
                binding.checkTransferred.isChecked = false
                binding.checkReceived.isChecked = false
            } else {
                binding.checkDeposit.isChecked = false
                binding.checkTransferred.isChecked = false
                binding.checkReceived.isChecked = false
            }
        }

//        binding.checkReceived.setOnCheckedChangeListener { _, b ->
//            if (b) {
//                binding.checkDeposit.isChecked = false
//                binding.checkTransferred.isChecked = false
//                binding.checkMoved.isChecked = false
//            } else {
//                binding.checkDeposit.isChecked = false
//                binding.checkTransferred.isChecked = false
//                binding.checkMoved.isChecked = false
//            }
//        }
//
//        binding.checkTransferred.setOnCheckedChangeListener { _, b ->
//            if (b) {
//                binding.checkDeposit.isChecked = false
//                binding.checkReceived.isChecked = false
//                binding.checkMoved.isChecked = false
//            } else {
//                binding.checkDeposit.isChecked = false
//                binding.checkReceived.isChecked = false
//                binding.checkMoved.isChecked = false
//            }
//        }
//
//        binding.checkDeposit.setOnCheckedChangeListener { _, b ->
//            if (b) {
//                binding.checkReceived.isChecked = false
//                binding.checkTransferred.isChecked = false
//                binding.checkMoved.isChecked = false
//            } else {
//                binding.checkReceived.isChecked = false
//                binding.checkTransferred.isChecked = false
//                binding.checkMoved.isChecked = false
//            }
//        }
//
//        binding.checkMoved.setOnCheckedChangeListener { _, b ->
//            if (b) {
//                binding.checkDeposit.isChecked = false
//                binding.checkTransferred.isChecked = false
//                binding.checkReceived.isChecked = false
//            } else {
//                binding.checkDeposit.isChecked = false
//                binding.checkTransferred.isChecked = false
//                binding.checkReceived.isChecked = false
//            }
//        }

        binding.tvClear.setOnClickListener {
            binding.checkReceived.isChecked = false
            binding.checkTransferred.isChecked = false
            binding.checkDeposit.isChecked = false
            binding.checkMoved.isChecked = false

            binding.tvLastMonth.isChecked = false
            binding.tvLastThreeMonth.isChecked = false
            binding.tvLastSixMonth.isChecked = false
            binding.tvLastYear.isChecked = false
            binding.tvAllTime.isChecked = false

//            Utils.transaction_status = ""
//            Utils.date_type = null
//            dismiss()
//            onClear.invoke(this)
        }

        binding.tvLastMonth.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.tvLastThreeMonth.isChecked = false
                binding.tvLastSixMonth.isChecked = false
                binding.tvLastYear.isChecked = false
                binding.tvAllTime.isChecked = false
            }
        }

        binding.tvLastThreeMonth.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.tvLastMonth.isChecked = false
                binding.tvLastSixMonth.isChecked = false
                binding.tvLastYear.isChecked = false
                binding.tvAllTime.isChecked = false
            }
        }

        binding.tvLastSixMonth.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.tvLastThreeMonth.isChecked = false
                binding.tvLastMonth.isChecked = false
                binding.tvLastYear.isChecked = false
                binding.tvAllTime.isChecked = false
            }
        }

        binding.tvLastYear.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.tvLastThreeMonth.isChecked = false
                binding.tvLastMonth.isChecked = false
                binding.tvLastMonth.isChecked = false
                binding.tvAllTime.isChecked = false
            }
        }

        binding.tvAllTime.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.tvLastThreeMonth.isChecked = false
                binding.tvLastMonth.isChecked = false
                binding.tvLastMonth.isChecked = false
                binding.tvLastMonth.isChecked = false
            }
        }

        binding.btnApply.setOnClickListener {
            if (binding.checkReceived.isChecked)
                Utils.transaction_status = "Received"
            else if (binding.checkTransferred.isChecked)
                Utils.transaction_status = "Transferred"
            else if (binding.checkDeposit.isChecked)
                Utils.transaction_status = "Deposited"
            else if (binding.checkMoved.isChecked)
                Utils.transaction_status = "Moved"
            else Utils.transaction_status = ""

            if (binding.tvLastMonth.isChecked)
                Utils.date_type = 1
            else if (binding.tvLastThreeMonth.isChecked)
                Utils.date_type = 2
            else if (binding.tvLastSixMonth.isChecked)
                Utils.date_type = 3
            else if (binding.tvLastYear.isChecked)
                Utils.date_type = 4
            else if (binding.tvAllTime.isChecked)
                Utils.date_type = null

            dismiss()
            onFilter.invoke(this)
        }
    }

}