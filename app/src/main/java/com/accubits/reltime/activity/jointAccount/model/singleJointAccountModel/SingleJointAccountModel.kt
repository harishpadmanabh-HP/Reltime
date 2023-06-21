package com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep

data class SingleJointAccountModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result?,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)