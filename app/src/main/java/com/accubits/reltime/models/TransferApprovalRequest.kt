package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class TransferApprovalRequest(
    @Expose
    @SerializedName("amount")
    var amount: Double?,
    @Expose
    @SerializedName("coinCode")
    val coinCode: String?,
    @Expose
    @SerializedName("joint_account")
    val joint_account: String? = null,
)
