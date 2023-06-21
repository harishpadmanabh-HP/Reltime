package com.accubits.reltime.activity.jointAccount.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.compose.ui.res.stringResource
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Member
import com.accubits.reltime.databinding.CardAccountUsersBinding
import org.web3j.abi.datatypes.Bool

class ViewJointAccountUserListAdapter(private var listener :Listener?=null) :
    RecyclerView.Adapter<ViewJointAccountUserListAdapter.UserHolder>() {

    private var memberList: List<Member>? = ArrayList()
    private var isAdmin : Boolean=false

    inner class UserHolder(private val binder: CardAccountUsersBinding) :
        RecyclerView.ViewHolder(binder.root) {

        fun setDetails(member: Member) {
            binder.apply {
                tvLogoChar.text = (member.name[0]).toString()
                tvContactName.text = member.name
                if (member.isAdmin) {
                    tvAdmin.visibility = View.VISIBLE
                }
                else {
                    tvAdmin.visibility = View.GONE
                }
                tvContactType.text = member.permission_label ?: root.context.resources.getString(R.string.no_permission_set)

                tvRemove.visibility = View.GONE
                if(listener==null||member.isAdmin)
                    btMore.visibility = View.GONE
                else btMore.visibility = View.VISIBLE
                btMore.setOnClickListener {
                    displayPopupWindow(binder.btMore,member)
                }
            }
        }
    }
    private fun displayPopupWindow(anchorView: View, member: Member) {
        val popupView: View = LayoutInflater.from(anchorView.context).inflate(R.layout.popup_edit_joint_account, null)
        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(null)
        popupWindow.showAsDropDown(anchorView)

        var tvRemove = popupView.findViewById<TextView>(R.id.tv_remove)
        var tvSetPermission = popupView.findViewById<TextView>(R.id.tvSetPermission)
        if(!isAdmin){
            tvRemove.visibility=View.GONE
        }
        tvSetPermission.setOnClickListener {
            listener?.onSetPermission(member)
            popupWindow.dismiss()
        }
        tvRemove.setOnClickListener {
            listener?.onRemove(member)
            popupWindow.dismiss()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewJointAccountUserListAdapter.UserHolder {
        val binder =
            CardAccountUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserHolder(binder)
    }

    override fun onBindViewHolder(
        holder: ViewJointAccountUserListAdapter.UserHolder,
        position: Int
    ) {
        val member = getMember(position)
        holder.setDetails(member)
    }

    fun setAdmin(admin: Boolean) {
        isAdmin=admin
    }

    private fun getMember(position: Int): Member {
        return memberList!![position]
    }

    fun getMembers(): List<Member> {
        return memberList!!
    }

    override fun getItemCount(): Int {
        return memberList?.size?: 0
    }

    fun updateList(memberList: List<Member>) {
        this.memberList = memberList
        notifyDataSetChanged()
    }

    interface Listener{
        fun onSetPermission(member: Member)
        fun onRemove(member: Member)
    }
}