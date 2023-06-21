package com.accubits.reltime.activity.v2.wallet.jointaccount

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.accubits.reltime.R
import com.accubits.reltime.activity.jointAccount.model.DeleteUserRequestModel
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Member
import com.accubits.reltime.databinding.BottomSheetAccountOptionBinding
import com.accubits.reltime.databinding.BottomSheetMemberOptionsBinding
import com.accubits.reltime.models.PermissionList
import com.google.android.material.bottomsheet.BottomSheetDialog

class MemberOptionBottomSheet(
    context: Context, private val onManagePermissions: (Member, String, Int?) -> Unit,
    private val onRemoveUser: (Member) -> Unit
) : BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme) {

    private val mContext: Context = context
    private var member: Member? = null
    private var permissionList: ArrayList<PermissionList>? = null

    private var binding: BottomSheetMemberOptionsBinding =
        BottomSheetMemberOptionsBinding.inflate(layoutInflater)

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
        binding.ivClose.setOnClickListener { dismiss() }
        binding.checkDeposit.setOnClickListener {
            if(binding.checkDeposit.isChecked) {
                if (binding.checkMove.isChecked)
                    member?.let { onManagePermissions.invoke(it, "Deposit & Move", 3) }
                else
                    member?.let { onManagePermissions.invoke(it, "Deposit", 1) }
            } else {
                if (binding.checkMove.isChecked)
                    member?.let { onManagePermissions.invoke(it, "Move", 2) }
                else member?.let { onManagePermissions.invoke(it, "", 0) }
            }
        }
//        binding.checkDeposit.setOnCheckedChangeListener { button, b ->
//            if(b) {
//                if (binding.checkMove.isChecked)
//                    member?.let { onManagePermissions.invoke(it, "Deposit & Move", 3) }
//                else
//                    member?.let { onManagePermissions.invoke(it, "Deposit", 1) }
//            } else {
//
//            }
//        }

        binding.checkMove.setOnClickListener {
            if(binding.checkMove.isChecked) {
                if (binding.checkDeposit.isChecked)
                    member?.let { onManagePermissions.invoke(it, "Deposit & Move", 3) }
                else
                    member?.let { onManagePermissions.invoke(it, "Move", 2) }
            } else {
                if (binding.checkDeposit.isChecked)
                    member?.let { onManagePermissions.invoke(it, "Deposit", 1) }
                else member?.let { onManagePermissions.invoke(it, "", 0) }
            }
        }

//        binding.checkMove.setOnCheckedChangeListener { _, b ->
//            if(b)
//                if (binding.checkDeposit.isChecked)
//                    member?.let { onManagePermissions.invoke(it, "Deposit & Move", 3) }
//                else
//                    member?.let { onManagePermissions.invoke(it, "Move", 2) }
//        }

        binding.layoutRemoveUser.setOnClickListener{
            member?.let { it1 -> onRemoveUser(it1) }
        }
    }

    public fun getData(memberData: Member?) {
        member = memberData
        populateValues()
    }

    private fun populateValues() {
        binding.tvRemoveUser.text = "My account member ${member?.name} will be removed"
        when (member?.permissions) {
            1 -> {
                binding.checkDeposit.isChecked = true
                binding.checkMove.isChecked = false
            }
            2 -> {
                binding.checkDeposit.isChecked = false
                binding.checkMove.isChecked = true
            }
            3 -> {
                binding.checkDeposit.isChecked = true
                binding.checkMove.isChecked = true
            }
        }
    }

    public fun getPermissionData(permissionList: ArrayList<PermissionList>?) {
        this.permissionList = permissionList
    }
}