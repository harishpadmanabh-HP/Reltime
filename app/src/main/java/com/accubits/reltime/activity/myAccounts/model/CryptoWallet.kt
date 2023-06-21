package com.accubits.reltime.activity.myAccounts.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class CryptoWallet(
    @SerializedName("id")
    val id: Int,
    @SerializedName("coinCode")
    val coinCode: String,
    @SerializedName("balance")
    val balance: String,
    @SerializedName("statistics")
    val statistics: String?,
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("members")
    val members: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("address")
    val address: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("coin_name")
    val coin_name: String
) : ReltimeAccount(), Serializable