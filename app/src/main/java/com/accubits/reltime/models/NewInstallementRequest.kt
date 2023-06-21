package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class NewInstallementRequest(
    @Expose
    @SerializedName("payment_amount")
    var payment_amount: Double,
    @Expose
    @SerializedName("method")
    val method: String="wallet",
    @Expose
    @SerializedName("address")
    val address: String,
    @Expose
    @SerializedName("transaction_for")
    val transaction_for: String = "loanRepaymentV2",
    @Expose
    @SerializedName("borrowing_id")
    val borrowing_id: Int
)
