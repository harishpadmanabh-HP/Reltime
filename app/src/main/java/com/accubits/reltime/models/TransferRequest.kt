package com.accubits.reltime.models


import com.google.gson.annotations.SerializedName

data class TransferRequest(
    @SerializedName("address")
    val address: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("coinCode")
    val coinCode: String?,
    @SerializedName("contact_type")
    val contactType: String,
    @SerializedName("method")
    val method: String,
    @SerializedName("receiver")
    val `receiver`: String,
    @SerializedName("description")
    val description: String
)