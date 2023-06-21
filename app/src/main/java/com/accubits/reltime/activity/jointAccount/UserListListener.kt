package com.accubits.reltime.activity.jointAccount

import com.accubits.reltime.activity.contacts.model.ContactData


interface UserListListener {
    fun onRemove(contactData: ContactData)
}