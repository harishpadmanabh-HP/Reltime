package com.accubits.reltime.activity.v2.transfer.bottomSheet

import android.content.Context
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.transfer.viewmodel.TransferViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.DialogFilterBinding
import com.accubits.reltime.databinding.DialogSelectAmountRtoBinding
import com.accubits.reltime.utils.Extensions.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

class MyRtoAccountsBottomSheetDialog(
    context: Context
) : BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme) {
    var binding: DialogSelectAmountRtoBinding =
        DialogSelectAmountRtoBinding.inflate(layoutInflater)


    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)

        init()
    }


    private fun init() {


        binding.ivClose.setOnClickListener {
            dismiss()
        }

    }

}