package com.accubits.reltime.activity.rto.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RtoP2PResponseModel(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("result")
    val result: Result? = null
) {
    @Keep
    data class Result(
        @SerializedName("id")
        val id: Int,
        @SerializedName("signedTxn")
        val signedTxn: String,
        @SerializedName("txnBuild")
        val txnBuild: TxnBuild
    ) {
        @Keep
        data class TxnBuild(
            @SerializedName("data")
            val data: String,
            @SerializedName("gasLimit")
            val gasLimit: String,
            @SerializedName("gasPrice")
            val gasPrice: String,
            @SerializedName("nonce")
            val nonce: String,
            @SerializedName("to")
            val to: String,
            @SerializedName("value")
            val value: String
        )
    }
}