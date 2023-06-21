package com.accubits.reltime.activity.myAccounts.model


import androidx.annotation.Keep
import com.accubits.reltime.activity.withdraw.model.AccountResult
import com.google.gson.annotations.SerializedName

@Keep
data class MyAccountsListModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: AccountResult,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)