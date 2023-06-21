package com.accubits.reltime.activity.v2.loan

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import androidx.core.view.isVisible
import com.accubits.reltime.R
import com.accubits.reltime.databinding.DialogSortBinding
import com.accubits.reltime.helpers.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog

class BorrowSortBottomSheet(
    context: Context,
    private val onFilter: (BottomSheetDialog) -> Unit
) : BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme) {
    var binding: DialogSortBinding =
        DialogSortBinding.inflate(layoutInflater)

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)

        init()
    }

    private fun init() {
        binding.loanAmount.setOnCheckedChangeListener { _, checkedId ->
            if (findViewById<RadioButton>(checkedId)?.isChecked != true)
                return@setOnCheckedChangeListener
            when (checkedId) {
                R.id.rbLoanAmountLow -> {
                    onRBSelection(amountSort = "0")
                }
                R.id.rbLoanAmountHigh -> {
                    onRBSelection(amountSort = "1")
                }
                R.id.rbInterestRateLow -> {
                    onRBSelection(interestRateSort = "0")
                }
                R.id.rbInterestRateHigh -> {
                    onRBSelection(interestRateSort = "1")
                }
                R.id.rbLoanTermMinimum -> {
                    onRBSelection(loanTermSort = "0")
                }
                R.id.rbLoanTermMaximum -> {
                    onRBSelection(loanTermSort = "1")
                }
            }
        }
        binding.tvClear.setOnClickListener {
            binding.loanAmount.clearCheck()
            onRBSelection()
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }
        apply {
            show()
            window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes.windowAnimations = R.style.BottomDialogAnimation
                setGravity(Gravity.BOTTOM)
            }
        }
    }

    private fun onRBSelection(
        amountSort: String? = "",
        interestRateSort: String? = "",
        loanTermSort: String? = ""
    ) {
        if (amountSort.isNullOrEmpty()&&interestRateSort.isNullOrEmpty()&&loanTermSort.isNullOrEmpty())
            binding.tvClear.visibility = View.GONE
        else if (!binding.tvClear.isVisible)
            binding.tvClear.visibility = View.VISIBLE

        amountSort?.let { Utils.amount_sort = it }
        interestRateSort?.let { Utils.interest_rate_sort = it }
        loanTermSort?.let { Utils.loan_term_sort = it }
        dismiss()
        onFilter.invoke(this)
    }

    override fun show() {
        super.show()
        if (Utils.amount_sort.isNotEmpty()) {
            if (Utils.amount_sort == "0") {
                binding.rbLoanAmountLow.isChecked = true
            } else {
                binding.rbLoanAmountHigh.isChecked = true
            }
        }

        if (Utils.interest_rate_sort.isNotEmpty()) {
            if (Utils.interest_rate_sort == "0") {
                binding.rbInterestRateLow.isChecked = true
            } else {
                binding.rbInterestRateHigh.isChecked = true
            }
        }

        if (Utils.loan_term_sort.isNotEmpty()) {
            if (Utils.loan_term_sort == "0") {
                binding.rbLoanTermMinimum.isChecked = true
            } else {
                binding.rbLoanTermMaximum.isChecked = true
            }
        }
    }
}

