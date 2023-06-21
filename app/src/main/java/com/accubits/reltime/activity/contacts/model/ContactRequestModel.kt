package com.accubits.reltime.activity.contacts.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ContactRequestModel(
    @SerializedName("contacts")
    var contacts: List<Contact>? = null
) {
    @Keep
    data class Contact(
        @SerializedName("contact_number")
        val contact_number: String,
        @SerializedName("name")
        val name: String
    )
}