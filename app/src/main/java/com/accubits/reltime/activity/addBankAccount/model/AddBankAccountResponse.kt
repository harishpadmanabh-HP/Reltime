package com.accubits.reltime.activity.addBankAccount.model

import androidx.annotation.Keep
import com.accubits.reltime.activity.rto.model.RtoSignTransactionResponseModel
import com.google.gson.annotations.SerializedName

@Keep
data class AddBankAccountResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("result")
    val result: Result?,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)
{
    @Keep
    data class Result(
        @SerializedName("id")
        val id: Int,
        @SerializedName("bank_name")
        val bank_name: String,
        @SerializedName("account_number")
        val account_number: String,
        @SerializedName("full_name")
        val full_name: String,
        @SerializedName("swift_code")
        val swift_code: String,
        @SerializedName("routing_Number")
        val routing_Number: String,
        @SerializedName("account_type")
        val account_type: String,
        @SerializedName("wyre_wallet_id")
        val wyre_wallet_id: String?,
        @SerializedName("account_id")
        val account_id: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("payment_method_id")
        val payment_method_id: String,
        @SerializedName("bank_hash")
        val bank_hash: String,
        @SerializedName("address")
        val address: String,
        @SerializedName("is_default")
        val is_default: Boolean,
        @SerializedName("contact_number")
        val contact_number: String,
        @SerializedName("created_at")
        val created_at: String,
        @SerializedName("wallet")
        val wallet: String?,
        @SerializedName("user")
        val user: Int





 /*
    "": "paymentmethod:PA_Y6QTLXWCDMA",
    "bank_hash": "BANK-4M6dLhj6V1dlW2nteRP4qc+N4AcZ/pJrFWQPdeY8NSQ=",
    "": "India",
    "": false,
    "": "+919544843751",
    "": "2022-05-25T10:31:58.155023Z",
    "": null,
    "user": 120*/
    )
}