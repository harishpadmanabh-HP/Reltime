package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class LendingRequestModel(
    @Expose
    @SerializedName("rto_amount")
    val rto_amount: Double?,
    @Expose
    @SerializedName("interest_rate")
    val interest_rate: String,
    @Expose
    @SerializedName("installments")
    val installments: String,
    @Expose
    @SerializedName("collateral")
    val collateral: String,
    @Expose
    @SerializedName("lendingType")
    val lendingType: String,
    @Expose
    @SerializedName("lendTo")
    val lendTo: String?,
    @Expose
    @SerializedName("lending_id")
    val lendingId: String?
)
