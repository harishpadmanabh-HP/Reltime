package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RequestLoanUpdateRequest(
    @Expose
    @SerializedName("paymentIntentId")
    val paymentIntentId: String?,
    @Expose
    @SerializedName("transaction_for")
    val transaction_for: String,
    @Expose
    @SerializedName("type")
    val type: String,
    @Expose
    @SerializedName("borrowing_id")
    var borrowing_id: Int,
    @Expose
    @SerializedName("payment_amount")
    var payment_amount: Double?
)
