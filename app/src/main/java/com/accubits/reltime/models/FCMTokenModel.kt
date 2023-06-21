package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FCMTokenModel(
    @SerializedName("fcmToken")
    val fcmToken: String,
    @SerializedName("type")
    val type: String
)