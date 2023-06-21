package com.accubits.reltime.activity.jointAccount.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.activity.contacts.model.ContactData
import com.accubits.reltime.activity.jointAccount.UserListListener
import com.accubits.reltime.databinding.CardAccountUsersBinding

class JointAccountSelectedContactAdapter(private val listener: UserListListener) :
    RecyclerView.Adapter<JointAccountSelectedContactAdapter.SelectedHolder>() {

    private var selectedUsers: ArrayList<ContactData>? = ArrayList()

    inner class SelectedHolder(private val binder: CardAccountUsersBinding) :
        RecyclerView.ViewHolder(binder.root) {
        fun setDetails(contactData: ContactData) {
            binder.tvAdmin.visibility = View.GONE
            binder.tvLogoChar.text = (contactData.contactName[0]).toString()
            binder.tvContactName.text = contactData.contactName
            binder.tvContactType.text = "${contactData.contactType}: ${contactData.contactNumber}"
            binder.tvRemove.visibility = View.VISIBLE
            binder.tvRemove.setOnClickListener {
                listener.onRemove(contactData)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JointAccountSelectedContactAdapter.SelectedHolder {
        val binder =
            CardAccountUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectedHolder(binder)
    }

    override fun onBindViewHolder(
        holder: JointAccountSelectedContactAdapter.SelectedHolder,
        position: Int
    ) {
        val contactData = getContact(position)
        if (contactData != null) {
            holder.setDetails(contactData)
        }
    }

    private fun getContact(position: Int): ContactData? {
        return selectedUsers?.get(position)
    }

    override fun getItemCount(): Int {
        return selectedUsers?.size ?: 0
    }

    fun updateList(chosenContacts: HashMap<String, ContactData>) {
        selectedUsers?.clear()
        selectedUsers?.addAll(chosenContacts.values)
    }
}