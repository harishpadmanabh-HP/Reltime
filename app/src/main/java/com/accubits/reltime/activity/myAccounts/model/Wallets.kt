package com.accubits.reltime.activity.myAccounts.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Wallets(
    @SerializedName("RTC")
    val rTC: RTO?,
    @SerializedName("RTO")
    val rTO: RTO?
)