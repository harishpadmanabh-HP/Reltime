package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class CheckoutApiRequest(
    @Expose
    @SerializedName("amount")
    val amount: String,
    @Expose
    @SerializedName("source_currency")
    val source_currency: String,
    @Expose
    @SerializedName("dest_currency")
    val dest_currency: String,
)
