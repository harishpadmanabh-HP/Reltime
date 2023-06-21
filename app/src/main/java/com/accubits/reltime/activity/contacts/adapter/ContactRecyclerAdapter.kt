package com.accubits.reltime.activity.contacts.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.activity.contacts.ContactItemListener
import com.accubits.reltime.activity.contacts.model.ContactData
import com.accubits.reltime.databinding.ContactItemBinding
import com.accubits.reltime.utils.Extensions.loadImageWithUrl

class ContactRecyclerAdapter(private val listener: ContactItemListener,val isNewDesign:Boolean?=false) :
    RecyclerView.Adapter<ContactRecyclerAdapter.Holder>(), Filterable {
    private var contactList: ArrayList<ContactData>? = null
    private var fullContactList: ArrayList<ContactData>? = null
    private var characterMap: HashMap<Int, Char>? = null
    private var isFiltering: Boolean = false

    fun onDataChange(characterMap: HashMap<Int, Char>, contactList: ArrayList<ContactData>) {
        this.contactList = contactList
        this.fullContactList = contactList
        this.characterMap = characterMap
        notifyDataSetChanged()
    }

    fun clearList() {
        contactList?.clear()
        characterMap?.clear()
    }

    inner class Holder(private val binder: ContactItemBinding) :
        RecyclerView.ViewHolder(binder.root) {
        fun setFirstCharacter(char: Char) {
            binder.tvLetterText.text = char.toString()
        }

        fun setContactData(contactData: ContactData) {
            binder.tvContactName.text = contactData.contactName
            binder.tvContactType.text = binder.tvContactType.context.resources.getString(R.string.n_n_,contactData.contactType,contactData.contactNumber)
            binder.tvContactType.visibility= if (isNewDesign==true) GONE else VISIBLE
        }

        fun setPadding(sizeInDp: Int) {
            val scale: Float = binder.root.context.resources.displayMetrics.density
            val topPadding = (sizeInDp * scale + 0.5f).toInt()
            binder.root.setPadding(0, topPadding, 0, 0)
        }

        fun prepareInvite(contactData: ContactData) {
            binder.tvInvite.setOnClickListener {
                listener.onInviteClicked(contactData)
//                listener.onContactNameClicked(contactData)
            }
        }

        fun prepareContact(contactData: ContactData) {
            Log.d("Contact", "load Condition"+contactData)
            binder.root.setOnClickListener {
                Log.d("Contact", "befor Condition"+contactData)
                if (contactData.exist && contactData.userId != null) {
                    Log.d("Contact", ""+contactData)
                    listener.onContactNameClicked(contactData)
                }
            }
        }

        fun setImageUri(uri: String?, char: Char) {
            if (uri != null) {
                binder.ivUserIcon.loadImageWithUrl(uri) { isError ->
                    if (!isError) {
                        binder.tvLogoChar.visibility = INVISIBLE
                        binder.ivUserIcon.visibility = VISIBLE
                    } else {
                        binder.ivUserIcon.visibility = INVISIBLE
                        binder.tvLogoChar.visibility = VISIBLE
                        binder.tvLogoChar.text = char.toString()
                    }
                }
            } else {
                binder.ivUserIcon.visibility = INVISIBLE
                binder.tvLogoChar.visibility = VISIBLE
                binder.tvLogoChar.text = char.toString()
            }
        }

        fun makeInvite(exist: Boolean) {
            binder.tvInvite.visibility = if (exist) INVISIBLE else VISIBLE
        }

        fun setCharacterVisibility(visible: Boolean) {
            binder.tvLetterText.visibility = if (visible) VISIBLE else GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binder = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binder)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.setContactData(item)
            val character = getCharacterFromMap(position)
            if (character != null && !isFiltering) {
                holder.setPadding(24)
                holder.setCharacterVisibility(true)
                holder.setFirstCharacter(character)
            } else {
                holder.setPadding(16)
                holder.setCharacterVisibility(false)
//                holder.setFirstCharacter('\u0000') // setting empty char
            }
            holder.makeInvite(item.exist)
            holder.setImageUri(item.contactImageUri, item.contactName[0])
            holder.prepareInvite(item)
            holder.prepareContact(item)

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

