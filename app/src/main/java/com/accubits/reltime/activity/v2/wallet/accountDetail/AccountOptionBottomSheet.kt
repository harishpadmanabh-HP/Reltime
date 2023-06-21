package com.accubits.reltime.activity.v2.wallet.accountDetail

import android.content.Context
import android.view.Window
import com.accubits.reltime.R
import com.accubits.reltime.databinding.BottomSheetAccountOptionBinding
import com.accubits.reltime.databinding.BottomSheetTransactionHistoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class AccountOptionBottomSheet(
    context: Context
) : BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme) {
    private var binding: BottomSheetAccountOptionBinding =
        BottomSheetAccountOptionBinding.inflate(layoutInflater)

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        init()
        setListeners()
    }

    private fun init() {

    }

    private fun setListeners() {
//        binding.ivClose.setOnClickListener { dismiss() }
    }

}