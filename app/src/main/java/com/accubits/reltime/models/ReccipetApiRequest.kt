package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReccipetApiRequest(
    @Expose
    @SerializedName("paymentIntentId")
    val paymentIntentId: String,
    @Expose
    @SerializedName("transaction_for")
    val transaction_for: String,
    @Expose
    @SerializedName("type")
    val type: String,
    @Expose
    @SerializedName("borrowing_id")
    val borrowing_id: Int?
)
