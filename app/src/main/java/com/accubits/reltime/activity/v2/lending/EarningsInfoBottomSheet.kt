package com.accubits.reltime.activity.v2.lending

import android.content.Context
import android.view.Window
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.lending.model.ResultLendingCalculation
import com.accubits.reltime.databinding.BottomSheetDialogEarningsInfoBinding
import com.accubits.reltime.helpers.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog


class EarningsInfoBottomSheet(
    context: Context, private val lendCalculation: ResultLendingCalculation
) : BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme) {
    private var binding: BottomSheetDialogEarningsInfoBinding =
        BottomSheetDialogEarningsInfoBinding.inflate(layoutInflater)

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        init()
        setListeners()
    }

    private fun init() {
        binding.tvTotalInterestTitle.text =
            context.resources.getString(R.string.total_interest_percentage,lendCalculation.totalPercentage.toString()+"%")
        binding.tvTotalEarningsTitle.text =
            context.resources.getString(R.string.your_earnings_percentage,lendCalculation.earningPercentage.toString()+"%")
        binding.tvReltimeFeeTitle.text =
            context.resources.getString(R.string.reltime_fee_percentage,lendCalculation.reltimePercentage.toString()+"%")

        binding.tvTotalInterest.text = Utils.getRTOAmount(lendCalculation.totalInterestAmount)
        binding.tvTotalEarnings.text =Utils.getRTOAmount(lendCalculation.totalEarnings)
        binding.tvReltimeFee.text =Utils.getRTOAmount(lendCalculation.totalAdminShare)
    }

    private fun setListeners() {
        binding.ivClose.setOnClickListener { dismiss() }

    }


}