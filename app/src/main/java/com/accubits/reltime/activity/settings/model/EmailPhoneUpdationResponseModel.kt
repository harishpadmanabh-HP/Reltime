package com.accubits.reltime.activity.settings.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class EmailPhoneUpdationResponseModel(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
)