package com.accubits.reltime.activity.rto.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RtoSignTransactionRequestModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("signedTxn")
    val signedTxn: String,
    @SerializedName("type")
    val type: String
)