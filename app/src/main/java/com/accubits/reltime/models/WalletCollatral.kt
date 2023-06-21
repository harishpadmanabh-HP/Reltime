package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WalletCollatral(
    @Expose
    @SerializedName("amount")
    var amount: Double,
    @Expose
    @SerializedName("coinCode")
    val coinCode: String
)
