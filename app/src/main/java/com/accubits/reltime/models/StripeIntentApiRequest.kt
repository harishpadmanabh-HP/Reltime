package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StripeIntentApiRequest(

    @Expose
    @SerializedName("amount")
    var amount: Int,
    @Expose
    @SerializedName("currency")
    val currency: String
)
