package com.accubits.reltime.activity.v2.wallet.home.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class WalletHomeSuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("message")
    val message: String,
    @Expose
    @SerializedName("result")
    var result: Result?
)

@Keep
data class Result(
    @Expose
    @SerializedName("assest")
    val assest: String,
    @Expose
    @SerializedName("earnings")
    val earnings: String,
    @Expose
    @SerializedName("all_accounts")
    val allAccounts: Int,
    @Expose
    @SerializedName("accounts")
    val accounts: List<AccountItem>?
)


@Keep
data class AccountItem(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("coinCode")
    val coinCode: String,
    @Expose
    @SerializedName("balance")
    val balance: String,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("statistics")
    val statistics: String?,
    @Expose
    @SerializedName("symbol")
    val symbol: String?,
    @Expose
    @SerializedName("members")
    val members: String?,
    @Expose
    @SerializedName("type")
    val type: String?
)
