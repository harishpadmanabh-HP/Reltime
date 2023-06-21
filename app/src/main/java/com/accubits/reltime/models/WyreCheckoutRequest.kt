package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class WyreCheckoutRequest(
    @Expose
    @SerializedName("amount")
    val amount: String,
    @Expose
    @SerializedName("coinCode")
    val coinCode: String
)
