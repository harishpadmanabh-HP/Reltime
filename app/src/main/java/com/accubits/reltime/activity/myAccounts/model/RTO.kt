package com.accubits.reltime.activity.myAccounts.model


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
data class RTO(
    @SerializedName("balance")
    val balance: String,
    @SerializedName("current_value")
    val currentValue: String?,
    @SerializedName("coinCode")
    val coinCode: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("displayBalance")
    val displayBalance: Int,
    @SerializedName("displayText")
    val displayText: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("isToken")
    val isToken: Boolean,
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("userAddress")
    val userAddress: String,
    @SerializedName("statistics")
    val statistics: String?,
    @SerializedName("id")
    val id: Int,
) : ReltimeAccount(), Serializable