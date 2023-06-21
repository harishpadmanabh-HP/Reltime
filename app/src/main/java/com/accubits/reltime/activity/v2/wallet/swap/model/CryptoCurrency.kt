package com.accubits.reltime.activity.v2.wallet.swap.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class CryptoCurrency(
    @SerializedName("id")
    val id: Int,
    @SerializedName("coinCode")
    val coinCode: String,
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("address")
    val address: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("coin_name")
    val coin_name: String,
    @SerializedName("balance")
    val balance: String,
    @SerializedName("network")
    val network: String?="",
    @SerializedName("fee")
    val fee: String?="0",
    @SerializedName("reltime_icon")
    val reltime_icon: String?,
    @SerializedName("reltime_network")
    val reltime_network: String?
) : Parcelable// ReltimeAccount(), Serializable


@Keep
data class CurrencyListResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: List<CryptoCurrency>?,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

