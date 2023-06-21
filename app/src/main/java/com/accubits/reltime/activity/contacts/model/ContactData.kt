package com.accubits.reltime.activity.contacts.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class ContactData(
    val contactName: String,
    val contactNumber: String,
    var contactType: String,
    val contactImageUri: String? = null,
    var userId: String? = null,
    var exist: Boolean = false
) : Serializable