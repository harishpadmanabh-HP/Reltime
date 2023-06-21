package com.accubits.reltime.activity.withdraw.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AccountListResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: AccountResult,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)
