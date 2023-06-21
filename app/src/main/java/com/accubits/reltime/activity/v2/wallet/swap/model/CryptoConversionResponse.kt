package com.accubits.reltime.activity.v2.wallet.swap.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CryptoConversionResponse
    (
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: CryptoConversionResult?,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

@Keep
data class CryptoConversionResult(
    @SerializedName("converted_amount")
    val converted_amount: String)