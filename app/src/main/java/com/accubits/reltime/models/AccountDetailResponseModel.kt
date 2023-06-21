package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import org.checkerframework.checker.units.qual.K

@Keep
data class AccountDetailResponseModel (
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("result")
    val result: AccountDetailResult? = null
)

@Keep
data class AccountDetailResult (
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("balance")
    val balance: String? = null,
    @SerializedName("coinCode")
    val coinCode: String? = null,
    @SerializedName("symbol")
    val symbol: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("created_at")
    val createdAt: Double? = null,
    @SerializedName("type")
    val type: String? = null,


    @SerializedName("account_number")
    val account_number: String? = null,
    @SerializedName("full_name")
    val full_name: String? = null,
    @SerializedName("swift_code")
    val swift_code: String? = null,
    @SerializedName("account_type")
    val account_type: String? = null,
    @SerializedName("bank_name")
    val bank_name: String? = null,
    @SerializedName("accountTypeName")
    val accountTypeName: String? = null

)


