package com.accubits.reltime.activity.v2.lending.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class LendingCalculationRequest(
    @Expose
    @SerializedName("amount")
    val amount: String,
    @Expose
    @SerializedName("interest_rate")
    val interest_rate: String,
    @Expose
    @SerializedName("instalments")
    val instalments: String
)
