package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class SignTransactionRequest(
    @Expose
    @SerializedName("signedTxn")
    val signedTxn: String,
    @Expose
    @SerializedName("type")
    val type: String?,
    @Expose
    @SerializedName("id")
    var id: Int?,
    @Expose
    @SerializedName("success")
    var success: Boolean?
)
