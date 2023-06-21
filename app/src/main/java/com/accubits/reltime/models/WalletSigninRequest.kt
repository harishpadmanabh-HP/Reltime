package com.accubits.reltime.models


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class WalletSigninRequest(
    @SerializedName("id") val id: Int?=null,
    @SerializedName("signedTxn") val signedTxn: String,
    @SerializedName("type") val type: String? = null,
    @SerializedName("chain") val chain: String? = null
)