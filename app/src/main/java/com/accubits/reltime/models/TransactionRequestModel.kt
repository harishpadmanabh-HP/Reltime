package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class TransactionRequestModel(
    @Expose
    @SerializedName("amount")
    var amount: Double?
)
