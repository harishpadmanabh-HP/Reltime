package com.accubits.reltime.activity.contacts

import com.accubits.reltime.activity.contacts.model.ContactData

interface ContactItemListener {
    fun onInviteClicked(contactData: ContactData)
    fun onContactNameClicked(contactData: ContactData)
    fun onContactIconClicked(contactData: ContactData)
    fun onListEmpty(onSearch: Boolean, isFiltering: Boolean = false)
}