package com.accubits.reltime.activity.jointAccount.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep

data class JointAccountListModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: List<ResultJointAccount>,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)