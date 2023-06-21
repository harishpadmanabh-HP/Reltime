package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TransactionApprovalRequestModel(
    @SerializedName("amount")
    val amount: Float,
    @SerializedName("coinCode")
    val coinCode: String,
    @SerializedName("joint_account")
    val jointAccount: String,
)