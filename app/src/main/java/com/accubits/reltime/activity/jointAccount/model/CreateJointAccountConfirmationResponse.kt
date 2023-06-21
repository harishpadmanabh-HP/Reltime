package com.accubits.reltime.activity.jointAccount.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CreateJointAccountConfirmationResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)