package com.accubits.reltime.activity.v2.login

import android.content.Context
import android.view.Window
import com.accubits.reltime.R
import com.accubits.reltime.databinding.BottomSheetDialogConfirmBorrowBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ConfirmBottomSheetDialog(
    context: Context, title: String, desc: String, onConfirmClick: (Boolean) -> Unit
) : BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme) {
    var binding: BottomSheetDialogConfirmBorrowBinding =
        BottomSheetDialogConfirmBorrowBinding.inflate(layoutInflater)

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        binding.tvTitle.text = title
        binding.tvDesc.text = desc

        binding.btnCancel.setOnClickListener {
            cancel()
        }
        binding.btnAgree.setOnClickListener {
            cancel()
            onConfirmClick.invoke(true)
        }
        binding.ivClose.setOnClickListener {
            cancel()
        }
        show()
    }

}