package com.accubits.reltime.activity.jointAccount.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SignJointAccountTxnRequestModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("txnHash")
    val txnHash: String,
    @SerializedName("type")
    val type: String
)