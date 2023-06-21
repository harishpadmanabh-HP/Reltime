package com.accubits.reltime.activity.addBankAccount.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BankListResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

@Keep
data class Result(
    @SerializedName("bankNameList")
    val bankNameList: List<BankItem>
)

@Keep
data class BankItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("bankCode")
    val bankCode: String
){
    override fun toString(): String {
        return name
    }
}