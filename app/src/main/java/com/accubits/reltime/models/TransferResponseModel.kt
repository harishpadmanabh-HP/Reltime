package com.accubits.reltime.models


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TransferResponseModel(
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Data,
    @SerializedName("status") val status: Int,
    @SerializedName("success") val success: Boolean
) {
    @Keep
    data class Data(
        @SerializedName("data") val `data`: Data,
        @SerializedName("error") val error: Any,
        @SerializedName("id") val id: Int,
        @SerializedName("success") val success: Boolean
    ) {
        @Keep
        data class Data(
            @SerializedName("txnBuild") val txnBuild: TxnBuild
        ) {
            @Keep
            data class TxnBuild(
                @SerializedName("data") val `data`: String,
                @SerializedName("gasLimit") val gasLimit: String,
                @SerializedName("gasPrice") val gasPrice: String,
                @SerializedName("nonce") val nonce: String,
                @SerializedName("to") val to: String,
                @SerializedName("value") val value: String
            )
        }
    }
}



