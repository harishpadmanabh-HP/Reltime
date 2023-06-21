package com.accubits.reltime.activity.jointAccount.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.activity.contacts.ContactItemListener
import com.accubits.reltime.activity.contacts.model.ContactData
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Member
import com.accubits.reltime.databinding.ContactItemBinding

private const val TAG = "JointAccountContactSear"

class JointAccountContactSearchAdapter(private val listener: ContactItemListener) :
    RecyclerView.Adapter<JointAccountContactSearchAdapter.Holder>(), Filterable {
    private var contactList: ArrayList<ContactData>? = null
    private var fullContactList: ArrayList<ContactData>? = null
    private var characterMap: HashMap<Int, Char>? = null
    private var invitedContactMap: List<Member>? = null
    private var isFiltering: Boolean = false

    private var selectedUserMap: HashMap<String, ContactData>? = null

    fun onDataChange(
        characterMap: HashMap<Int, Char>,
        contactList: ArrayList<ContactData>,
        selectedUserMap: HashMap<String, ContactData>?,
        invitedContactMap: List<Member>?
    ) {
        this.contactList = contactList
        this.fullContactList = contactList
        this.selectedUserMap = selectedUserMap ?: HashMap()
        this.characterMap = characterMap
        this.invitedContactMap = invitedContactMap
        notifyDataSetChanged()
    }

    fun getSelectedContacts() = selectedUserMap

    fun clearList() {
        contactList?.clear()
        selectedUserMap?.clear()
        characterMap?.clear()
        //invitedContactMap?.clear()
    }

    inner class Holder(private val binder: ContactItemBinding) :
        RecyclerView.ViewHolder(binder.root) {

        fun bindData(item: ContactData,pos: Int){

            makeCheckBoxVisible(item)
            setContactData(item)
            val character = getCharacterFromMap(pos)
            if (character != null && !isFiltering) {
                setPadding(32)
                setCharacterVisibility(true)
                setFirstCharacter(character)
            } else {
                setPadding(8)
                setCharacterVisibility(false)
//                holder.setFirstCharacter('\u0000') // setting empty char
            }
            onCheckBoxClick(item)
            setImageUri(item.contactImageUri, item.contactName[0])
        }

        fun makeCheckBoxVisible(item: ContactData) {
            if(invitedContactMap!=null&& invitedContactMap?.isNotEmpty() == true)
            for (invited in invitedContactMap!!){
                if(invited.user.toString()==item.userId){
                    binder.cbSelect.visibility = View.GONE
                    binder.tvInvite.visibility = View.VISIBLE
                    binder.tvInvite.text=binder.tvInvite.context.resources.getString( if (invited.isAccepted) R.string.added_ else R.string.invited)
                    break
                }
                binder.cbSelect.visibility = View.VISIBLE
                binder.tvInvite.visibility = View.GONE
            }
            else{
                binder.cbSelect.visibility = View.VISIBLE
                binder.tvInvite.visibility = View.GONE
            }

        }

        fun onCheckBoxClick(contactData: ContactData) {
            val data = selectedUserMap?.get(contactData.userId)
            binder.cbSelect.isChecked = data != null
            binder.cbSelect.isClickable=false
            binder.parentContactItem.setOnClickListener {
                val isDataNotChecked= selectedUserMap?.get(contactData.userId) == null
                if (isDataNotChecked) {
                    selectedUserMap?.put(contactData.userId!!, contactData)
                    binder.cbSelect.isChecked=true
                } else {
                    selectedUserMap?.remove(contactData.userId!!)
                    binder.cbSelect.isChecked=false
                }
            }
        }

        fun setFirstCharacter(char: Char) {
            binder.tvLetterText.text = char.toString()
        }

        fun setContactData(contactData: ContactData) {
            binder.tvContactName.text = contactData.contactName
            binder.tvContactType.text = "${contactData.contactType}: ${contactData.contactNumber}"

        }

        fun setPadding(sizeInDp: Int) {
            val scale: Float = binder.root.context.resources.displayMetrics.density
            val topPadding = (sizeInDp * scale + 0.5f).toInt()
            binder.root.setPadding(0, topPadding, 0, 0)
        }

        fun setImageUri(uri: String?, char: Char) {
            try{
                if (uri != null) {
                    binder.ivUserIcon.setImageURI(Uri.parse(uri))
                    binder.ivUserIcon.visibility = View.VISIBLE
                    binder.tvLogoChar.visibility = View.INVISIBLE
                } else {
                    binder.ivUserIcon.visibility = View.INVISIBLE
                    binder.tvLogoChar.visibility = View.VISIBLE
                    binder.tvLogoChar.text=char.toString()
                }
            }catch (e :Exception){
                binder.ivUserIcon.visibility = View.INVISIBLE
                binder.tvLogoChar.visibility = View.VISIBLE
                binder.tvLogoChar.text=char.toString()
            }

        }

        fun setCharacterVisibility(visible: Boolean) {
            binder.tvLetterText.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binder = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binder)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bindData(item,position)

        }
    }

    private fun getCharacterFromMap(position: Int): Char? {
        return characterMap?.get(position)
    }

    private fun getItem(position: Int): ContactData? {
        return contactList?.get(position)
    }

    override fun getItemCount(): Int {
        return contactList?.size ?: 0
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                //isFiltering = true
                val searchString = constraint?.toString() ?: ""
                val filteredList = fullContactList?.filter {
                    it.contactNumber.contains(searchString, true) ||
                            it.contactName.contains(searchString, true)
                }
                val filterResult = FilterResults()
                filterResult.count = filteredList?.size ?: 0
                filterResult.values = filteredList
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null) {
                    contactList = results.values as ArrayList<ContactData>
                    if (contactList?.size == 0) {
                        listener.onListEmpty(true)
                    } else {
                        isFiltering = contactList?.size != fullContactList?.size
                        notifyDataSetChanged()
                        listener.onListEmpty(false, isFiltering)
                    }
                } else {
                    listener.onListEmpty(true)
                }
            }
        }
    }
}

