package com.accubits.reltime.activity.rto.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class RtoSignTransactionResponseModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result?,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
) {
    @Keep
    data class Result(
        @SerializedName("From")
        val From: String,
        @SerializedName("To")
        val To: String,
        @SerializedName("date")
        val date: String,
        @SerializedName("time")
        val time: String,
        @SerializedName("transaction_id")
        val transaction_id: String,
        @Expose
        @SerializedName("timestamp")
        val timestamp: String,
        @Expose
        @SerializedName("amount")
        val amount: String,


    )
}