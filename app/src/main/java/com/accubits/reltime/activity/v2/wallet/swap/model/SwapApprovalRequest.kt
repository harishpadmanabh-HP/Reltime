package com.accubits.reltime.activity.v2.wallet.swap.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SwapApprovalRequest(
    @SerializedName("coinCode")
    val coinCode: String,
    @SerializedName("to_coinCode")
    val to_coinCode: String?=null,
    @SerializedName("amount")
    val amount: String?,//Double?,
    @SerializedName("converted_amount")
    val converted_amount: String?=null
)

/*
{
    "coinCode": "RTO",
    "amount": 70,
    "to_coinCode": "RTC",
    "converted_amount": 100
}*/
