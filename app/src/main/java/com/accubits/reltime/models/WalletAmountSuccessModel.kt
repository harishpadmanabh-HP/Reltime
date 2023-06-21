package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class WalletAmountSuccessModel(
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
    var result: List<ResultWalletAmountSuccessModel>
)

@Keep
data class ResultWalletAmountSuccessModel(
    @Expose
    @SerializedName("RTO")
    var RTO: RTO?,
    @Expose
    @SerializedName("RTC")
    var RTC: RTC?
)

@Keep
data class RTC(
    @Expose
    @SerializedName("image")
    val image: String,
    @Expose
    @SerializedName("symbol")
    val symbol: String,
    @Expose
    @SerializedName("balance")
    val balance: String?,
    @Expose
    @SerializedName("isToken")
    var isToken: Boolean,
    @Expose
    @SerializedName("coinCode")
    val coinCode: String,
    @Expose
    @SerializedName("description")
    val description: String,
    @Expose
    @SerializedName("displayText")
    val displayText: String,
    @Expose
    @SerializedName("userAddress")
    val userAddress: String,
    @Expose
    @SerializedName("displayBalance")
    var displayBalance: Int
)

@Keep
data class RTO(
    @Expose
    @SerializedName("image")
    val image: String,
    @Expose
    @SerializedName("symbol")
    val symbol: String,
    @Expose
    @SerializedName("balance")
    val balance: String?,
    @Expose
    @SerializedName("isToken")
    var isToken: Boolean,
    @Expose
    @SerializedName("coinCode")
    val coinCode: String,
    @Expose
    @SerializedName("description")
    val description: String,
    @Expose
    @SerializedName("displayText")
    val displayText: String,
    @Expose
    @SerializedName("userAddress")
    val userAddress: String,
    @Expose
    @SerializedName("displayBalance")
    var displayBalance: Int
)
