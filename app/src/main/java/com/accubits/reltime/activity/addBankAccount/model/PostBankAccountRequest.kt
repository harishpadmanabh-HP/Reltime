package com.accubits.reltime.activity.addBankAccount.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostBankAccountRequest(
    @Expose
    @SerializedName("bank_name")
    val bank_name: String? = null,
    @Expose
    @SerializedName("account_number")
    val account_number: String? = null,
    @Expose
    @SerializedName("full_name")
    val full_name: String? = null,
    @Expose
    @SerializedName("swift_code")
    val swift_code: String? = null,
    @Expose
    @SerializedName("address")
    val address: String? = null,
    @Expose
    @SerializedName("contact_number")
    val contact_number: String? = null,
    @Expose
    @SerializedName("routing_number")
    val routing_number: String? = null,
    @Expose
    @SerializedName("account_type")
    val account_type: String? = null
    )