package com.accubits.reltime.activity.v2.loan

import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.Window
import com.accubits.reltime.R
import com.accubits.reltime.databinding.BottomSheetDialogConfirmBorrowBinding
import com.accubits.reltime.databinding.BottomSheetDialogLoginBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class ConfirmBorrowBottomSheet(
    context: Context
) : BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme) {
    var binding: BottomSheetDialogConfirmBorrowBinding =
        BottomSheetDialogConfirmBorrowBinding.inflate(layoutInflater)

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
//        binding.btnNext.setOnClickListener {
//            validate()
//        }
//        binding.etPhone.setOnKeyListener { v, keyCode, event ->
//            if (event.action == KeyEvent.ACTION_DOWN &&
//                keyCode == KeyEvent.KEYCODE_ENTER
//            ) {
//                validate()
//                true
//            } else false
//        }
//        binding.ivClose.setOnClickListener { dismiss() }
    }

    private fun validate() {
//        if (!Patterns.PHONE.matcher(binding.etPhone.text.toString().trim()).matches()) {
//            binding.tvError.text =
//                context.resources.getString(R.string.phone_error)
//            binding.tvError.visibility = View.VISIBLE
//        }
//        else if (binding.etPhone.text.toString().trim().length<6||binding.etPhone.text.toString().trim().length>13){
//            binding.tvError.text =
//                context.resources.getString(R.string.phone_error)
//            binding.tvError.visibility = View.VISIBLE
//        }
//        else {
//            binding.tvError.visibility = View.GONE
////            val intent = Intent(context, BiometricLoginV2Activity::class.java)
////            intent.putExtra(BiometricLoginV2Activity.EXTRA_PHONE_NUMBER,binding.etPhone.text.toString().trim())
////            context.startActivity(intent)
//            binding.etPhone.postDelayed({dismiss()},1000)
//        }
    }


}