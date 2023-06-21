package com.accubits.reltime.activity.v2.wallet.jointaccount

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Member
import com.accubits.reltime.databinding.RowJointAccountMemberBinding

class JointAccountMemberListAdapter(private val mListener: (Int) -> Unit) :
    RecyclerView.Adapter<JointAccountMemberListAdapter.UserHolder>() {

    private var memberList: List<Member>? = ArrayList()
    private var isAdmin: Boolean = false

    inner class UserHolder(private val binder: RowJointAccountMemberBinding) :
        RecyclerView.ViewHolder(binder.root) {

        fun setDetails(member: Member) {
            binder.apply {
                tvLogoChar.text = (member.name[0]).toString()
                tvContactName.text = member.name
                if (member.isAdmin) {
                    tvAdmin.visibility = View.VISIBLE
                } else {
                    tvAdmin.visibility = View.GONE
                }
                tvContactType.text = member.permission_label
                    ?: root.context.resources.getString(R.string.no_permission_set)

                if (member.isAdmin)
                    btMore.visibility = View.GONE
                else btMore.visibility = View.VISIBLE

                btMore.setOnClickListener {
                    if (!member.isAdmin)
                        mListener.invoke(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JointAccountMemberListAdapter.UserHolder {
        val binder =
            RowJointAccountMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserHolder(binder)
    }

    override fun onBindViewHolder(
        holder: JointAccountMemberListAdapter.UserHolder,
        position: Int
    ) {
        val member = getMember(position)
        holder.setDetails(member)
    }

    fun setAdmin(admin: Boolean) {
        isAdmin = admin
    }

    private fun getMember(position: Int): Member {
        return memberList!![position]
    }

    fun getMembers(): List<Member> {
        return memberList!!
    }

    override fun getItemCount(): Int {
        return memberList?.size ?: 0
    }

    fun updateList(memberList: List<Member>) {
        this.memberList = memberList
        notifyDataSetChanged()
    }
}